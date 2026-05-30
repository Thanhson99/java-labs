<?php

declare(strict_types=1);

$root = dirname(__DIR__);
$fields = ['answer', 'explanation', 'applyOrPitfalls', 'practice'];

function readBank(string $path): array
{
    $data = json_decode((string) file_get_contents($path), true);
    if (!is_array($data)) {
        throw new RuntimeException("Invalid JSON: $path");
    }
    return $data;
}

function writeBank(string $path, array $bank): void
{
    file_put_contents(
        $path,
        json_encode($bank, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . PHP_EOL
    );
}

function shortText(string $text, int $limit = 86): string
{
    $text = trim(preg_replace('/\s+/', ' ', strip_tags($text)) ?? '');
    $text = str_replace(["\n", "\r", '"'], [' ', ' ', "'"], $text);
    if (mb_strlen($text) <= $limit) {
        return $text;
    }
    return rtrim(mb_substr($text, 0, $limit - 1)) . '…';
}

function countLines(array $bank, array $fields): array
{
    $counts = [];
    foreach ($bank['topics'] as $topic) {
        foreach ($topic['questions'] as $question) {
            foreach ($fields as $field) {
                foreach (($question[$field] ?? []) as $line) {
                    if (is_string($line) && trim($line) !== '') {
                        $counts[$line] = ($counts[$line] ?? 0) + 1;
                    }
                }
            }
        }
    }
    return $counts;
}

function replacementLine(string $lang, string $field, string $question, string $topic, int $slot): string
{
    $q = shortText($question);
    $topic = shortText($topic, 60);

    if ($lang === 'vi') {
        $templates = [
            'answer' => [
                "Với câu \"$q\", hãy bắt đầu từ rule chính rồi nối ngay vào một tình huống code cụ thể.",
                "Trong chủ đề $topic, câu trả lời nên chỉ rõ behavior nào thay đổi và vì sao thay đổi đó quan trọng.",
                "Một cách trả lời tự nhiên là nêu định nghĩa ngắn, sau đó dùng \"$q\" để dẫn vào ví dụ thực tế.",
            ],
            'explanation' => [
                "Hãy kiểm chứng \"$q\" bằng một ví dụ nhỏ trước khi kết luận, nhất là khi có state, null hoặc external call.",
                "Phần giải thích cho \"$q\" nên lần theo flow chạy thật thay vì chỉ nhắc lại thuật ngữ của $topic.",
                "Nếu người học còn mơ hồ về \"$q\", hãy hỏi họ dự đoán output hoặc lỗi xảy ra ở bước nào.",
            ],
            'applyOrPitfalls' => [
                "Áp dụng khi review phần code liên quan tới \"$q\" và cần chứng minh behavior bằng test hoặc log.",
                "Điểm dễ sai trong \"$q\" là trả lời quá chung mà không đưa ra tình huống lỗi cụ thể trong $topic.",
                "Với \"$q\", nên kiểm tra case biên, dữ liệu sai và cách hệ thống báo lỗi trước khi coi đáp án là đủ.",
            ],
            'practice' => [
                "Biến \"$q\" thành một ví dụ chạy được, rồi thêm một input sai để xem hệ thống phản ứng.",
                "Viết một test nhỏ cho \"$q\" và đặt tên test theo behavior cần bảo vệ.",
                "Tự giải thích lại \"$q\" bằng một flow gồm input, xử lý, output và lỗi có thể xảy ra.",
            ],
        ];
        return $templates[$field][$slot % count($templates[$field])];
    }

    $templates = [
        'answer' => [
            "For \"$q\", start with the main rule and immediately connect it to a concrete code path.",
            "In $topic, the answer should name which behavior changes and why that change matters.",
            "A natural answer gives a short definition, then uses \"$q\" to introduce a realistic example.",
        ],
        'explanation' => [
            "Verify \"$q\" with a small example before generalizing, especially when state, nulls, or external calls are involved.",
            "The explanation for \"$q\" should follow the runtime flow instead of repeating the terminology from $topic.",
            "If \"$q\" still feels vague, ask where the output changes or where the failure first appears.",
        ],
        'applyOrPitfalls' => [
            "Use this when reviewing code related to \"$q\" and you need evidence from a test or log.",
            "The main risk in \"$q\" is giving a generic answer without naming a concrete failure case in $topic.",
            "For \"$q\", check edge cases, invalid data, and error reporting before treating the answer as complete.",
        ],
        'practice' => [
            "Turn \"$q\" into a runnable example, then add one bad input and observe the result.",
            "Write a small test for \"$q\" and name the test after the behavior it protects.",
            "Explain \"$q\" as a flow: input, processing, output, and the failure that could appear.",
        ],
    ];
    return $templates[$field][$slot % count($templates[$field])];
}

function personalizeFile(string $path, string $lang, array $fields, int $threshold): int
{
    $bank = readBank($path);
    $counts = countLines($bank, $fields);
    $changed = 0;

    foreach ($bank['topics'] as &$topic) {
        $topicTitle = (string) ($topic['title'] ?? 'Java backend');
        foreach ($topic['questions'] as &$question) {
            $questionTitle = (string) ($question['question'] ?? $topicTitle);
            foreach ($fields as $field) {
                if (!isset($question[$field]) || !is_array($question[$field])) {
                    continue;
                }

                $next = [];
                $seen = [];
                foreach ($question[$field] as $index => $line) {
                    if (!is_string($line) || trim($line) === '') {
                        continue;
                    }

                    $candidate = $line;
                    if (($counts[$line] ?? 0) > $threshold) {
                        $candidate = replacementLine($lang, $field, $questionTitle, $topicTitle, $index);
                        $changed++;
                    }

                    if (!isset($seen[$candidate])) {
                        $next[] = $candidate;
                        $seen[$candidate] = true;
                    }
                }
                $question[$field] = $next;
            }

            if (isset($question['answerShort']) && ($counts[$question['answerShort']] ?? 0) > $threshold) {
                $question['answerShort'] = $question['answer'][0] ?? $question['answerShort'];
                $changed++;
            }
        }
        unset($question);
    }
    unset($topic);

    writeBank($path, $bank);
    return $changed;
}

$en = $root . '/docs/data/content/question-bank.en.json';
$vi = $root . '/docs/data/content/question-bank.vi.json';
$fallback = $root . '/docs/data/content/question-bank.json';

$enChanged = personalizeFile($en, 'en', $fields, 10);
$viChanged = personalizeFile($vi, 'vi', $fields, 10);
copy($vi, $fallback);

echo "Personalized repeated English lines: {$enChanged}\n";
echo "Personalized repeated Vietnamese lines: {$viChanged}\n";
echo "Synced fallback question bank from Vietnamese source.\n";
