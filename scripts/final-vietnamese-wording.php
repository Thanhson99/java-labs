<?php

declare(strict_types=1);

$files = [
    'docs/data/content/question-bank.vi.json',
    'docs/data/content/question-bank.json',
];

foreach ($files as $file) {
    $text = (string) file_get_contents($file);
    $text = preg_replace('/Pitfall chính là trả lời quá chung mà không chỉ ra case lỗi cụ thể trong/u', 'Điểm dễ sai là trả lời quá chung mà không đưa ra tình huống lỗi cụ thể trong', $text) ?? $text;
    $text = preg_replace('/Pitfall chính của/u', 'Điểm dễ sai trong', $text) ?? $text;
    $text = str_replace('case lỗi', 'tình huống lỗi', $text);
    $text = str_replace('case biên', 'tình huống biên', $text);
    $text = str_replace('trust ranh giới', 'ranh giới tin cậy', $text);
    $text = str_replace('flow gồm input', 'luồng gồm input', $text);
    $text = str_replace('flow chạy thật', 'luồng chạy thật', $text);
    file_put_contents($file, $text);
}

echo "Final Vietnamese wording cleanup complete.\n";
