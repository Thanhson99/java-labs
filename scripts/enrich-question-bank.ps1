param(
    [string] $Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"

$cleanupScript = Join-Path $PSScriptRoot "cleanup-site-content.ps1"
if (-not (Test-Path -LiteralPath $cleanupScript)) {
    throw "Missing cleanup script: $cleanupScript"
}

Write-Host "Content enrichment now uses the natural cleanup pipeline."
& $cleanupScript -Root $Root
