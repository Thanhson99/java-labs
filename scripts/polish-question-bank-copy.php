<?php

declare(strict_types=1);

$root = dirname(__DIR__);

function loadJson(string $path): array
{
    $data = json_decode((string) file_get_contents($path), true);
    if (!is_array($data)) {
        throw new RuntimeException("Invalid JSON: $path");
    }
    return $data;
}

function saveJson(string $path, array $data): void
{
    file_put_contents(
        $path,
        json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . PHP_EOL
    );
}

function shortLabel(string $text): string
{
    $text = trim(preg_replace('/\s+/', ' ', $text) ?? '');
    return mb_strlen($text) > 74 ? rtrim(mb_substr($text, 0, 73)) . '…' : $text;
}

function polishLine(string $line, string $lang): string
{
    $pairs = $lang === 'vi'
        ? [
            'behavior' => 'hành vi',
            'boundary' => 'ranh giới',
            'runtime flow' => 'luồng chạy thực tế',
            'Thêm một input sai' => 'Thêm một tình huống biên',
            'thêm một input sai' => 'thêm một tình huống biên',
            'đặt tên test theo behavior cần bảo vệ' => 'đặt tên test theo hành vi cần bảo vệ',
            'Phần giải thích nên lần theo flow chạy thật' => 'Phần giải thích nên bám vào luồng code thực tế',
            'Một cách trả lời tự nhiên là nêu định nghĩa ngắn, sau đó dùng' => 'Sau định nghĩa ngắn, hãy dùng',
            'để dẫn vào ví dụ thực tế' => 'để dẫn vào một ví dụ thực tế',
        ]
        : [
            'then add one bad input and observe the result' => 'then add one edge case and explain the result',
            'The explanation should follow the runtime flow' => 'The explanation should follow the actual code path',
            'should follow the runtime flow' => 'should follow the actual code path',
            'A natural answer gives a short definition, then uses' => 'After the short definition, use',
            'to introduce a realistic example' => 'to introduce a realistic example',
            'name the test after the behavior it protects' => 'name the test after the behavior it protects',
        ];

    return str_replace(array_keys($pairs), array_values($pairs), $line);
}

function fallbackLine(string $lang, string $field, string $question, string $topic): string
{
    $q = shortLabel($question);
    $topic = shortLabel($topic);

    if ($lang === 'vi') {
        return match ($field) {
            'explanation' => "Với \"$q\", hãy giải thích bằng một ví dụ đủ nhỏ để người học thấy được luồng chạy.",
            'applyOrPitfalls' => "Khi áp dụng \"$q\" trong $topic, hãy chỉ rõ rủi ro nếu hiểu sai.",
            'practice' => "Tạo một biến thể nhỏ của \"$q\" và tự dự đoán kết quả trước khi chạy.",
            default => "Với \"$q\", hãy nối quy tắc với một tình huống code cụ thể.",
        };
    }

    return match ($field) {
        'explanation' => "For \"$q\", explain it with a small enough example that the execution path is visible.",
        'applyOrPitfalls' => "When applying \"$q\" in $topic, name the risk of misunderstanding it.",
        'practice' => "Create a small variation of \"$q\" and predict the result before running it.",
        default => "For \"$q\", connect the rule to a concrete code situation.",
    };
}

function polishFile(string $path, string $lang): int
{
    $bank = loadJson($path);
    $changed = 0;

    foreach ($bank['topics'] as &$topic) {
        $topicTitle = (string) ($topic['title'] ?? 'Java backend');

        foreach ($topic['questions'] as &$question) {
            $questionTitle = (string) ($question['question'] ?? $topicTitle);
            $answerSet = [];
            foreach (($question['answer'] ?? []) as $line) {
                if (is_string($line)) {
                    $answerSet[$line] = true;
                }
            }

            foreach (['answer', 'explanation', 'applyOrPitfalls', 'practice'] as $field) {
                if (!isset($question[$field]) || !is_array($question[$field])) {
                    continue;
                }

                $next = [];
                $seen = [];
                foreach ($question[$field] as $line) {
                    if (!is_string($line) || trim($line) === '') {
                        continue;
                    }

                    $candidate = polishLine($line, $lang);
                    if ($field !== 'answer' && isset($answerSet[$candidate])) {
                        $candidate = fallbackLine($lang, $field, $questionTitle, $topicTitle);
                    }

                    if (!isset($seen[$candidate])) {
                        $next[] = $candidate;
                        $seen[$candidate] = true;
                    }
                }

                if ($next !== $question[$field]) {
                    $question[$field] = $next;
                    $changed++;
                }
            }

            if (isset($question['answerShort']) && is_string($question['answerShort'])) {
                $short = polishLine($question['answerShort'], $lang);
                if ($short !== $question['answerShort']) {
                    $question['answerShort'] = $short;
                    $changed++;
                }
            }
        }
        unset($question);
    }
    unset($topic);

    saveJson($path, $bank);
    return $changed;
}

$en = $root . '/docs/data/content/question-bank.en.json';
$vi = $root . '/docs/data/content/question-bank.vi.json';
$fallback = $root . '/docs/data/content/question-bank.json';

$enChanged = polishFile($en, 'en');
$viChanged = polishFile($vi, 'vi');
copy($vi, $fallback);

echo "Polished English question copy: {$enChanged}\n";
echo "Polished Vietnamese question copy: {$viChanged}\n";
echo "Synced fallback question bank from Vietnamese source.\n";
