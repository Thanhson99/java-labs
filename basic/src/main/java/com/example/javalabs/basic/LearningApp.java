package com.example.javalabs.basic;

import com.example.javalabs.basic.featureflag.*;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point for the basic Java learning module.
 *
 * <p>The goal of this class is not to contain complex logic. Instead, it wires together the
 * smaller example classes so you can run the project and inspect how each concept behaves.</p>
 */
public final class LearningApp {

    private LearningApp() {
    }

    /**
     * Runs a small guided tour through the example classes.
     *
     * @param args command-line arguments that are currently unused
     */
    public static void main(String[] args) {
        printSection("Practice Roadmap");
        PracticeRoadmap.stages().forEach(stage -> {
            System.out.println(stage.order() + ". " + stage.title());
            System.out.println("   Goal: " + stage.goal());
            System.out.println("   Read: " + stage.sourceFiles());
            System.out.println("   Tests: " + stage.testFiles());
        });

        printSection("Control Flow");
        System.out.println("classifyNumber(7) = " + ControlFlowExamples.classifyNumber(7));
        System.out.println("sumEvenNumbers(10) = " + ControlFlowExamples.sumEvenNumbers(10));
        System.out.println("factorial(5) = " + ControlFlowExamples.factorial(5));

        printSection("Strings");
        System.out.println("reverseWords('java is fun') = " + StringToolkit.reverseWords("java is fun"));
        System.out.println("countVowels('Documentation') = " + StringToolkit.countVowels("Documentation"));
        System.out.println("isPalindrome('level') = " + StringToolkit.isPalindrome("level"));

        printSection("Objects and Encapsulation");
        BankAccount account = new BankAccount("ACC-001", "Alice", 100.0);
        account.deposit(50.0);
        account.withdraw(20.0);
        System.out.println(account.summary());

        printSection("Collections and Streams");
        List<Student> students = List.of(
                new Student("Alice", 92, List.of("Java", "SQL")),
                new Student("Bob", 77, List.of("Spring")),
                new Student("Cara", 88, List.of("Java", "Testing", "Docker"))
        );
        System.out.println("Top student = " + StudentAnalytics.findTopStudent(students).name());
        System.out.println("Average score = " + StudentAnalytics.averageScore(students));
        System.out.println("Students studying Java = " + StudentAnalytics.filterByTopic(students, "java"));

        printSection("Polymorphism and Sealed Types");
        List<Shape> shapes = List.of(new Circle(2.0), new Rectangle(3.0, 4.0));
        for (Shape shape : shapes) {
            System.out.println(shape.describe() + ", area = " + shape.area());
        }

        printSection("Asynchronous Code");
        String report = AsyncExamples.buildUserReport("Ada").join();
        System.out.println(report);

        printSection("Enums and Generics");
        Pair<String, DifficultyLevel> learningPair = Pair.of("Current module", DifficultyLevel.INTERMEDIATE);
        System.out.println(learningPair.left() + " -> " + learningPair.right());
        System.out.println("Advice: " + learningPair.right().studyAdvice());

        printSection("Maps and Sorting");
        List<InventoryItem> inventory = List.of(
                new InventoryItem("Keyboard", 12, "hardware"),
                new InventoryItem("Mouse", 5, "hardware"),
                new InventoryItem("Notebook", 20, "stationery"),
                new InventoryItem("Pen", 3, "stationery")
        );
        System.out.println("Quantity by category = " + InventoryAnalytics.totalQuantityByCategory(inventory));
        System.out.println("Top stock item = " + InventoryAnalytics.sortByStockDescending(inventory).get(0).name());
        System.out.println("Low stock warning = " + InventoryAnalytics.firstLowStockItem(inventory, 3));

        printSection("Exceptions");
        System.out.println("safeDivide(9, 3) = " + ExceptionPlayground.safeDivide(9, 3));
        System.out.println("parsePositiveInt('24') = " + ExceptionPlayground.parsePositiveInt("24"));

        printSection("File I/O");
        try {
            Path demoFile = Files.createTempFile("java-labs-demo", ".txt");
            Files.writeString(demoFile, "Java\n\nSpring\nTesting\n");
            System.out.println(FileReport.readNonBlankLines(demoFile));
            System.out.println(FileReport.summarize(demoFile));
            Files.deleteIfExists(demoFile);
        } catch (Exception exception) {
            System.out.println("File demo failed: " + exception.getMessage());
        }

        printSection("Rate Limiting");
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 1_000, timeSource);
        System.out.println("allow #1 = " + limiter.allow("demo-client"));
        System.out.println("allow #2 = " + limiter.allow("demo-client"));
        System.out.println("allow #3 = " + limiter.allow("demo-client"));
        timeSource.advanceMillis(1_000);
        System.out.println("allow after reset = " + limiter.allow("demo-client"));

        printSection("Connection Pooling");
        SimpleConnectionPool<FakeDatabaseConnection> pool =
                new SimpleConnectionPool<>(2, id -> new FakeDatabaseConnection(id, "users-db"));
        try (SimpleConnectionPool.PooledConnection<FakeDatabaseConnection> connection = pool.borrow()) {
            System.out.println(connection.value().query("select * from users where active = true"));
        }
        System.out.println("available connections after release = " + pool.availableCount());

        printSection("Multi-Database Routing");
        MultiDatabaseUserProfileRepository multiRepository = new MultiDatabaseUserProfileRepository(java.util.Map.of(
                Region.APAC, new InMemoryUserProfileRepository("users-apac"),
                Region.EU, new InMemoryUserProfileRepository("users-eu"),
                Region.US, new InMemoryUserProfileRepository("users-us")
        ));
        UserProfile apacUser = new UserProfile("u-100", "apac@example.com", Region.APAC);
        multiRepository.save(apacUser);
        System.out.println("APAC database = " + multiRepository.databaseNameFor(Region.APAC));
        System.out.println("user lookup = " + multiRepository.findById("u-100"));

        printSection("Microservice-Style Service");
        InMemoryNotificationClient notificationClient = new InMemoryNotificationClient();
        RegistrationService registrationService =
                new RegistrationService(multiRepository, notificationClient, new FixedWindowRateLimiter(5, 60_000, new SystemTimeSource()));
        RegistrationResult registrationResult =
                registrationService.register("learning-api-key", new UserProfile("u-200", "new@example.com", Region.EU));
        System.out.println(registrationResult);
        System.out.println("sent notifications = " + notificationClient.sentMessages());

        printSection("Optimization: Avoid Repeated Lookups");
        List<Customer> customers = List.of(
                new Customer("c-1", "Alice", "retail"),
                new Customer("c-2", "Bob", "enterprise")
        );
        List<Order> orders = List.of(
                new Order("o-1", "c-1", 120.0),
                new Order("o-2", "c-1", 35.0),
                new Order("o-3", "c-2", 900.0)
        );
        InMemoryCustomerDirectory repeatedLookupDirectory = new InMemoryCustomerDirectory(customers);
        OrderSummaryService repeatedLookupService = new OrderSummaryService(repeatedLookupDirectory);
        System.out.println("Repeated lookup summaries = "
                + repeatedLookupService.buildSummariesWithRepeatedLookup(orders));
        System.out.println("Repeated lookup calls = " + repeatedLookupDirectory.singleLookupCount());

        InMemoryCustomerDirectory batchLookupDirectory = new InMemoryCustomerDirectory(customers);
        OrderSummaryService batchLookupService = new OrderSummaryService(batchLookupDirectory);
        System.out.println("Batch lookup summaries = "
                + batchLookupService.buildSummariesWithBatchLookup(orders));
        System.out.println("Batch lookup calls = " + batchLookupDirectory.batchLookupCount());

        printSection("Optimization: TTL Cache");
        ManualTimeSource cacheClock = new ManualTimeSource(1_000);
        InMemoryCustomerDirectory cacheDelegate = new InMemoryCustomerDirectory(customers);
        CachedCustomerDirectory cachedDirectory = new CachedCustomerDirectory(cacheDelegate, 5_000, cacheClock);
        System.out.println("First lookup = " + cachedDirectory.findById("c-1"));
        System.out.println("Second lookup from cache = " + cachedDirectory.findById("c-1"));
        System.out.println("Delegate single lookups before TTL expires = " + cacheDelegate.singleLookupCount());
        cacheClock.advanceMillis(5_000);
        System.out.println("Lookup after TTL expires = " + cachedDirectory.findById("c-1"));
        System.out.println("Delegate single lookups after TTL expires = " + cacheDelegate.singleLookupCount());

        printSection("Optimization: Bounded Cache");
        InMemoryCustomerDirectory boundedCacheDelegate = new InMemoryCustomerDirectory(List.of(
                new Customer("c-1", "Alice", "retail"),
                new Customer("c-2", "Bob", "enterprise"),
                new Customer("c-3", "Cara", "partner")
        ));
        CachedCustomerDirectory boundedCache =
                new CachedCustomerDirectory(boundedCacheDelegate, 30_000, new ManualTimeSource(2_000), 2);
        boundedCache.findById("c-1");
        boundedCache.findById("c-2");
        boundedCache.findById("c-1");
        boundedCache.findById("c-3");
        System.out.println("Bounded cache entries = " + boundedCache.cachedEntryCount());
        System.out.println("Delegate lookups after LRU eviction = " + boundedCacheDelegate.singleLookupCount());

        printSection("Reliability: Retry Temporary Failures");
        FlakyNotificationClient flakyNotificationClient = new FlakyNotificationClient(2);
        ResilientNotificationClient resilientNotificationClient =
                new ResilientNotificationClient(flakyNotificationClient, 3);
        resilientNotificationClient.sendWelcomeMessage(new UserProfile("u-300", "retry@example.com", Region.US));
        System.out.println("Notification attempts = " + flakyNotificationClient.attemptCount());
        System.out.println("Notification successes = " + flakyNotificationClient.successCount());

        printSection("Reliability: Circuit Breaker");
        ManualTimeSource breakerClock = new ManualTimeSource(1_000);
        FlakyNotificationClient failingNotificationClient = new FlakyNotificationClient(5);
        CircuitBreakerNotificationClient breakerClient =
                new CircuitBreakerNotificationClient(failingNotificationClient, 2, 10_000, breakerClock);
        try {
            breakerClient.sendWelcomeMessage(new UserProfile("u-400", "breaker@example.com", Region.US));
        } catch (RuntimeException exception) {
            System.out.println("First downstream failure = " + exception.getMessage());
        }
        try {
            breakerClient.sendWelcomeMessage(new UserProfile("u-400", "breaker@example.com", Region.US));
        } catch (RuntimeException exception) {
            System.out.println("Second downstream failure opens breaker = " + breakerClient.state());
        }
        try {
            breakerClient.sendWelcomeMessage(new UserProfile("u-400", "breaker@example.com", Region.US));
        } catch (RuntimeException exception) {
            System.out.println("Fast failure while open = " + exception.getMessage());
        }
        breakerClock.advanceMillis(10_000);
        System.out.println("State after cooldown = " + breakerClient.state());

        printSection("Observability: Client Metrics");
        ManualTimeSource metricsClock = new ManualTimeSource(1_000);
        InMemoryNotificationClient measuredClient = new InMemoryNotificationClient();
        LatencySimulatingNotificationClient slowMeasuredClient =
                new LatencySimulatingNotificationClient(measuredClient, metricsClock, 75);
        InstrumentedNotificationClient instrumentedClient =
                new InstrumentedNotificationClient(slowMeasuredClient, metricsClock);
        instrumentedClient.sendWelcomeMessage(new UserProfile("u-500", "metrics@example.com", Region.EU));
        instrumentedClient.sendWelcomeMessage(new UserProfile("u-501", "metrics-2@example.com", Region.EU));
        ClientCallMetrics metrics = instrumentedClient.metrics();
        System.out.println("Client total calls = " + metrics.totalCalls());
        System.out.println("Client successful calls = " + metrics.successfulCalls());
        System.out.println("Client average duration ms = " + metrics.averageDurationMillis());

        printSection("Optimization: Paging Large Reads");
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository(List.of(
                new Order("o-10", "c-1", 10.0),
                new Order("o-20", "c-1", 20.0),
                new Order("o-30", "c-2", 30.0),
                new Order("o-40", "c-2", 40.0),
                new Order("o-50", "c-2", 50.0)
        ));
        OrderExportService exportService = new OrderExportService(orderRepository);
        System.out.println("Paged export report = " + exportService.exportInPages(2));
        System.out.println("findAll calls = " + orderRepository.findAllCount());
        System.out.println("findPage calls = " + orderRepository.findPageCount());

        printSection("Optimization: Cursor Paging");
        InMemoryCursorOrderRepository cursorOrderRepository = new InMemoryCursorOrderRepository(List.of(
                new Order("o-50", "c-1", 50.0),
                new Order("o-10", "c-1", 10.0),
                new Order("o-30", "c-2", 30.0),
                new Order("o-20", "c-1", 20.0),
                new Order("o-40", "c-2", 40.0)
        ));
        CursorOrderExportService cursorExportService = new CursorOrderExportService(cursorOrderRepository);
        System.out.println("Cursor export report = " + cursorExportService.exportWithCursor(2));
        System.out.println("findAfter calls = " + cursorOrderRepository.findAfterCount());

        printSection("Reliability: Idempotent Side Effects");
        InMemoryNotificationClient idempotentDelegate = new InMemoryNotificationClient();
        InMemoryIdempotencyStore idempotencyStore = new InMemoryIdempotencyStore();
        IdempotentNotificationClient idempotentClient =
                new IdempotentNotificationClient(idempotentDelegate, idempotencyStore);
        UserProfile duplicatedUser = new UserProfile("u-600", "duplicate@example.com", Region.APAC);
        idempotentClient.sendWelcomeMessage(duplicatedUser);
        idempotentClient.sendWelcomeMessage(duplicatedUser);
        System.out.println("Sent messages after duplicate calls = " + idempotentDelegate.sentMessages().size());
        System.out.println("Idempotency keys stored = " + idempotencyStore.processedCount());

        printSection("Reliability: Bulkhead Limit");
        BulkheadNotificationClient bulkheadClient =
                new BulkheadNotificationClient(new InMemoryNotificationClient(), 2);
        bulkheadClient.sendWelcomeMessage(new UserProfile("u-700", "bulkhead@example.com", Region.US));
        System.out.println("Bulkhead in-flight calls after completion = " + bulkheadClient.inFlightCalls());

        printSection("Reliability: Timeout Boundary");
        ManualTimeSource timeoutClock = new ManualTimeSource(1_000);
        InMemoryNotificationClient timeoutDelegate = new InMemoryNotificationClient();
        LatencySimulatingNotificationClient slowClient =
                new LatencySimulatingNotificationClient(timeoutDelegate, timeoutClock, 150);
        TimeoutNotificationClient timeoutClient = new TimeoutNotificationClient(slowClient, 100, timeoutClock);
        try {
            timeoutClient.sendWelcomeMessage(new UserProfile("u-800", "timeout@example.com", Region.EU));
        } catch (RuntimeException exception) {
            System.out.println("Timeout result = " + exception.getMessage());
        }

        printSection("Optimization: Batched Notifications");
        InMemoryNotificationBatchClient batchClient = new InMemoryNotificationBatchClient();
        WelcomeNotificationBatcher batcher = new WelcomeNotificationBatcher(batchClient, 2);
        batcher.enqueueWelcomeMessage(new UserProfile("u-900", "batch-1@example.com", Region.APAC));
        batcher.enqueueWelcomeMessage(new UserProfile("u-901", "batch-2@example.com", Region.APAC));
        batcher.enqueueWelcomeMessage(new UserProfile("u-902", "batch-3@example.com", Region.APAC));
        batcher.flush();
        System.out.println("Batched sent messages = " + batchClient.sentMessageCount());
        System.out.println("Batch downstream calls = " + batchClient.batchCallCount());

        printSection("Reliability: Dead Letter Capture");
        InMemoryDeadLetterStore deadLetterStore = new InMemoryDeadLetterStore();
        DeadLetteringNotificationClient deadLetteringClient =
                new DeadLetteringNotificationClient(new FlakyNotificationClient(1), deadLetterStore);
        try {
            deadLetteringClient.sendWelcomeMessage(new UserProfile("u-950", "dead-letter@example.com", Region.US));
        } catch (RuntimeException exception) {
            System.out.println("Captured failure = " + exception.getMessage());
        }
        System.out.println("Dead-letter messages = " + deadLetterStore.findAll());

        printSection("Data Consistency: Optimistic Locking");
        InMemoryVersionedInventoryRepository versionedRepository =
                new InMemoryVersionedInventoryRepository(List.of(new VersionedInventoryItem("SKU-1", 10, 0)));
        StockReservationService stockReservationService = new StockReservationService(versionedRepository);
        System.out.println("Reserved stock = " + stockReservationService.reserve("SKU-1", 3));
        VersionedInventoryItem staleRead = new VersionedInventoryItem("SKU-1", 7, 0);
        try {
            versionedRepository.save(staleRead.reserve(1), staleRead.version());
        } catch (OptimisticLockException exception) {
            System.out.println("Rejected stale write = " + exception.getMessage());
        }

        printSection("Reliability: Outbox Pattern");
        InMemoryUserProfileRepository outboxUserRepository = new InMemoryUserProfileRepository("users-outbox");
        InMemoryOutboxEventStore outboxStore = new InMemoryOutboxEventStore();
        OutboxRegistrationService outboxRegistrationService =
                new OutboxRegistrationService(outboxUserRepository, outboxStore);
        outboxRegistrationService.register(new UserProfile("u-1000", "outbox@example.com", Region.EU));
        OutboxDispatcher outboxDispatcher =
                new OutboxDispatcher(outboxStore, new InMemoryOutboxEventPublisher());
        System.out.println("Outbox events before dispatch = " + outboxStore.findAll());
        System.out.println("Dispatched outbox events = " + outboxDispatcher.dispatchPending(10));
        System.out.println("Outbox events after dispatch = " + outboxStore.findAll());

        printSection("Reliability: Outbox Retry Backoff");
        OutboxRetryPolicy retryPolicy = new OutboxRetryPolicy(5, 1_000, 10_000);
        OutboxRetryPlanner retryPlanner = new OutboxRetryPlanner(retryPolicy);
        System.out.println("Retry plan = " + retryPlanner.planRetries(List.of(
                new OutboxEvent(
                        "user-registered-u-1001",
                        "UserRegistered",
                        "u-1001",
                        "retry-plan@example.com",
                        OutboxEventStatus.FAILED,
                        2
                )
        )));

        printSection("Optimization: Deduplicate Batch Lookups");
        CustomerLookupService customerLookupService = new CustomerLookupService(new InMemoryCustomerDirectory(customers));
        CustomerLookupResult lookupResult =
                customerLookupService.loadCustomersPreservingOrder(List.of("c-1", "c-2", "c-1", "c-2"));
        System.out.println("Requested customer ids = " + lookupResult.requestedCount());
        System.out.println("Unique ids looked up = " + lookupResult.uniqueLookupCount());
        System.out.println("Customers in original order = " + lookupResult.customers());

        printSection("Optimization: Top N Without Full Sort");
        List<InventoryItem> rankingItems = List.of(
                new InventoryItem("Keyboard", 12, "hardware"),
                new InventoryItem("Mouse", 5, "hardware"),
                new InventoryItem("Notebook", 20, "stationery"),
                new InventoryItem("Pen", 3, "stationery"),
                new InventoryItem("Monitor", 20, "hardware"),
                new InventoryItem("Cable", 8, "hardware")
        );
        System.out.println("Top by full sort = " + TopInventorySelector.topByFullSort(rankingItems, 3));
        System.out.println("Top by bounded heap = " + TopInventorySelector.topByBoundedHeap(rankingItems, 3));

        printSection("Optimization: One-Pass Stock Summary");
        InventoryStockSummary stockSummary = InventoryStockSummarizer.summarizeOnePass(rankingItems);
        System.out.println("Stock item count = " + stockSummary.itemCount());
        System.out.println("Stock total quantity = " + stockSummary.totalQuantity());
        System.out.println("Stock average quantity = " + stockSummary.averageQuantity());

        printSection("Optimization: Indexed Lookup");
        IndexedInventoryCatalog catalog = new IndexedInventoryCatalog(rankingItems);
        System.out.println("Indexed hardware items = " + catalog.findByCategoryIndexed("hardware"));
        System.out.println("Scanning hardware items = " + catalog.findByCategoryScanning("hardware"));

        printSection("Optimization: Chunked Batch Processing");
        InMemoryNotificationBatchClient chunkBatchClient = new InMemoryNotificationBatchClient();
        ChunkedNotificationSender chunkedSender = new ChunkedNotificationSender(chunkBatchClient, 2);
        int chunkCalls = chunkedSender.sendAll(List.of(
                new UserProfile("u-1100", "chunk-1@example.com", Region.APAC),
                new UserProfile("u-1101", "chunk-2@example.com", Region.APAC),
                new UserProfile("u-1102", "chunk-3@example.com", Region.APAC),
                new UserProfile("u-1103", "chunk-4@example.com", Region.APAC),
                new UserProfile("u-1104", "chunk-5@example.com", Region.APAC)
        ));
        System.out.println("Chunked downstream calls = " + chunkCalls);
        System.out.println("Chunked sent messages = " + chunkBatchClient.sentMessageCount());

        printSection("Optimization: Sliding Window Rate Limiter");
        ManualTimeSource slidingClock = new ManualTimeSource(1_000);
        SlidingWindowRateLimiter slidingLimiter = new SlidingWindowRateLimiter(2, 1_000, slidingClock);
        System.out.println("sliding allow #1 = " + slidingLimiter.allow("demo-client"));
        slidingClock.advanceMillis(400);
        System.out.println("sliding allow #2 = " + slidingLimiter.allow("demo-client"));
        slidingClock.advanceMillis(400);
        System.out.println("sliding allow #3 before oldest expires = " + slidingLimiter.allow("demo-client"));
        slidingClock.advanceMillis(200);
        System.out.println("sliding allow after oldest expires = " + slidingLimiter.allow("demo-client"));

        printSection("Optimization: Token Bucket Rate Limiter");
        ManualTimeSource tokenClock = new ManualTimeSource(0);
        TokenBucketRateLimiter tokenLimiter = new TokenBucketRateLimiter(2, 1.0, tokenClock);
        System.out.println("token allow #1 = " + tokenLimiter.allow("demo-client"));
        System.out.println("token allow #2 = " + tokenLimiter.allow("demo-client"));
        System.out.println("token allow #3 before refill = " + tokenLimiter.allow("demo-client"));
        tokenClock.advanceMillis(1_000);
        System.out.println("tokens after refill = " + tokenLimiter.availableTokens("demo-client"));
        System.out.println("token allow after refill = " + tokenLimiter.allow("demo-client"));

        printSection("Optimization: Skip No-Op Writes");
        InMemoryUserProfileRepository updateDelegate = new InMemoryUserProfileRepository("users-update");
        updateDelegate.save(new UserProfile("u-1200", "skip@example.com", Region.US));
        CountingUserProfileRepository countingRepository = new CountingUserProfileRepository(updateDelegate);
        UserProfileUpdateService updateService = new UserProfileUpdateService(countingRepository);
        System.out.println(updateService.updateEmailIfChanged("u-1200", " SKIP@example.com "));
        System.out.println(updateService.updateEmailIfChanged("u-1200", "changed@example.com"));
        System.out.println("Actual save calls = " + countingRepository.saveCount());

        printSection("Optimization: Incremental Stock Summary");
        IncrementalInventorySummary incrementalSummary = new IncrementalInventorySummary();
        incrementalSummary.addOrUpdate(new InventoryItem("Keyboard", 12, "hardware"));
        incrementalSummary.addOrUpdate(new InventoryItem("Mouse", 5, "hardware"));
        incrementalSummary.addOrUpdate(new InventoryItem("Keyboard", 30, "hardware"));
        incrementalSummary.remove("Mouse");
        System.out.println("Incremental summary = " + incrementalSummary.snapshot());

        printSection("Optimization: Coalesced Lookups");
        InMemoryCustomerDirectory coalescingDelegate = new InMemoryCustomerDirectory(customers);
        CoalescingCustomerDirectory coalescingDirectory = new CoalescingCustomerDirectory(coalescingDelegate);
        System.out.println("Coalesced lookup = " + coalescingDirectory.findById("c-1"));
        System.out.println("In-flight lookup count = " + coalescingDirectory.inFlightLookupCount());

        printSection("Optimization: Negative Cache");
        InMemoryCustomerDirectory negativeCacheDelegate = new InMemoryCustomerDirectory(customers);
        NegativeCachingCustomerDirectory negativeCache =
                new NegativeCachingCustomerDirectory(negativeCacheDelegate, 5_000, new ManualTimeSource(0));
        System.out.println("Missing lookup #1 = " + negativeCache.findById("missing"));
        System.out.println("Missing lookup #2 = " + negativeCache.findById("missing"));
        System.out.println("Delegate single lookups = " + negativeCacheDelegate.singleLookupCount());

        printSection("Optimization: Stale-While-Revalidate Cache");
        ManualTimeSource staleClock = new ManualTimeSource(0);
        StaleCustomerCache staleCache =
                new StaleCustomerCache(new InMemoryCustomerDirectory(customers), 1_000, 5_000, staleClock);
        System.out.println("Fresh customer = " + staleCache.get("c-1"));
        staleClock.advanceMillis(1_000);
        System.out.println("Is stale before refresh = " + staleCache.isStale("c-1"));
        System.out.println("Customer after refresh attempt = " + staleCache.get("c-1"));

        printSection("Optimization: Jittered Backoff");
        JitteredBackoffPolicy jitteredBackoff = new JitteredBackoffPolicy(5, 1_000, 10_000, 0.2);
        System.out.println("Can retry attempt 2 = " + jitteredBackoff.canRetry(2));
        System.out.println("Jittered delay attempt 0 = " + jitteredBackoff.delayBeforeNextAttemptMillis(0));
        System.out.println("Jittered delay attempt 1 = " + jitteredBackoff.delayBeforeNextAttemptMillis(1));
        System.out.println("Jittered delay attempt 4 = " + jitteredBackoff.delayBeforeNextAttemptMillis(4));

        printSection("Optimization: Retry Budget");
        ManualTimeSource retryBudgetClock = new ManualTimeSource(0);
        RetryBudget retryBudget = new RetryBudget(2, 1_000, retryBudgetClock);
        System.out.println("retry budget acquire #1 = " + retryBudget.tryAcquireRetry());
        System.out.println("retry budget acquire #2 = " + retryBudget.tryAcquireRetry());
        System.out.println("retry budget acquire #3 = " + retryBudget.tryAcquireRetry());
        retryBudgetClock.advanceMillis(1_000);
        System.out.println("retry budget after window = " + retryBudget.remainingRetries());

        printSection("Optimization: Bounded Dead-Letter Store");
        BoundedDeadLetterStore boundedDeadLetters = new BoundedDeadLetterStore(2);
        boundedDeadLetters.save(new DeadLetterMessage("msg-1", "payload-1", "temporary failure"));
        boundedDeadLetters.save(new DeadLetterMessage("msg-2", "payload-2", "temporary failure"));
        boundedDeadLetters.save(new DeadLetterMessage("msg-3", "payload-3", "temporary failure"));
        System.out.println("Dead letters kept = " + boundedDeadLetters.findAll());
        System.out.println("Dead letters dropped = " + boundedDeadLetters.droppedCount());

        printSection("Optimization: Two-Level Customer Cache");
        InMemoryCustomerDirectory twoLevelDelegate = new InMemoryCustomerDirectory(customers);
        TwoLevelCustomerCache twoLevelCache = new TwoLevelCustomerCache(twoLevelDelegate, 1, 3);
        System.out.println("Two-level first lookup = " + twoLevelCache.findById("c-1"));
        System.out.println("Two-level second lookup = " + twoLevelCache.findById("c-1"));
        System.out.println("Two-level delegate lookups = " + twoLevelDelegate.singleLookupCount());
        System.out.println("Two-level L1 size = " + twoLevelCache.l1Size());
        System.out.println("Two-level L2 size = " + twoLevelCache.l2Size());

        printSection("Optimization: Cache Warmup");
        InMemoryCustomerDirectory warmupDelegate = new InMemoryCustomerDirectory(customers);
        TwoLevelCustomerCache warmupCache = new TwoLevelCustomerCache(warmupDelegate, 2, 3);
        CacheWarmupService warmupService = new CacheWarmupService(warmupCache);
        CacheWarmupReport warmupReport = warmupService.warmup(List.of("c-1", "c-2", "missing", "c-1"));
        System.out.println("Warmup report = " + warmupReport);
        warmupCache.findById("c-1");
        System.out.println("Warmup delegate lookups after cached read = " + warmupDelegate.singleLookupCount());

        printSection("Optimization: User Profile Diff");
        UserProfileDiffService diffService = new UserProfileDiffService();
        UserProfileDiff profileDiff = diffService.diff(
                new UserProfile("u-1300", "old@example.com", Region.US),
                new UserProfile("u-1300", "new@example.com", Region.APAC)
        );
        System.out.println("Profile has changes = " + profileDiff.hasChanges());
        System.out.println("Profile changes = " + profileDiff.changes());

        printSection("Optimization: Selective Change Events");
        InMemoryUserProfileChangePublisher changePublisher = new InMemoryUserProfileChangePublisher();
        SelectiveProfileChangePublisher selectivePublisher =
                new SelectiveProfileChangePublisher(new UserProfileDiffService(), changePublisher);
        boolean eventPublished = selectivePublisher.publishIfChanged(
                new UserProfile("u-1400", "before@example.com", Region.US),
                new UserProfile("u-1400", "after@example.com", Region.US)
        );
        System.out.println("Change event published = " + eventPublished);
        System.out.println("Published change events = " + changePublisher.publishedEvents());

        printSection("Optimization: Coalesced Change Events");
        ProfileChangeEventCoalescer eventCoalescer = new ProfileChangeEventCoalescer();
        List<UserProfileChangeEvent> coalescedEvents = eventCoalescer.coalesce(List.of(
                new UserProfileChangeEvent("u-1500", List.of(
                        new FieldChange("email", "old@example.com", "mid@example.com")
                )),
                new UserProfileChangeEvent("u-1500", List.of(
                        new FieldChange("email", "mid@example.com", "new@example.com"),
                        new FieldChange("region", "US", "APAC")
                ))
        ));
        System.out.println("Coalesced change events = " + coalescedEvents);

        printSection("Optimization: Priority Job Queue");
        PriorityJobQueue priorityQueue = new PriorityJobQueue();
        priorityQueue.enqueue(new BackgroundJob("job-low", "send digest email", JobPriority.LOW));
        priorityQueue.enqueue(new BackgroundJob("job-high", "process payment timeout", JobPriority.HIGH));
        priorityQueue.enqueue(new BackgroundJob("job-normal", "refresh search index", JobPriority.NORMAL));
        System.out.println("Priority jobs drained = " + priorityQueue.drain(3));

        printSection("Optimization: Aging Priority Job Queue");
        ManualTimeSource agingQueueClock = new ManualTimeSource(0);
        AgingPriorityJobQueue agingQueue = new AgingPriorityJobQueue(1_000, agingQueueClock);
        agingQueue.enqueue(new BackgroundJob("old-low", "slow report", JobPriority.LOW));
        agingQueueClock.advanceMillis(3_000);
        agingQueue.enqueue(new BackgroundJob("new-high", "urgent sync", JobPriority.HIGH));
        System.out.println("old-low effective weight = " + agingQueue.effectiveWeight("old-low"));
        System.out.println("Aging queue next job = " + agingQueue.poll());

        printSection("Optimization: Deadline Job Queue");
        DeadlineJobQueue deadlineQueue = new DeadlineJobQueue(new ManualTimeSource(2_000));
        deadlineQueue.enqueue(new DeadlineJob(new BackgroundJob("sla-soon", "send SLA alert", JobPriority.LOW), 2_500));
        deadlineQueue.enqueue(new DeadlineJob(new BackgroundJob("sla-later", "archive report", JobPriority.HIGH), 5_000));
        System.out.println("Millis until next deadline = " + deadlineQueue.millisUntilNextDeadline());
        System.out.println("Deadline queue next job = " + deadlineQueue.poll());

        printSection("Optimization: SLA Budget Tracker");
        SlaBudgetTracker slaTracker = new SlaBudgetTracker(0.80, 60_000, new ManualTimeSource(0));
        slaTracker.record(ServiceCallOutcome.SUCCESS);
        slaTracker.record(ServiceCallOutcome.SUCCESS);
        slaTracker.record(ServiceCallOutcome.FAILURE);
        System.out.println("SLA snapshot = " + slaTracker.snapshot());
        System.out.println("SLA budget exhausted = " + slaTracker.snapshot().budgetExhausted());

        printSection("Optimization: Adaptive Retry Controller");
        ManualTimeSource adaptiveRetryClock = new ManualTimeSource(0);
        RetryBudget adaptiveRetryBudget = new RetryBudget(1, 1_000, adaptiveRetryClock);
        SlaBudgetTracker adaptiveSlaTracker = new SlaBudgetTracker(0.80, 60_000, adaptiveRetryClock);
        adaptiveSlaTracker.record(ServiceCallOutcome.SUCCESS);
        adaptiveSlaTracker.record(ServiceCallOutcome.SUCCESS);
        adaptiveSlaTracker.record(ServiceCallOutcome.SUCCESS);
        AdaptiveRetryController adaptiveRetryController =
                new AdaptiveRetryController(adaptiveRetryBudget, adaptiveSlaTracker);
        System.out.println("Adaptive retry preview = " + adaptiveRetryController.preview());
        System.out.println("Adaptive retry acquire = " + adaptiveRetryController.tryAcquireRetry());
        System.out.println("Adaptive retry second acquire = " + adaptiveRetryController.tryAcquireRetry());

        printSection("Optimization: Load Shedding Controller");
        LoadSheddingController loadSheddingController =
                new LoadSheddingController(adaptiveSlaTracker, 2, 4);
        System.out.println("Low priority under pressure = "
                + loadSheddingController.decide(new IncomingRequest("req-low", JobPriority.LOW), 2));
        System.out.println("High priority under pressure = "
                + loadSheddingController.decide(new IncomingRequest("req-high", JobPriority.HIGH), 2));

        printSection("Optimization: Graceful Degradation");
        GracefulDegradationController degradationController =
                new GracefulDegradationController(loadSheddingController);
        System.out.println("Degraded low priority response = "
                + degradationController.decide(new IncomingRequest("req-degrade", JobPriority.LOW), 2, true));
        System.out.println("High priority full response = "
                + degradationController.decide(new IncomingRequest("req-full", JobPriority.HIGH), 2, true));

        printSection("Optimization: Feature Flag Rollout");
        FeatureFlagEvaluator featureFlagEvaluator = new FeatureFlagEvaluator();
        FeatureFlagRule checkoutFlag = new FeatureFlagRule("new-checkout", true, 25);
        System.out.println("Feature flag u-1 = " + featureFlagEvaluator.evaluate(checkoutFlag, "u-1"));
        System.out.println("Feature flag u-2 = " + featureFlagEvaluator.evaluate(checkoutFlag, "u-2"));

        printSection("Optimization: Adaptive Feature Flag Rollout");
        AdaptiveFeatureFlagController adaptiveFlagController =
                new AdaptiveFeatureFlagController(featureFlagEvaluator, adaptiveSlaTracker, 5);
        System.out.println("Adaptive feature flag u-1 = "
                + adaptiveFlagController.evaluate(new FeatureFlagRule("new-checkout", true, 50), "u-1"));

        printSection("Optimization: Feature Flag Registry");
        FeatureFlagRegistry featureFlagRegistry = new FeatureFlagRegistry(List.of(checkoutFlag));
        featureFlagRegistry.upsert(new FeatureFlagRule("recommendations", true, 10));
        System.out.println("Registered flags = " + featureFlagRegistry.snapshot());
        System.out.println("Registry evaluation = "
                + featureFlagRegistry.evaluate("recommendations", "u-1", featureFlagEvaluator));
        System.out.println("Missing flag evaluation = "
                + featureFlagRegistry.evaluate("missing-flag", "u-1", featureFlagEvaluator));

        printSection("Optimization: Feature Flag Config Fingerprint");
        FeatureFlagConfigFingerprinter configFingerprinter = new FeatureFlagConfigFingerprinter();
        FeatureFlagConfigFingerprint currentFingerprint = configFingerprinter.fingerprint(featureFlagRegistry);
        FeatureFlagConfigFingerprint reorderedFingerprint = configFingerprinter.fingerprint(List.of(
                new FeatureFlagRule("recommendations", true, 10),
                checkoutFlag
        ));
        System.out.println("Current feature flag fingerprint = " + currentFingerprint);
        System.out.println("Same config after reorder = " + currentFingerprint.sameConfig(reorderedFingerprint));

        printSection("Optimization: Fingerprinting Feature Flag Reload");
        FeatureFlagRegistry fingerprintReloadRegistry = new FeatureFlagRegistry(List.of(
                checkoutFlag,
                new FeatureFlagRule("recommendations", true, 10)
        ));
        FingerprintingFeatureFlagReloader fingerprintingReloader = new FingerprintingFeatureFlagReloader(
                fingerprintReloadRegistry,
                configFingerprinter,
                new SafeFeatureFlagReloader(fingerprintReloadRegistry, new FeatureFlagReloadValidator(25, 10))
        );
        FingerprintingFeatureFlagReloadResult fingerprintReloadResult =
                fingerprintingReloader.reloadIfChanged(List.of(
                        new FeatureFlagRule("recommendations", true, 10),
                        checkoutFlag
                ));
        System.out.println("Fingerprint reload skipped = " + fingerprintReloadResult.skipped());
        System.out.println("Fingerprint reload result = " + fingerprintReloadResult);

        printSection("Optimization: Rate Limited Feature Flag Reload");
        RateLimitedFeatureFlagReloader rateLimitedFlagReloader = new RateLimitedFeatureFlagReloader(
                "feature-flags",
                new TokenBucketRateLimiter(1, 1.0, new ManualTimeSource(0)),
                fingerprintingReloader
        );
        RateLimitedFeatureFlagReloadResult firstRateLimitedReload =
                rateLimitedFlagReloader.reloadIfAllowed(List.of(
                        new FeatureFlagRule("new-checkout", true, 30),
                        new FeatureFlagRule("recommendations", true, 10)
                ));
        RateLimitedFeatureFlagReloadResult secondRateLimitedReload =
                rateLimitedFlagReloader.reloadIfAllowed(List.of(
                        new FeatureFlagRule("new-checkout", true, 35),
                        new FeatureFlagRule("recommendations", true, 10)
                ));
        System.out.println("First rate-limited reload = " + firstRateLimitedReload);
        System.out.println("Second rate-limited reload blocked = " + secondRateLimitedReload.blocked());

        printSection("Optimization: Debounced Feature Flag Reload");
        ManualTimeSource debounceClock = new ManualTimeSource(0);
        FeatureFlagRegistry debounceRegistry = new FeatureFlagRegistry(List.of(checkoutFlag));
        FingerprintingFeatureFlagReloader debounceFingerprintingReloader = new FingerprintingFeatureFlagReloader(
                debounceRegistry,
                configFingerprinter,
                new SafeFeatureFlagReloader(debounceRegistry, new FeatureFlagReloadValidator(25, 10))
        );
        DebouncedFeatureFlagReloader debouncedReloader = new DebouncedFeatureFlagReloader(
                500,
                debounceClock,
                new RateLimitedFeatureFlagReloader(
                        "feature-flags",
                        new TokenBucketRateLimiter(2, 1.0, debounceClock),
                        debounceFingerprintingReloader
                )
        );
        debouncedReloader.submit(List.of(new FeatureFlagRule("new-checkout", true, 30)));
        debounceClock.advanceMillis(250);
        debouncedReloader.submit(List.of(new FeatureFlagRule("new-checkout", true, 35)));
        System.out.println("Debounced reload before due = " + debouncedReloader.flushIfDue());
        debounceClock.advanceMillis(500);
        System.out.println("Debounced reload after quiet period = " + debouncedReloader.flushIfDue());

        printSection("Optimization: Instrumented Debounced Feature Flag Reload");
        FeatureFlagReloadMetrics reloadMetrics = new FeatureFlagReloadMetrics();
        InstrumentedDebouncedFeatureFlagReloader instrumentedDebouncedReloader =
                new InstrumentedDebouncedFeatureFlagReloader(debouncedReloader, reloadMetrics);
        instrumentedDebouncedReloader.flushIfDue();
        instrumentedDebouncedReloader.submit(List.of(new FeatureFlagRule("new-checkout", true, 40)));
        System.out.println("Reload metrics after submit/wait = "
                + instrumentedDebouncedReloader.metricsSnapshot());

        printSection("Optimization: Feature Flag Reload Health Analyzer");
        FeatureFlagReloadHealthAnalyzer reloadHealthAnalyzer =
                new FeatureFlagReloadHealthAnalyzer(0.20, 0.50, 0.20, 0.50);
        FeatureFlagReloadHealthReport reloadHealthReport =
                reloadHealthAnalyzer.analyze(instrumentedDebouncedReloader.metricsSnapshot());
        System.out.println("Reload health report = " + reloadHealthReport);

        printSection("Optimization: Feature Flag Reload Alert Policy");
        FeatureFlagReloadAlertPolicy reloadAlertPolicy = new FeatureFlagReloadAlertPolicy(true);
        FeatureFlagReloadAlert reloadAlert = reloadAlertPolicy.evaluate(reloadHealthReport);
        System.out.println("Reload alert = " + reloadAlert);

        printSection("Optimization: Feature Flag Reload Alert Suppressor");
        FeatureFlagReloadAlertSuppressor reloadAlertSuppressor =
                new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(0));
        FeatureFlagReloadAlertDecision firstAlertDecision = reloadAlertSuppressor.evaluate(reloadAlert);
        System.out.println("First alert decision = " + firstAlertDecision);
        System.out.println("Duplicate alert decision = " + reloadAlertSuppressor.evaluate(reloadAlert));

        printSection("Optimization: Feature Flag Reload Alert Router");
        FeatureFlagReloadAlertRouter reloadAlertRouter = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertRoute reloadAlertRoute = reloadAlertRouter.route(firstAlertDecision);
        System.out.println("Reload alert route = " + reloadAlertRoute);

        printSection("Optimization: Feature Flag Reload Alert Dispatcher");
        InMemoryFeatureFlagReloadAlertSink reloadAlertSink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDispatcher reloadAlertDispatcher =
                new FeatureFlagReloadAlertDispatcher(reloadAlertSink);
        FeatureFlagReloadAlertDispatchResult reloadAlertDispatch =
                reloadAlertDispatcher.dispatch(reloadAlertRoute);
        System.out.println("Reload alert dispatch = " + reloadAlertDispatch);
        System.out.println("Delivered reload alerts = " + reloadAlertSink.findAll());

        printSection("Optimization: Feature Flag Reload Alert Retry Policy");
        FeatureFlagReloadAlertRetryPolicy reloadAlertRetryPolicy =
                new FeatureFlagReloadAlertRetryPolicy(3, 250, 2.0, new ManualTimeSource(1_000));
        reloadAlertDispatch.delivery().ifPresent(delivery ->
                System.out.println("Reload alert retry plan = "
                        + reloadAlertRetryPolicy.planFailure(delivery, 1)));

        printSection("Optimization: Feature Flag Reload Alert Dead Letter");
        FeatureFlagReloadAlertDeadLetterStore reloadAlertDeadLetters =
                new FeatureFlagReloadAlertDeadLetterStore(5);
        reloadAlertDispatch.delivery().ifPresent(delivery -> {
            FeatureFlagReloadAlertRetryPlan exhaustedPlan =
                    reloadAlertRetryPolicy.planFailure(delivery, 3);
            reloadAlertDeadLetters.record(delivery, exhaustedPlan);
        });
        System.out.println("Reload alert dead letters = " + reloadAlertDeadLetters.findAll());

        printSection("Optimization: Feature Flag Reload Alert Dead-Letter Replay");
        FeatureFlagReloadAlertDeadLetterReplayer reloadAlertReplayer =
                new FeatureFlagReloadAlertDeadLetterReplayer(reloadAlertDeadLetters, reloadAlertDispatcher);
        System.out.println("Reload alert replay result = " + reloadAlertReplayer.replay(5));
        System.out.println("Delivered reload alerts after replay = " + reloadAlertSink.findAll());

        printSection("Optimization: Feature Flag Reload Alert Replay Cleanup");
        FeatureFlagReloadAlertDeadLetterReplayCoordinator reloadAlertReplayCoordinator =
                new FeatureFlagReloadAlertDeadLetterReplayCoordinator(
                        reloadAlertDeadLetters,
                        reloadAlertDispatcher
                );
        System.out.println("Reload alert replay cleanup = "
                + reloadAlertReplayCoordinator.replayAndRemoveDelivered(5));
        System.out.println("Reload alert dead letters after cleanup = " + reloadAlertDeadLetters.findAll());

        printSection("Optimization: Feature Flag Reload Alert Dead-Letter Monitor");
        FeatureFlagReloadAlertDeadLetterMonitor reloadAlertDeadLetterMonitor =
                new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0);
        System.out.println("Reload alert dead-letter health = "
                + reloadAlertDeadLetterMonitor.analyze(reloadAlertDeadLetters));

        printSection("Optimization: Feature Flag Reload Alert Dead-Letter Alert Policy");
        FeatureFlagReloadAlertDeadLetterAlertPolicy reloadAlertDeadLetterAlertPolicy =
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(true);
        System.out.println("Reload alert dead-letter alert = "
                + reloadAlertDeadLetterAlertPolicy.evaluate(
                        reloadAlertDeadLetterMonitor.analyze(reloadAlertDeadLetters)));

        printSection("Optimization: Feature Flag Reload Alert Dead-Letter Alert Workflow");
        FeatureFlagReloadAlertDeadLetterAlertWorkflow reloadAlertDeadLetterAlertWorkflow =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                        reloadAlertDeadLetterMonitor,
                        reloadAlertDeadLetterAlertPolicy,
                        new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(0)),
                        reloadAlertRouter,
                        reloadAlertDispatcher
                );
        System.out.println("Reload alert dead-letter workflow = "
                + reloadAlertDeadLetterAlertWorkflow.run(reloadAlertDeadLetters));

        printSection("Optimization: Instrumented Reload Alert Dead-Letter Workflow");
        InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow instrumentedDeadLetterAlertWorkflow =
                new InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow(
                        reloadAlertDeadLetterAlertWorkflow,
                        new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics()
                );
        instrumentedDeadLetterAlertWorkflow.run(reloadAlertDeadLetters);
        System.out.println("Reload alert dead-letter workflow metrics = "
                + instrumentedDeadLetterAlertWorkflow.metricsSnapshot());

        printSection("Optimization: Reload Alert Dead-Letter Workflow Health Analyzer");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer deadLetterAlertWorkflowHealthAnalyzer =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(0.25, 0.75, 0.5, 0.8);
        System.out.println("Reload alert dead-letter workflow health = "
                + deadLetterAlertWorkflowHealthAnalyzer.analyze(
                        instrumentedDeadLetterAlertWorkflow.metricsSnapshot()));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Alert Policy");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy deadLetterAlertWorkflowAlertPolicy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(true);
        System.out.println("Reload alert dead-letter workflow alert = "
                + deadLetterAlertWorkflowAlertPolicy.evaluate(
                        deadLetterAlertWorkflowHealthAnalyzer.analyze(
                                instrumentedDeadLetterAlertWorkflow.metricsSnapshot())));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Alert Pipeline");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline deadLetterAlertWorkflowAlertPipeline =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
                        deadLetterAlertWorkflowHealthAnalyzer,
                        deadLetterAlertWorkflowAlertPolicy,
                        new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(0)),
                        reloadAlertRouter,
                        reloadAlertDispatcher
                );
        System.out.println("Reload alert dead-letter workflow alert pipeline = "
                + deadLetterAlertWorkflowAlertPipeline.run(
                        instrumentedDeadLetterAlertWorkflow.metricsSnapshot()));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Log");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog deadLetterAlertWorkflowIncidentLog =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(5);
        deadLetterAlertWorkflowIncidentLog.record(deadLetterAlertWorkflowAlertPipeline.run(
                instrumentedDeadLetterAlertWorkflow.metricsSnapshot()));
        System.out.println("Reload alert dead-letter workflow incidents = "
                + deadLetterAlertWorkflowIncidentLog.findAll());

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Log Monitor");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor incidentLogMonitor =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(0.5, 0.9, 1, 0);
        System.out.println("Reload alert dead-letter workflow incident log health = "
                + incidentLogMonitor.analyze(deadLetterAlertWorkflowIncidentLog));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Log Alert Policy");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy incidentLogAlertPolicy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(true);
        System.out.println("Reload alert dead-letter workflow incident log alert = "
                + incidentLogAlertPolicy.evaluate(incidentLogMonitor.analyze(deadLetterAlertWorkflowIncidentLog)));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Log Alert Pipeline");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline incidentLogAlertPipeline =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
                        incidentLogMonitor,
                        incidentLogAlertPolicy,
                        new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(0)),
                        reloadAlertRouter,
                        reloadAlertDispatcher
                );
        System.out.println("Reload alert dead-letter workflow incident log alert pipeline = "
                + incidentLogAlertPipeline.run(deadLetterAlertWorkflowIncidentLog));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Log Summary");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer incidentLogSummarizer =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer();
        System.out.println("Reload alert dead-letter workflow incident log summary = "
                + incidentLogSummarizer.summarize(deadLetterAlertWorkflowIncidentLog));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Triage Planner");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner incidentTriagePlanner =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner();
        System.out.println("Reload alert dead-letter workflow incident triage = "
                + incidentTriagePlanner.plan(incidentLogSummarizer.summarize(deadLetterAlertWorkflowIncidentLog)));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Triage Formatter");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter incidentTriageFormatter =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter();
        System.out.println(incidentTriageFormatter.format(
                incidentTriagePlanner.plan(incidentLogSummarizer.summarize(deadLetterAlertWorkflowIncidentLog))));

        printSection("Optimization: Reload Alert Dead-Letter Workflow Incident Triage Digest");
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder incidentTriageDigestBuilder =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder(
                        incidentLogSummarizer,
                        incidentTriagePlanner,
                        incidentTriageFormatter,
                        new ManualTimeSource(2_000)
                );
        System.out.println("Reload alert dead-letter workflow incident triage digest = "
                + incidentTriageDigestBuilder.build(deadLetterAlertWorkflowIncidentLog));

        printSection("Optimization: Feature Flag Reload Validation");
        FeatureFlagReloadValidator reloadValidator = new FeatureFlagReloadValidator(25, 10);
        FeatureFlagReloadValidationReport validationReport = reloadValidator.validate(featureFlagRegistry, List.of(
                new FeatureFlagRule("new-checkout", true, 80),
                new FeatureFlagRule("experimental-search", true, 50)
        ));
        System.out.println("Feature flag reload validation = " + validationReport);
        System.out.println("Feature flag reload accepted = " + validationReport.accepted());

        printSection("Optimization: Safe Feature Flag Reload");
        SafeFeatureFlagReloader safeFlagReloader = new SafeFeatureFlagReloader(
                featureFlagRegistry,
                new FeatureFlagReloadValidator(25, 10)
        );
        SafeFeatureFlagReloadResult safeReloadResult = safeFlagReloader.reload(List.of(
                new FeatureFlagRule("new-checkout", true, 30),
                new FeatureFlagRule("recommendations", true, 10),
                new FeatureFlagRule("profile-v2", true, 5)
        ));
        System.out.println("Safe feature flag reload applied = " + safeReloadResult.applied());
        System.out.println("Safe feature flag reload result = " + safeReloadResult);

        printSection("Optimization: Feature Flag Reload Diff");
        FeatureFlagReloader flagReloader = new FeatureFlagReloader(featureFlagRegistry);
        FeatureFlagReloadReport reloadReport = flagReloader.reload(List.of(
                new FeatureFlagRule("new-checkout", true, 25),
                new FeatureFlagRule("recommendations", true, 25),
                new FeatureFlagRule("profile-v2", true, 5)
        ));
        System.out.println("Feature flag reload report = " + reloadReport);

        printSection("Optimization: Feature Flag Audit Log");
        InMemoryFeatureFlagAuditLog flagAuditLog = new InMemoryFeatureFlagAuditLog();
        AuditedFeatureFlagReloader auditedFlagReloader = new AuditedFeatureFlagReloader(
                new FeatureFlagReloader(featureFlagRegistry),
                flagAuditLog,
                new ManualTimeSource(10_000)
        );
        auditedFlagReloader.reload(List.of(
                new FeatureFlagRule("new-checkout", true, 25),
                new FeatureFlagRule("recommendations", true, 50)
        ));
        System.out.println("Feature flag audit events = " + flagAuditLog.findAll());

        printSection("Optimization: Feature Flag Rollback Plan");
        FeatureFlagRollbackPlanner rollbackPlanner = new FeatureFlagRollbackPlanner();
        if (!flagAuditLog.findAll().isEmpty()) {
            System.out.println("Feature flag rollback plan = "
                    + rollbackPlanner.plan(flagAuditLog.findAll().get(0)));
        }

        printSection("Optimization: Versioned Feature Flag Snapshots");
        FeatureFlagSnapshotStore snapshotStore = new FeatureFlagSnapshotStore();
        snapshotStore.save(FeatureFlagSnapshot.fromRules(1, List.of(checkoutFlag)));
        snapshotStore.save(FeatureFlagSnapshot.fromRules(2, List.of(
                new FeatureFlagRule("new-checkout", true, 75),
                new FeatureFlagRule("recommendations", true, 25)
        )));
        FeatureFlagRegistry restoredFlags = snapshotStore.restore(1);
        System.out.println("Latest feature flag snapshot = " + snapshotStore.latest());
        System.out.println("Restored checkout flag = " + restoredFlags.find("new-checkout"));

        printSection("Optimization: Feature Flag Snapshot Retention");
        FeatureFlagSnapshotRetentionPolicy retentionPolicy = new FeatureFlagSnapshotRetentionPolicy(1);
        System.out.println("Snapshot retention report = "
                + retentionPolicy.apply(snapshotStore.history()));
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
