<?php

declare(strict_types=1);

$pairs = [
    'One loai endpoint' => 'One endpoint type',
    'Tin rang production luon on' => 'Assume production is always fine',
    'Test HTTP behavior of controller' => 'Test controller HTTP behavior',
    'Write a test for the happy path and one error case' => 'Write a test for the main success path and one error case',
    'Viết test cho happy path và một case lỗi' => 'Viết test cho luồng thành công và một trường hợp lỗi',
    'web flow' => 'luồng web',
    'integration flow' => 'luồng tích hợp',
    'Async chain rối khó debug' => 'Chuỗi async rối và khó debug',
    'Test HTTP behavior của controller' => 'Kiểm tra hành vi HTTP của controller',
];

$files = [
    'docs/data/quizzes/quiz-bank.en.json',
    'docs/data/quizzes/quiz-bank.vi.json',
    'docs/data/quizzes/quiz-bank.json',
    'docs/data/roadmap/backend-roadmap.en.json',
    'docs/data/roadmap/backend-roadmap.vi.json',
    'docs/data/roadmap/backend-roadmap.json',
];

foreach ($files as $file) {
    $text = (string) file_get_contents($file);
    $text = str_replace(array_keys($pairs), array_values($pairs), $text);
    file_put_contents($file, $text);
}

echo "Small quiz and roadmap copy cleaned.\n";
