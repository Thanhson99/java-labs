param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("basic", "spring")]
    [string]$Mode
)

$ErrorActionPreference = "Stop"
$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$DefaultPort = if ($env:PORT) { [int]$env:PORT } else { 8089 }
$DesiredJavaVersion = if ($env:JAVA_VERSION) { $env:JAVA_VERSION } else { "17" }

function Import-EnvFile {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path $Path)) {
        return
    }

    Write-Host "Loading env from $Path"

    Get-Content $Path | ForEach-Object {
        $line = $_.Trim()
        if (-not $line -or $line.StartsWith("#")) {
            return
        }

        $parts = $line -split "=", 2
        if ($parts.Count -ne 2) {
            return
        }

        $name = $parts[0].Trim()
        $value = $parts[1].Trim().Trim('"').Trim("'")
        [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
        Set-Item -Path "Env:$name" -Value $value
    }
}

function Get-JavaMajorVersion {
    param(
        [Parameter(Mandatory = $true)]
        [string]$JavaExe
    )

    $versionOutput = & $JavaExe -version 2>&1 | Select-Object -First 1
    if ($versionOutput -match '"([^"]+)"') {
        $version = $Matches[1]
        if ($version.StartsWith("1.")) {
            return ($version.Split(".")[1])
        }
        return ($version.Split(".")[0])
    }

    return $null
}

function Use-JavaVersion {
    param(
        [Parameter(Mandatory = $true)]
        [string]$DesiredMajor
    )

    $candidates = New-Object System.Collections.Generic.List[string]

    if ($env:JAVA_HOME) {
        $javaFromHome = Join-Path $env:JAVA_HOME "bin\java.exe"
        if (Test-Path $javaFromHome) {
            $candidates.Add($javaFromHome)
        }
    }

    try {
        $javaCmd = (Get-Command java -ErrorAction Stop).Source
        if ($javaCmd) {
            $candidates.Add($javaCmd)
        }
    }
    catch {
    }

    $searchRoots = @(
        "C:\Program Files\Java",
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\Microsoft",
        "C:\Program Files\Zulu",
        "C:\Program Files\Amazon Corretto"
    )

    foreach ($root in $searchRoots) {
        if (Test-Path $root) {
            Get-ChildItem -Path $root -Recurse -Filter java.exe -ErrorAction SilentlyContinue |
                Where-Object { $_.FullName -like "*\bin\java.exe" } |
                ForEach-Object { $candidates.Add($_.FullName) }
        }
    }

    foreach ($candidate in ($candidates | Select-Object -Unique)) {
        $major = Get-JavaMajorVersion -JavaExe $candidate
        if ($major -eq $DesiredMajor) {
            $resolvedJavaHome = Split-Path (Split-Path $candidate -Parent) -Parent
            $env:JAVA_HOME = $resolvedJavaHome
            $env:PATH = "$resolvedJavaHome\bin;$env:PATH"
            Write-Host "Using Java $DesiredMajor from $resolvedJavaHome"
            return
        }
    }

    throw "Could not find JDK $DesiredMajor. Install it or run with JAVA_VERSION=<installed-version>."
}

function Test-PortFree {
    param(
        [Parameter(Mandatory = $true)]
        [int]$Port
    )

    $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, $Port)
    try {
        $listener.Start()
        return $true
    }
    catch {
        return $false
    }
    finally {
        try {
            $listener.Stop()
        }
        catch {
        }
    }
}

function Get-FreePort {
    param(
        [Parameter(Mandatory = $true)]
        [int]$StartPort
    )

    for ($port = $StartPort; $port -lt ($StartPort + 50); $port++) {
        if (Test-PortFree -Port $port) {
            return $port
        }
    }

    throw "Could not find a free port starting from $StartPort."
}

function Run-Basic {
    Use-JavaVersion -DesiredMajor $DesiredJavaVersion
    Push-Location (Join-Path $RootDir "basic")
    try {
        & .\mvnw.cmd -q -DskipTests compile
        & java -cp target/classes com.example.javalabs.basic.LearningApp
    }
    finally {
        Pop-Location
    }
}

function Run-Spring {
    Use-JavaVersion -DesiredMajor $DesiredJavaVersion
    $SelectedPort = Get-FreePort -StartPort $DefaultPort
    $env:PORT = [string]$SelectedPort

    Push-Location (Join-Path $RootDir "spring")
    try {
        Write-Host "Starting Spring on http://localhost:$SelectedPort/"

        $job = Start-Job -ArgumentList $SelectedPort -ScriptBlock {
            param([int]$PortNumber)
            $url = "http://localhost:$PortNumber/"

            for ($i = 0; $i -lt 60; $i++) {
                try {
                    Invoke-WebRequest -Uri $url -UseBasicParsing | Out-Null
                    Start-Process $url
                    return
                }
                catch {
                    Start-Sleep -Seconds 1
                }
            }
        }

        try {
            & .\mvnw.cmd spring-boot:run
        }
        finally {
            Stop-Job $job -ErrorAction SilentlyContinue | Out-Null
            Remove-Job $job -Force -ErrorAction SilentlyContinue | Out-Null
        }
    }
    finally {
        Pop-Location
    }
}

Import-EnvFile -Path (Join-Path $RootDir ".env")
Import-EnvFile -Path (Join-Path $RootDir "spring\.env")

switch ($Mode) {
    "basic" { Run-Basic }
    "spring" { Run-Spring }
}
