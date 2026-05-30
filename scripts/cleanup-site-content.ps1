param(
    [string] $Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"

function Test-NoisyEnglish {
    param([string] $Text)

    if ([string]::IsNullOrWhiteSpace($Text)) {
        return $false
    }

    $patterns = @(
        "\b(tu|hon|tang|tranh|tach|khoi|dau|som|vua|chay|dat|doan|dung|dang|lo|bo qua|het han|truy cap|nguy hiem|phu thuoc|dong bo|thuc te|toi uu|cai nao|dau tien|ca object|ca file|tu dau|o dau|theo lop|vong doi)\b",
        "\b(isn|isi|iafter|andn|whatam|whatn|such asng|lowhatn|reistional|goodi|whatong|question hinh|explain quyet)\b",
        "\b(varioush|varioushe|with repo nay|theo kieu|he thong ngoai|ben ngoai|nguoi|thuong|cang it|co multiple|for phep|chinh sach|nghiep vu|song con|luyen interview|understand qua|read write data|to quy|moi thu|phat hien|chan doan|su have|tu duy|nghe nghiep)\b",
        "\b(o diem nao|have ich|use to ism|to ism|should understand|such as in practice|phuc tap|sinh bug|theo order nao|o API|diem nao|khac commit|pawhatnation|question do|store y|callback hell|stuff pattern)\b",
        "\bis it\?",
        "This concept\s+matters\s+because.*Java backend code behaves",
        "A complete answer\s+should.*happy path",
        "People often go wrong.*endpoint.*happy path",
        "People often go wrong.*test.*happy path",
        "Code only dep o happy path"
    )

    foreach ($pattern in $patterns) {
        if ($Text -match $pattern) {
            return $true
        }
    }

    return $false
}

function Get-EnglishPool {
    param(
        [string] $TopicTitle,
        [string] $Field
    )

    $topic = if ([string]::IsNullOrWhiteSpace($TopicTitle)) { "this Java backend topic" } else { $TopicTitle.Trim() }

    $pools = @{
        question = @(
            "How would you explain $topic in practical Java backend terms?",
            "What should a developer verify when applying $topic in real code?",
            "What mistake should you avoid when working with $topic?"
        )
        answer = @(
            "Start from the real behavior: what input enters the code, what state changes, and what result should be observable.",
            "Connect the concept to the layer where it appears, such as controller flow, service rules, persistence, messaging, security, or runtime behavior.",
            "A useful answer includes the normal case, one failure case, and how you would prove the behavior with a test, log, or small reproduction.",
            "Do not stop at the definition; explain what breaks when the concept is misunderstood in a backend application."
        )
        explanation = @(
            "The practical way to learn this is to follow one request or code path and name the boundary where the behavior changes.",
            "Small examples matter because they expose assumptions about null values, object state, transactions, concurrency, or external calls.",
            "When the result is surprising, inspect the evidence first: stack trace, SQL, logs, request payload, thread behavior, or configuration."
        )
        apply = @(
            "Use this when reviewing code, debugging production-like behavior, or explaining why one design is safer than another.",
            "Common mistakes come from testing only the easiest case and skipping invalid input, duplicate requests, timeout, rollback, or permission checks.",
            "The safest habit is to tie the concept to an observable signal: a focused test, a clear log, a metric, or a reproducible example."
        )
        practice = @(
            "Write a small Java example that demonstrates both the expected path and one broken path.",
            "Add a focused test around the behavior, then change one input to confirm the test catches the important failure.",
            "Explain the idea in three parts: the rule, a realistic backend example, and the pitfall you would watch for in review."
        )
    }

    return $pools[$Field]
}

function Repair-Array {
    param(
        [object[]] $Items,
        [string] $TopicTitle,
        [ValidateSet("answer", "explanation", "apply", "practice")] [string] $Field,
        [int] $Minimum
    )

    $clean = New-Object System.Collections.Generic.List[string]
    foreach ($item in @($Items)) {
        $text = [string] $item
        if ([string]::IsNullOrWhiteSpace($text)) {
            continue
        }

        if (-not (Test-NoisyEnglish $text) -and -not $clean.Contains($text.Trim())) {
            $clean.Add($text.Trim())
        }
    }

    foreach ($candidate in (Get-EnglishPool $TopicTitle $Field)) {
        if ($clean.Count -ge $Minimum) {
            break
        }
        if (-not $clean.Contains($candidate)) {
            $clean.Add($candidate)
        }
    }

    return @($clean)
}

function Repair-EnglishQuestionBank {
    param([string] $Path)

    $bank = Get-Content -Raw -Encoding UTF8 $Path | ConvertFrom-Json
    $changed = 0

    foreach ($topic in @($bank.topics)) {
        $topicTitle = [string] $topic.title

        foreach ($question in @($topic.questions)) {
            if (Test-NoisyEnglish ([string] $question.question)) {
                $question.question = (Get-EnglishPool $topicTitle "question")[0]
                $changed++
            }

            $answer = Repair-Array @($question.answer) $topicTitle "answer" 4
            if (($answer -join "`n") -ne (@($question.answer) -join "`n")) {
                $question.answer = $answer
                $changed++
            }

            $explanation = Repair-Array @($question.explanation) $topicTitle "explanation" 3
            if (($explanation -join "`n") -ne (@($question.explanation) -join "`n")) {
                $question.explanation = $explanation
                $changed++
            }

            $apply = Repair-Array @($question.applyOrPitfalls) $topicTitle "apply" 3
            if (($apply -join "`n") -ne (@($question.applyOrPitfalls) -join "`n")) {
                $question.applyOrPitfalls = $apply
                $changed++
            }

            $practice = Repair-Array @($question.practice) $topicTitle "practice" 3
            if (($practice -join "`n") -ne (@($question.practice) -join "`n")) {
                $question.practice = $practice
                $changed++
            }

            if (Test-NoisyEnglish ([string] $question.answerShort)) {
                $question.answerShort = [string] @($question.answer)[0]
                $changed++
            }
        }
    }

    $json = $bank | ConvertTo-Json -Depth 100
    [System.IO.File]::WriteAllText($Path, $json, [System.Text.UTF8Encoding]::new($false))
    return $changed
}

$enPath = Join-Path $Root "docs/data/content/question-bank.en.json"
$enChanged = Repair-EnglishQuestionBank $enPath

"Cleaned English question bank changes: $enChanged"
