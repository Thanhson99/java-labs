<?php

declare(strict_types=1);

$root = dirname(__DIR__);

function readJsonFile(string $path): array
{
    $json = file_get_contents($path);
    if ($json === false) {
        throw new RuntimeException("Cannot read $path");
    }

    $data = json_decode($json, true);
    if (!is_array($data)) {
        throw new RuntimeException("Invalid JSON: $path");
    }

    return $data;
}

function writeJsonFile(string $path, array $data): void
{
    $json = json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    if ($json === false) {
        throw new RuntimeException("Cannot encode $path");
    }

    file_put_contents($path, $json . PHP_EOL);
}

function categoryForTopic(array $topic): string
{
    $source = strtolower(($topic['id'] ?? '') . ' ' . ($topic['title'] ?? '') . ' ' . ($topic['track'] ?? ''));

    return match (true) {
        str_contains($source, 'spring') || str_contains($source, 'bean') => 'spring',
        str_contains($source, 'jpa') || str_contains($source, 'sql') || str_contains($source, 'database') || str_contains($source, 'transaction') => 'data',
        str_contains($source, 'security') || str_contains($source, 'jwt') || str_contains($source, 'auth') => 'security',
        str_contains($source, 'concurrency') || str_contains($source, 'thread') || str_contains($source, 'async') => 'concurrency',
        str_contains($source, 'testing') || str_contains($source, 'maven') || str_contains($source, 'build') => 'testing',
        str_contains($source, 'messaging') || str_contains($source, 'kafka') || str_contains($source, 'rabbit') || str_contains($source, 'microservice') => 'messaging',
        str_contains($source, 'http') || str_contains($source, 'rest') || str_contains($source, 'api') => 'web',
        str_contains($source, 'observability') || str_contains($source, 'reliability') || str_contains($source, 'operations') => 'ops',
        str_contains($source, 'file') || str_contains($source, 'io') || str_contains($source, 'network') => 'io',
        str_contains($source, 'pattern') || str_contains($source, 'architecture') || str_contains($source, 'design') => 'architecture',
        str_contains($source, 'interview') || str_contains($source, 'problem') => 'interview',
        default => 'core',
    };
}

function repeatedLines(string $lang): array
{
    if ($lang === 'vi') {
        return [
            'Viết một ví dụ Java tối thiểu cho câu hỏi này, chạy thử, rồi đổi một input để quan sát behavior thay đổi.',
            'Khi học phần này, hãy viết một ví dụ Java rất nhỏ và dự đoán kết quả trước khi chạy. Thói quen này giúp kiến thức cụ thể hơn thay vì học thuộc.',
            'Khi review code, hãy nhìn boundary nơi khái niệm được dùng: compile time, runtime, request handling, database access, concurrency hoặc deployment.',
            'Viết một failing test hoặc reproduction case trước, sau đó sửa implementation và giải thích vì sao fix đó đúng.',
            'Tự trả lời trong 60 giây theo cấu trúc: định nghĩa, ví dụ, pitfall và liên hệ production.',
            'Tìm một case tương tự trong project Java/Spring Boot và ghi lại bạn sẽ test nó như thế nào.',
            'Khi trả lời phỏng vấn, giữ cấu trúc rõ: định nghĩa ngắn, ví dụ thực tế, lỗi hay gặp và cách bạn verify hoặc debug.',
            'Khi áp dụng vào công việc thật, cũng nên nói tới vận hành: bạn sẽ log gì, test gì, metric hoặc symptom nào cho thấy có vấn đề và làm sao giữ code dễ bảo trì.',
            'Điểm quan trọng là hiểu behavior thật, không chỉ nhớ tên khái niệm.',
            'Trong hệ thống Java backend, câu trả lời tốt thường nối quy tắc ngôn ngữ với service boundary, tính nhất quán dữ liệu, xử lý lỗi, observability và maintainability của team.',
            'Áp dụng khi đọc code production, review pull request, giải thích bug hoặc chọn giữa hai cách implement.',
            'Dễ sai khi chỉ nhớ thuật ngữ nhưng không giải thích được điều gì thay đổi trong memory, runtime flow, SQL behavior, HTTP behavior hoặc maintainability của team.',
            'Khi học, hãy nối định nghĩa với trách nhiệm của backend service: validate input, business rule, nhất quán dữ liệu, observability và xử lý failure.',
            'Trong production, câu hỏi quan trọng không chỉ là code chạy một lần, mà là nó có dễ hiểu, dễ test, dễ quan sát và an toàn dưới traffic thật không.',
            'Áp dụng khi bạn cần đọc code cũ, giải thích cho người khác hoặc quyết định chọn giải pháp nào trong backend Java.',
            'Dễ sai khi chỉ nhớ định nghĩa mà không nối nó với hành vi runtime, dữ liệu thật và trade-off kỹ thuật.',
        ];
    }

    return [
        'Write a minimal Java example for this question, run it, then change one input to see how the behavior changes.',
        'A good way to learn it is to write a tiny example, predict the result, and then run it.',
        'When reading code, follow how data moves through methods, objects, database calls, or request boundaries.',
        'Explain the answer out loud in 60 seconds using: definition, example, pitfall, and production relevance.',
        'Write one failing test or reproduction case first, then fix the implementation and explain why the fix works.',
        'Find one similar case in a Spring Boot or Java backend project and write a note about how you would test it.',
        'The key is to understand the actual behavior, not only the name of the concept.',
        'If the result is different from what you expected, inspect the assumption before changing the code.',
        'In a real project, look at the input, output, state changes, and failure points.',
        'Keep the explanation close to the code path so it feels practical instead of abstract.',
        'Use this knowledge when reading production code, reviewing pull requests, explaining a bug, or choosing between two implementation approaches.',
        'People often go wrong when they memorize the term but cannot explain what changes in memory, runtime flow, SQL behavior, HTTP behavior, or team maintainability.',
        'For learning, connect the definition to the responsibility of a backend service: input validation, business rules, data consistency, observability, and failure handling.',
        'In production, the important question is not only whether the code works once, but whether it remains understandable, testable, observable, and safe under real traffic.',
        'People often go wrong when they memorize a definition but never connect it to runtime behavior, real data, and technical trade-offs.',
        'Use this when you read older code, explain the idea to someone else, or choose between backend design options.',
    ];
}

function copyPools(string $lang): array
{
    if ($lang === 'vi') {
        return [
            'core' => [
                'answer' => [
                    'Hãy giải thích bằng một ví dụ Java nhỏ trước, rồi mới nói tới quy tắc tổng quát.',
                    'Điểm cần nắm là giá trị, reference, scope và thứ tự chạy thay đổi như thế nào qua từng dòng.',
                    'Khi đọc code, hãy hỏi dữ liệu đang nằm ở đâu và dòng tiếp theo có thể làm thay đổi state nào.',
                ],
                'explanation' => [
                    'Java Core dễ học sai nếu chỉ nhớ cú pháp; nên chạy ví dụ ngắn để thấy behavior thật.',
                    'Những lỗi như null, equality, collection mutation hoặc autoboxing thường chỉ lộ khi bạn thử case biên.',
                    'Một câu trả lời tốt nên chỉ ra được cả rule và ví dụ phản chứng.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi review DTO, validation, collection, string comparison hoặc logic rẽ nhánh.',
                    'Dễ sai khi đoán theo cảm giác mà không chạy thử case nhỏ.',
                    'Nên kiểm tra null, empty value và object identity trước khi kết luận.',
                ],
                'practice' => [
                    'Tạo một class nhỏ, in kết quả từng bước, rồi đổi input để thấy rule hoạt động.',
                    'Viết một test cho case đúng và một test cho case dễ nhầm.',
                    'Tự giải thích lại bằng ví dụ có biến, object và output cụ thể.',
                ],
            ],
            'spring' => [
                'answer' => [
                    'Nên nối khái niệm với flow thật: controller nhận request, service xử lý rule, repository làm việc với dữ liệu.',
                    'Điểm quan trọng là phân biệt phần Java tự làm và phần Spring container quản lý.',
                    'Một câu trả lời tốt nói rõ annotation giúp gì, nhưng cũng chỉ ra boundary mà annotation không thay thế được.',
                ],
                'explanation' => [
                    'Spring dễ gây ảo giác vì code chạy được dù boundary thiết kế chưa rõ.',
                    'Hãy nhìn dependency, bean lifecycle, transaction boundary và trách nhiệm từng layer.',
                    'Nếu khó test service, thường có dấu hiệu controller hoặc framework đang kéo quá nhiều logic.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi tách controller, service, repository, configuration và test slice.',
                    'Dễ sai khi lạm dụng annotation thay cho thiết kế rõ ràng.',
                    'Cẩn thận với circular dependency, self-invocation và transaction đặt sai tầng.',
                ],
                'practice' => [
                    'Trace một request từ controller tới repository và ghi lại trách nhiệm từng bước.',
                    'Viết test cho service mà không cần boot toàn bộ ứng dụng nếu có thể.',
                    'Thử bỏ một annotation hoặc đổi scope bean để hiểu Spring đang làm gì.',
                ],
            ],
            'data' => [
                'answer' => [
                    'Câu trả lời nên nối Java code với SQL thật, transaction boundary và dữ liệu đủ lớn.',
                    'Đúng chức năng chưa đủ; cần biết query chạy bao nhiêu lần và lock diễn ra ở đâu.',
                    'Với dữ liệu, hãy nói cả tính đúng, hiệu năng và khả năng rollback.',
                ],
                'explanation' => [
                    'Nhiều lỗi data không nằm ở syntax Java mà nằm ở query, index, isolation hoặc migration.',
                    'ORM giúp viết nhanh hơn nhưng không thay thế việc đọc SQL/log khi debug.',
                    'Transaction cần được hiểu như một boundary nghiệp vụ, không chỉ là annotation.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi viết repository, thiết kế query, migrate schema hoặc xử lý consistency.',
                    'Dễ sai khi chỉ nhìn entity mà không kiểm tra SQL thật.',
                    'Luôn chú ý N+1, missing index, long transaction và rollback không như mong đợi.',
                ],
                'practice' => [
                    'Bật SQL log cho một use case và đếm số query thật sự chạy.',
                    'Tạo dữ liệu mẫu đủ lớn để thấy khác biệt giữa query tốt và query kém.',
                    'Viết một test transaction cho cả case commit và rollback.',
                ],
            ],
            'security' => [
                'answer' => [
                    'Bắt đầu từ trust boundary: ai gửi request, identity được chứng minh bằng gì và permission được kiểm tra ở đâu.',
                    'Authentication trả lời người dùng là ai; authorization trả lời họ được phép làm gì.',
                    'Một flow bảo mật tốt cần nói cả token hợp lệ, token lỗi, hết hạn và thu hồi.',
                ],
                'explanation' => [
                    'Security thường hỏng ở case phụ: token hết hạn, replay, quyền thiếu hoặc log lộ dữ liệu.',
                    'Không nên tin input từ client chỉ vì request đã đi qua bước đăng nhập.',
                    'Thiết kế bảo mật phải dễ audit và dễ debug khi có sự cố.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi thiết kế login, refresh token, protected endpoint và role-based access.',
                    'Dễ sai khi chỉ test luồng đăng nhập thành công.',
                    'Cần kiểm tra lỗi quyền, token cũ, token bị dùng lại và dữ liệu nhạy cảm trong log.',
                ],
                'practice' => [
                    'Viết checklist cho một endpoint protected: identity, permission, input, audit log và error response.',
                    'Test một request thiếu token, token hết hạn và token đúng nhưng thiếu quyền.',
                    'Trace refresh-token flow từ controller tới nơi lưu token.',
                ],
            ],
            'concurrency' => [
                'answer' => [
                    'Hãy xác định state nào được chia sẻ, ai đọc/ghi state đó và thứ tự chạy có ảnh hưởng kết quả không.',
                    'Concurrency không chỉ là chạy nhanh hơn; nó còn là safety, ordering, timeout và cancellation.',
                    'Một câu trả lời tốt phải nói được bug sẽ lộ như thế nào khi nhiều luồng chạy cùng lúc.',
                ],
                'explanation' => [
                    'Bug concurrency thường khó tái hiện vì phụ thuộc timing và tải hệ thống.',
                    'Giảm shared mutable state thường hiệu quả hơn thêm lock một cách máy móc.',
                    'Khi async flow dài, đặt tên bước xử lý và gom error handling rõ ràng.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng cho batch, background job, thread pool, CompletableFuture và shared cache.',
                    'Dễ sai khi code chạy đúng local nhưng chưa từng thử tải hoặc concurrent access.',
                    'Cẩn thận lost update, deadlock, retry storm và task không được cancel.',
                ],
                'practice' => [
                    'Viết một ví dụ race condition nhỏ rồi sửa bằng cách giảm shared state hoặc đồng bộ đúng chỗ.',
                    'Đo thời gian trước và sau khi thêm concurrency để tránh tối ưu cảm tính.',
                    'Thử timeout/cancel một task để xem hệ thống cleanup tài nguyên thế nào.',
                ],
            ],
            'testing' => [
                'answer' => [
                    'Test tốt mô tả behavior người dùng hoặc hệ thống cần giữ, không chỉ gọi method cho đủ coverage.',
                    'Nên tách unit test, integration test và regression test theo rủi ro thật.',
                    'Một test đáng tin phải fail khi behavior quan trọng bị phá vỡ.',
                ],
                'explanation' => [
                    'Mock giúp cô lập dependency, nhưng mock quá nhiều làm test bám implementation.',
                    'Regression test nên tái hiện bug trước khi sửa để tránh lỗi quay lại.',
                    'Build tool và CI giúp biến thói quen kiểm tra thành một phần của workflow.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi sửa bug, refactor service hoặc thay đổi contract API.',
                    'Dễ sai khi test chỉ đi qua case đẹp và dữ liệu quá sạch.',
                    'Nên ưu tiên test behavior có rủi ro business hoặc production cao.',
                ],
                'practice' => [
                    'Viết một test fail trước cho bug nhỏ rồi mới sửa code.',
                    'Tách một test dùng mock và một test dùng integration để thấy khác biệt.',
                    'Chạy test bằng Maven/CI command thay vì chỉ chạy trong IDE.',
                ],
            ],
            'messaging' => [
                'answer' => [
                    'Messaging nên được giải thích qua producer, broker, consumer, retry và idempotency.',
                    'Event giúp giảm coupling theo thời gian nhưng làm consistency và quan sát phức tạp hơn.',
                    'Một flow bền cần biết chuyện gì xảy ra khi publish thành công nhưng xử lý phía sau thất bại.',
                ],
                'explanation' => [
                    'Async integration làm hệ thống linh hoạt hơn nhưng khó debug hơn synchronous call.',
                    'Outbox, retry và dead-letter queue là cách kiểm soát failure thay vì hy vọng mọi thứ luôn thành công.',
                    'Consumer nên được thiết kế để xử lý duplicate message một cách an toàn.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi tách tác vụ nền, tích hợp service hoặc cần recovery sau lỗi tạm thời.',
                    'Dễ sai khi quên duplicate, ordering, poison message hoặc backlog tăng quá nhanh.',
                    'Cần log correlation id để lần theo event qua nhiều service.',
                ],
                'practice' => [
                    'Vẽ một flow event từ nơi tạo event tới consumer cuối cùng.',
                    'Giả lập consumer xử lý cùng message hai lần và kiểm tra idempotency.',
                    'Thiết kế một dead-letter case và cách người vận hành phát hiện nó.',
                ],
            ],
            'web' => [
                'answer' => [
                    'API tốt cần nói rõ request, response, status code, validation và backward compatibility.',
                    'HTTP không chỉ là endpoint chạy được; nó là contract giữa client và server.',
                    'Một câu trả lời thực tế nên nhắc cả lỗi input, retry và cách client hiểu response.',
                ],
                'explanation' => [
                    'Nhiều bug API đến từ response mơ hồ, status code sai hoặc contract thay đổi đột ngột.',
                    'Validation nên diễn ra sớm để lỗi không đi sâu vào service và database.',
                    'Backward compatibility quan trọng khi client cũ vẫn đang dùng API.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi thiết kế endpoint, DTO, error response, pagination và versioning.',
                    'Dễ sai khi chỉ test request thành công mà không test lỗi nghiệp vụ.',
                    'Cần chú ý idempotency với POST/PUT/PATCH và retry từ client.',
                ],
                'practice' => [
                    'Viết bảng contract cho một endpoint: input, output, status code và error shape.',
                    'Test request thiếu field, field sai kiểu và request lặp lại.',
                    'Đổi response rồi kiểm tra client cũ có bị vỡ không.',
                ],
            ],
            'ops' => [
                'answer' => [
                    'Câu trả lời nên nói hệ thống báo lỗi, đo đạc và tự bảo vệ như thế nào khi chạy thật.',
                    'Observability gồm log, metric và trace; mỗi loại trả lời một câu hỏi khác nhau.',
                    'Reliability không chỉ là code đúng mà còn là timeout, retry, alert và rollback.',
                ],
                'explanation' => [
                    'Nếu không có tín hiệu quan sát, production issue sẽ trở thành đoán mò.',
                    'Alert tốt phải hành động được, ít nhiễu và có đủ context để điều tra.',
                    'Timeout và circuit breaker giúp tránh lỗi dây chuyền khi dependency chậm hoặc lỗi.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi vận hành API, job nền, external dependency hoặc hệ thống có traffic thật.',
                    'Dễ sai khi log quá ít, log quá nhiều hoặc metric không trả lời được câu hỏi vận hành.',
                    'Cần nghĩ tới correlation id, latency percentile và error budget.',
                ],
                'practice' => [
                    'Thêm correlation id vào một request flow rồi lần theo log.',
                    'Định nghĩa một metric latency và một alert có ngưỡng hành động rõ.',
                    'Giả lập dependency chậm để kiểm tra timeout và retry.',
                ],
            ],
            'io' => [
                'answer' => [
                    'I/O cần được nhìn qua encoding, kích thước dữ liệu, resource cleanup và lỗi từ hệ thống ngoài.',
                    'Đọc file hoặc gọi network đều có thể chậm, lỗi giữa chừng hoặc trả dữ liệu không như mong đợi.',
                    'Một câu trả lời tốt nói rõ cách đóng tài nguyên và xử lý partial failure.',
                ],
                'explanation' => [
                    'File nhỏ trong máy dev không đại diện cho file lớn hoặc storage chậm ở production.',
                    'Encoding sai có thể làm hỏng dữ liệu dù code nhìn có vẻ đúng.',
                    'Network I/O cần timeout vì dependency ngoài không nằm trong quyền kiểm soát của bạn.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi upload file, đọc stream, gọi HTTP hoặc tích hợp storage ngoài.',
                    'Dễ sai khi đọc toàn bộ file vào memory hoặc quên đóng stream.',
                    'Cần kiểm tra permission, path, file type, size limit và error message.',
                ],
                'practice' => [
                    'Đọc một file lớn bằng stream và log số dòng đã xử lý.',
                    'Thử sai charset để thấy dữ liệu bị ảnh hưởng thế nào.',
                    'Giả lập HTTP timeout và kiểm tra code cleanup/retry.',
                ],
            ],
            'architecture' => [
                'answer' => [
                    'Architecture tốt giúp thay đổi ít đau hơn, không phải làm code trông phức tạp hơn.',
                    'Hãy nói về responsibility, coupling, extension point và trade-off của lựa chọn.',
                    'Pattern chỉ có giá trị khi nó làm boundary rõ hơn hoặc giảm rủi ro thay đổi.',
                ],
                'explanation' => [
                    'Design pattern bị dùng sai khi vấn đề còn đơn giản nhưng code bị bẻ vòng vèo.',
                    'Một boundary tốt giúp test dễ hơn và giảm tác động khi yêu cầu đổi.',
                    'Senior thinking nằm ở việc nói rõ trade-off, không phải thuộc tên pattern.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi review module, tách service, chọn pattern hoặc thiết kế contract.',
                    'Dễ sai khi thêm abstraction trước khi có nhu cầu thay đổi thật.',
                    'Cần xem change scenario: nếu đổi một use case thì phần nào bị ảnh hưởng.',
                ],
                'practice' => [
                    'Vẽ dependency giữa các class rồi tìm coupling không cần thiết.',
                    'Refactor một if-else dài sang strategy chỉ khi nó thật sự làm code rõ hơn.',
                    'Viết trade-off ngắn cho một lựa chọn thiết kế bạn vừa đưa ra.',
                ],
            ],
            'interview' => [
                'answer' => [
                    'Khi gặp câu lạ, hãy làm rõ input, output, constraint và giả định trước khi trả lời.',
                    'Một câu trả lời tự nhiên nên đi từ quan sát, phân tích, lựa chọn rồi cách kiểm chứng.',
                    'Đừng vội đưa đáp án cuối; hãy cho người nghe thấy cách bạn suy luận.',
                ],
                'explanation' => [
                    'Phỏng vấn kỹ thuật đánh giá cách nghĩ nhiều không kém đáp án.',
                    'Nếu thiếu dữ kiện, nói rõ điều bạn cần hỏi thêm thay vì đoán chắc chắn.',
                    'Câu trả lời mạnh thường có ví dụ nhỏ và rủi ro thực tế đi kèm.',
                ],
                'applyOrPitfalls' => [
                    'Áp dụng khi phân tích code lạ, debug live hoặc thảo luận trade-off.',
                    'Dễ sai khi học thuộc câu mẫu nhưng không giải thích được vì sao.',
                    'Nên nói rõ giả định, rủi ro và cách kiểm chứng kết luận.',
                ],
                'practice' => [
                    'Chọn một câu và trả lời theo flow: làm rõ đề, phân tích, ví dụ, pitfall.',
                    'Ghi âm câu trả lời một phút rồi sửa chỗ lan man.',
                    'Lấy một bug cũ và trình bày lại như một case phỏng vấn.',
                ],
            ],
        ];
    }

    return [
        'core' => [
            'answer' => [
                'Start with a small Java example, then name the rule that explains the result.',
                'Focus on values, references, scope, and execution order before jumping to terminology.',
                'When reading code, ask where the data lives and which line can change state.',
            ],
            'explanation' => [
                'Java Core is easiest to understand when you run short examples instead of memorizing syntax.',
                'Null handling, equality, collection mutation, and autoboxing often reveal mistakes only in edge cases.',
                'A clear explanation includes the rule and one counterexample.',
            ],
            'applyOrPitfalls' => [
                'Use this when reviewing DTO mapping, validation, collection logic, string comparison, or branching code.',
                'A common mistake is trusting intuition without running a small example.',
                'Check null values, empty values, and object identity before drawing conclusions.',
            ],
            'practice' => [
                'Create a tiny class, print each step, and change one input to observe the rule.',
                'Write one test for the expected case and one for the case people usually miss.',
                'Explain the result with concrete variables, objects, and output.',
            ],
        ],
        'spring' => [
            'answer' => [
                'Map the concept onto a real request flow: controller, service, repository, and configuration.',
                'Separate what plain Java does from what the Spring container manages for you.',
                'Mention what the annotation helps with and what design boundary it does not replace.',
            ],
            'explanation' => [
                'Spring can make weak boundaries look acceptable because the application still starts.',
                'Look at dependencies, bean lifecycle, transaction boundaries, and layer responsibilities.',
                'A service that is hard to test often hides too much framework or controller logic.',
            ],
            'applyOrPitfalls' => [
                'Use this when separating controllers, services, repositories, configuration, and test slices.',
                'A common mistake is using annotations as a substitute for clear design.',
                'Watch for circular dependencies, self-invocation, and transactions placed in the wrong layer.',
            ],
            'practice' => [
                'Trace one request from controller to repository and write down the responsibility of each step.',
                'Write a service test without booting the whole application when possible.',
                'Remove or change one annotation in a sandbox branch to see what Spring was doing.',
            ],
        ],
        'data' => [
            'answer' => [
                'Connect the Java code to real SQL, transaction boundaries, and data volume.',
                'Correct output is not enough; know how many queries run and where locks can appear.',
                'For data work, discuss correctness, performance, and rollback behavior together.',
            ],
            'explanation' => [
                'Many data bugs come from queries, indexes, isolation, or migrations rather than Java syntax.',
                'ORMs speed up development, but they do not remove the need to read SQL and logs.',
                'A transaction is a business boundary, not just an annotation.',
            ],
            'applyOrPitfalls' => [
                'Use this when writing repositories, designing queries, migrating schemas, or handling consistency.',
                'A common mistake is looking only at the entity and never checking the SQL.',
                'Watch for N+1 queries, missing indexes, long transactions, and surprising rollback behavior.',
            ],
            'practice' => [
                'Enable SQL logging for one use case and count the real queries.',
                'Create enough sample data to expose the difference between a good query and a weak one.',
                'Write a transaction test for both commit and rollback.',
            ],
        ],
        'security' => [
            'answer' => [
                'Start with the trust boundary: who sends the request, how identity is proven, and where permission is checked.',
                'Authentication identifies the user; authorization decides what the user may do.',
                'A secure flow should cover valid tokens, invalid tokens, expiration, and revocation.',
            ],
            'explanation' => [
                'Security bugs often live in secondary cases: expired tokens, replay, missing permission, or sensitive logs.',
                'Do not trust client input just because the request passed authentication.',
                'Security design should be auditable and debuggable during incidents.',
            ],
            'applyOrPitfalls' => [
                'Use this when designing login, refresh tokens, protected endpoints, and role-based access.',
                'A common mistake is testing only the successful login flow.',
                'Check missing permissions, old tokens, token reuse, and sensitive data in logs.',
            ],
            'practice' => [
                'Create a checklist for a protected endpoint: identity, permission, input, audit log, and error response.',
                'Test requests with no token, an expired token, and a valid token without permission.',
                'Trace the refresh-token flow from controller to token storage.',
            ],
        ],
        'concurrency' => [
            'answer' => [
                'Identify shared state, who reads or writes it, and whether operation order changes the result.',
                'Concurrency is about safety, ordering, timeout, and cancellation, not only speed.',
                'A strong answer explains how the bug appears when multiple operations run at once.',
            ],
            'explanation' => [
                'Concurrency bugs are hard to reproduce because they depend on timing and load.',
                'Reducing shared mutable state is often safer than adding locks everywhere.',
                'Long async flows need clear step names and centralized error handling.',
            ],
            'applyOrPitfalls' => [
                'Use this for batch jobs, background work, thread pools, CompletableFuture, and shared caches.',
                'A common mistake is trusting code that only worked locally without load or concurrent access.',
                'Watch for lost updates, deadlocks, retry storms, and tasks that cannot be cancelled.',
            ],
            'practice' => [
                'Write a tiny race-condition example and fix it by reducing shared state or synchronizing correctly.',
                'Measure before and after adding concurrency so optimization is evidence-based.',
                'Try timing out or cancelling a task and verify resource cleanup.',
            ],
        ],
        'testing' => [
            'answer' => [
                'A good test describes behavior the system must keep, not just a method call for coverage.',
                'Separate unit, integration, and regression tests by real risk.',
                'A useful test fails when important behavior is broken.',
            ],
            'explanation' => [
                'Mocks isolate dependencies, but too many mocks make tests follow implementation details.',
                'A regression test should reproduce the bug before the fix is written.',
                'Build tools and CI turn checking into part of the workflow instead of a manual habit.',
            ],
            'applyOrPitfalls' => [
                'Use this when fixing bugs, refactoring services, or changing API contracts.',
                'A common mistake is testing only clean data and the easiest path.',
                'Prioritize behavior with real business or production risk.',
            ],
            'practice' => [
                'Write a failing test for a small bug before changing the implementation.',
                'Compare one mock-based test with one integration test to see what each protects.',
                'Run tests through Maven or CI commands, not only through the IDE.',
            ],
        ],
        'messaging' => [
            'answer' => [
                'Explain messaging through producer, broker, consumer, retry, and idempotency.',
                'Events reduce time coupling but make consistency and observability harder.',
                'A durable flow explains what happens when publishing succeeds but later processing fails.',
            ],
            'explanation' => [
                'Async integration gives flexibility but is harder to debug than a direct call.',
                'Outbox, retry, and dead-letter queues control failure instead of assuming every message succeeds.',
                'Consumers should handle duplicate messages safely.',
            ],
            'applyOrPitfalls' => [
                'Use this when splitting background work, integrating services, or recovering from temporary downstream failure.',
                'A common mistake is forgetting duplicates, ordering, poison messages, or fast-growing backlogs.',
                'Log a correlation id so one event can be traced across services.',
            ],
            'practice' => [
                'Draw the event flow from producer to final consumer.',
                'Process the same message twice and check whether the consumer remains safe.',
                'Design one dead-letter case and how operators would notice it.',
            ],
        ],
        'web' => [
            'answer' => [
                'A good API answer covers request, response, status code, validation, and backward compatibility.',
                'HTTP is not just a working endpoint; it is a contract between client and server.',
                'Mention invalid input, retry behavior, and how the client should interpret the response.',
            ],
            'explanation' => [
                'Many API bugs come from vague responses, wrong status codes, or sudden contract changes.',
                'Validate early so bad data does not travel deep into services and databases.',
                'Backward compatibility matters while older clients still use the API.',
            ],
            'applyOrPitfalls' => [
                'Use this when designing endpoints, DTOs, error responses, pagination, and versioning.',
                'A common mistake is testing only the successful request.',
                'Think about idempotency for POST, PUT, PATCH, and client retry behavior.',
            ],
            'practice' => [
                'Write a contract table for one endpoint: input, output, status code, and error shape.',
                'Test missing fields, wrong field types, and repeated requests.',
                'Change a response shape and check whether an older client breaks.',
            ],
        ],
        'ops' => [
            'answer' => [
                'Explain how the system reports problems, measures itself, and protects itself while running.',
                'Observability combines logs, metrics, and traces; each answers a different question.',
                'Reliability includes timeout, retry, alerting, and rollback, not only correct code.',
            ],
            'explanation' => [
                'Without observable signals, production issues become guesswork.',
                'A good alert is actionable, low-noise, and has enough context for investigation.',
                'Timeouts and circuit breakers reduce cascading failure when dependencies are slow or failing.',
            ],
            'applyOrPitfalls' => [
                'Use this when operating APIs, background jobs, external dependencies, or real traffic systems.',
                'A common mistake is logging too little, logging too much, or collecting metrics that answer no operational question.',
                'Think about correlation ids, latency percentiles, and error budgets.',
            ],
            'practice' => [
                'Add a correlation id to one request flow and follow it through logs.',
                'Define one latency metric and one alert with a clear action threshold.',
                'Simulate a slow dependency and test timeout and retry behavior.',
            ],
        ],
        'io' => [
            'answer' => [
                'Look at I/O through encoding, data size, resource cleanup, and errors from external systems.',
                'File reads and network calls can be slow, partial, or return unexpected data.',
                'A complete answer explains how resources are closed and partial failure is handled.',
            ],
            'explanation' => [
                'A small local file does not represent a large production file or slow storage.',
                'Wrong encoding can corrupt data even when the code looks correct.',
                'Network I/O needs timeouts because external systems are outside your control.',
            ],
            'applyOrPitfalls' => [
                'Use this for file uploads, streams, HTTP calls, and external storage.',
                'A common mistake is loading the whole file into memory or forgetting to close streams.',
                'Check permissions, paths, file type, size limits, and error messages.',
            ],
            'practice' => [
                'Read a large file through a stream and log progress as rows are processed.',
                'Try the wrong charset and observe how stored text changes.',
                'Simulate an HTTP timeout and verify cleanup or retry behavior.',
            ],
        ],
        'architecture' => [
            'answer' => [
                'Good architecture makes change less painful; it does not make code look more complicated.',
                'Discuss responsibility, coupling, extension points, and the trade-off behind the choice.',
                'A pattern is valuable when it clarifies a boundary or reduces change risk.',
            ],
            'explanation' => [
                'Patterns become harmful when the problem is simple but the code becomes indirect.',
                'A clear boundary makes testing easier and reduces the impact of change.',
                'Senior thinking means naming trade-offs, not memorizing pattern names.',
            ],
            'applyOrPitfalls' => [
                'Use this when reviewing modules, splitting services, choosing patterns, or designing contracts.',
                'A common mistake is adding abstraction before there is a real change pressure.',
                'Ask what changes when one use case changes.',
            ],
            'practice' => [
                'Draw dependencies between classes and look for unnecessary coupling.',
                'Refactor a long if-else into Strategy only if it actually makes behavior clearer.',
                'Write a short trade-off note for one design decision.',
            ],
        ],
        'interview' => [
            'answer' => [
                'For unfamiliar questions, clarify input, output, constraints, and assumptions before answering.',
                'A natural answer moves from observation to analysis, decision, and verification.',
                'Do not rush to the final answer; show how you reason.',
            ],
            'explanation' => [
                'Technical interviews evaluate reasoning as much as the final answer.',
                'When information is missing, say what you would ask instead of pretending certainty.',
                'A strong answer usually includes a small example and a realistic risk.',
            ],
            'applyOrPitfalls' => [
                'Use this when analyzing unfamiliar code, debugging live, or discussing trade-offs.',
                'A common mistake is memorizing a sample answer without understanding why it works.',
                'State assumptions, risks, and how you would verify the conclusion.',
            ],
            'practice' => [
                'Answer one question using this flow: clarify, analyze, example, pitfall.',
                'Record a one-minute answer and remove the rambling parts.',
                'Turn an old bug into a short interview case.',
            ],
        ],
    ];
}

function rebalanceArray(array $items, array $repeated, array $pool, string $field, string $seed, int $min): array
{
    $result = [];
    $seen = [];
    $removed = false;

    foreach ($items as $item) {
        if (!is_string($item) || trim($item) === '') {
            continue;
        }
        if (in_array($item, $repeated, true)) {
            $removed = true;
            continue;
        }
        if (!isset($seen[$item])) {
            $result[] = $item;
            $seen[$item] = true;
        }
    }

    if (!$removed && count($result) >= $min) {
        return $result;
    }

    $choices = $pool[$field] ?? [];
    $offset = abs(crc32($seed . ':' . $field)) % max(1, count($choices));
    for ($i = 0; count($result) < $min && $i < count($choices) * 2; $i++) {
        $candidate = $choices[($offset + $i) % count($choices)];
        if (!isset($seen[$candidate])) {
            $result[] = $candidate;
            $seen[$candidate] = true;
        }
    }

    return $result;
}

function rebalanceFile(string $path, string $lang): int
{
    $bank = readJsonFile($path);
    $pools = copyPools($lang);
    $repeated = repeatedLines($lang);
    $changed = 0;

    foreach ($bank['topics'] as &$topic) {
        $category = categoryForTopic($topic);
        $pool = $pools[$category] ?? $pools['core'];

        foreach ($topic['questions'] as &$question) {
            $seed = (string) ($question['id'] ?? ($topic['id'] ?? 'topic'));
            $fieldMap = [
                'answer' => ['answer', 4],
                'explanation' => ['explanation', 3],
                'applyOrPitfalls' => ['applyOrPitfalls', 3],
                'practice' => ['practice', 3],
            ];

            foreach ($fieldMap as $poolField => [$jsonField, $min]) {
                $before = $question[$jsonField] ?? [];
                if (!is_array($before)) {
                    $before = [];
                }
                $after = rebalanceArray($before, $repeated, $pool, $poolField, $seed, $min);
                if ($after !== $before) {
                    $question[$jsonField] = $after;
                    $changed++;
                }
            }

            if (isset($question['answerShort']) && in_array($question['answerShort'], $repeated, true)) {
                $question['answerShort'] = $question['answer'][0] ?? '';
                $changed++;
            }
        }
        unset($question);
    }
    unset($topic);

    writeJsonFile($path, $bank);
    return $changed;
}

$en = $root . '/docs/data/content/question-bank.en.json';
$vi = $root . '/docs/data/content/question-bank.vi.json';
$fallback = $root . '/docs/data/content/question-bank.json';

$enChanges = rebalanceFile($en, 'en');
$viChanges = rebalanceFile($vi, 'vi');
copy($vi, $fallback);

echo "Rebalanced English copy blocks: {$enChanges}\n";
echo "Rebalanced Vietnamese copy blocks: {$viChanges}\n";
echo "Synced fallback question bank from Vietnamese source.\n";
