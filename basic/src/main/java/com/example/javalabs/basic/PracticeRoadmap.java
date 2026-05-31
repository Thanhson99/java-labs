package com.example.javalabs.basic;

import java.util.List;

/**
 * Maps the static learning-site roadmap to concrete Java files in this module.
 *
 * <p>The learning site stores theory and interview guidance in JSON. This class turns the same
 * learning path into a code-first checklist: read the source file, read the matching test, run it,
 * then change one small thing and observe the behavior.</p>
 */
public final class PracticeRoadmap {

    /**
     * Utility class; roadmap data is exposed through {@link #stages()}.
     */
    private PracticeRoadmap() {
    }

    /**
     * Returns the complete code-first learning roadmap.
     *
     * <p>Each stage links source files, tests, and small exercises so the learning site JSON has a
     * matching hands-on path inside the Java module.</p>
     *
     * @return immutable stage definitions ordered from beginner to advanced practice
     */
    public static List<PracticeStage> stages() {
        return List.of(
                new PracticeStage(
                        1,
                        "Java syntax and control flow",
                        "Understand variables, methods, if/else, loops, and simple validation.",
                        List.of(
                                "ControlFlowExamples.java",
                                "StringToolkit.java"
                        ),
                        List.of(
                                "ControlFlowExamplesTest.java",
                                "StringToolkitTest.java"
                        ),
                        List.of(
                                "Predict the output before running the test.",
                                "Change one input value in LearningApp and explain the new output.",
                                "Add one edge-case test for a negative or blank input."
                        )
                ),
                new PracticeStage(
                        2,
                        "Objects, encapsulation, and invariants",
                        "Read how an object protects its own valid state.",
                        List.of(
                                "BankAccount.java",
                                "ExceptionPlayground.java"
                        ),
                        List.of(
                                "BankAccountTest.java",
                                "ExceptionPlaygroundTest.java"
                        ),
                        List.of(
                                "Trace constructor validation before looking at the test result.",
                                "Explain why balance is private and changed only through methods.",
                                "Try to withdraw too much money and identify the exception boundary."
                        )
                ),
                new PracticeStage(
                        3,
                        "Records, collections, streams, and sorting",
                        "Practice reading data transformations from input list to output result.",
                        List.of(
                                "Student.java",
                                "StudentAnalytics.java",
                                "InventoryItem.java",
                                "InventoryAnalytics.java"
                        ),
                        List.of(
                                "StudentAnalyticsTest.java",
                                "InventoryAnalyticsTest.java"
                        ),
                        List.of(
                                "Draw the input collection and expected output before reading the stream code.",
                                "Add a student or item and predict which assertion changes.",
                                "Rewrite one stream pipeline as a loop to compare both styles."
                        )
                ),
                new PracticeStage(
                        4,
                        "Interfaces, enums, generics, and polymorphism",
                        "Understand contracts and reusable types before moving into frameworks.",
                        List.of(
                                "Shape.java",
                                "Circle.java",
                                "Rectangle.java",
                                "DifficultyLevel.java",
                                "Pair.java"
                        ),
                        List.of(
                                "ShapeTest.java",
                                "DifficultyLevelTest.java",
                                "PairTest.java"
                        ),
                        List.of(
                                "Explain which behavior is promised by the interface.",
                                "Add one enum value and update the test expectation.",
                                "Create one Pair with different generic types and explain compile-time safety."
                        )
                ),
                new PracticeStage(
                        5,
                        "File I/O, JDBC, and resource handling",
                        "Follow how Java touches files, SQL, and resources that must be closed.",
                        List.of(
                                "FileReport.java",
                                "JdbcExamples.java",
                                "JdbcUserRecord.java"
                        ),
                        List.of(
                                "FileReportTest.java",
                                "JdbcExamplesTest.java"
                        ),
                        List.of(
                                "Read the test setup first because it creates the file or database data.",
                                "Identify where input becomes SQL parameters.",
                                "Explain why try-with-resources matters for external resources."
                        )
                ),
                new PracticeStage(
                        6,
                        "Rate limiting, pooling, and multi-database routing",
                        "Practice backend building blocks that appear behind real APIs.",
                        List.of(
                                "FixedWindowRateLimiter.java",
                                "SimpleConnectionPool.java",
                                "FakeDatabaseConnection.java",
                                "MultiDatabaseUserProfileRepository.java"
                        ),
                        List.of(
                                "FixedWindowRateLimiterTest.java",
                                "SimpleConnectionPoolTest.java",
                                "MultiDatabaseUserProfileRepositoryTest.java"
                        ),
                        List.of(
                                "Step through the state changes after every method call.",
                                "Change the window size or pool size and predict the next failing assertion.",
                                "Explain which class owns routing decisions and which class only stores data."
                        )
                ),
                new PracticeStage(
                        7,
                        "Async code and service orchestration",
                        "Read a small service flow from input boundary to side effect.",
                        List.of(
                                "AsyncExamples.java",
                                "RegistrationService.java",
                                "UserProfileRepository.java",
                                "NotificationClient.java"
                        ),
                        List.of(
                                "AsyncExamplesTest.java",
                                "RegistrationServiceTest.java"
                        ),
                        List.of(
                                "Trace success and failure paths separately.",
                                "Explain which dependency is real business logic and which is infrastructure.",
                                "Add one test for a downstream failure before changing production code."
                        )
                ),
                new PracticeStage(
                        8,
                        "Performance thinking and batch loading",
                        "Compare simple code with optimized code while keeping the same business result.",
                        List.of(
                                "Order.java",
                                "Customer.java",
                                "CustomerDirectory.java",
                                "InMemoryCustomerDirectory.java",
                                "OrderSummary.java",
                                "OrderSummaryService.java"
                        ),
                        List.of(
                                "OrderSummaryServiceTest.java"
                        ),
                        List.of(
                                "Run the repeated-lookup path and count how many data access calls it makes.",
                                "Run the batch-lookup path and confirm the summaries stay the same.",
                                "Explain why fewer database calls usually matter more than micro-optimizing a loop."
                        )
                ),
                new PracticeStage(
                        9,
                        "Caching with TTL and invalidation",
                        "Reduce repeated reads while keeping stale data bounded by time.",
                        List.of(
                                "CachedCustomerDirectory.java",
                                "CustomerDirectory.java",
                                "InMemoryCustomerDirectory.java",
                                "TimeSource.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "CachedCustomerDirectoryTest.java"
                        ),
                        List.of(
                                "Read the first test and explain why two reads only hit the delegate once.",
                                "Advance ManualTimeSource past the TTL and explain why the next read reloads.",
                                "Call invalidate after a simulated write and verify the next read refreshes data."
                        )
                ),
                new PracticeStage(
                        10,
                        "Retry boundaries for downstream calls",
                        "Handle temporary failures without hiding permanent failures or retrying forever.",
                        List.of(
                                "NotificationClient.java",
                                "FlakyNotificationClient.java",
                                "ResilientNotificationClient.java",
                                "RegistrationService.java"
                        ),
                        List.of(
                                "ResilientNotificationClientTest.java",
                                "RegistrationServiceTest.java"
                        ),
                        List.of(
                                "Make the flaky client fail twice and confirm the third attempt succeeds.",
                                "Set maxAttempts too low and confirm the wrapper fails fast enough.",
                                "Explain why retry belongs near infrastructure, not inside every business service method."
                        )
                ),
                new PracticeStage(
                        11,
                        "Circuit breaker for failing dependencies",
                        "Fail fast when a downstream service keeps failing, then probe recovery after cooldown.",
                        List.of(
                                "CircuitBreakerState.java",
                                "CircuitBreakerNotificationClient.java",
                                "FlakyNotificationClient.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "CircuitBreakerNotificationClientTest.java"
                        ),
                        List.of(
                                "Cause two failures and explain why the breaker opens.",
                                "Call again while open and confirm the delegate is not touched.",
                                "Advance ManualTimeSource past cooldown and explain the half-open trial."
                        )
                ),
                new PracticeStage(
                        12,
                        "Measure client calls before optimizing",
                        "Collect call count, failures, and latency so performance work is based on evidence.",
                        List.of(
                                "ClientCallMetrics.java",
                                "InstrumentedNotificationClient.java",
                                "LatencySimulatingNotificationClient.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "InstrumentedNotificationClientTest.java"
                        ),
                        List.of(
                                "Make two successful calls and calculate average duration by hand.",
                                "Force one failure and confirm metrics still count the call duration.",
                                "Explain why measuring latency before changing code is safer than guessing."
                        )
                ),
                new PracticeStage(
                        13,
                        "Bounded cache and memory control",
                        "Limit cache size so optimization does not turn into unbounded memory growth.",
                        List.of(
                                "CachedCustomerDirectory.java",
                                "InMemoryCustomerDirectory.java",
                                "Customer.java"
                        ),
                        List.of(
                                "CachedCustomerDirectoryTest.java"
                        ),
                        List.of(
                                "Create a cache with maxEntries set to 2.",
                                "Read three customers and explain which entry is evicted.",
                                "Explain why TTL and max size solve different cache risks."
                        )
                ),
                new PracticeStage(
                        14,
                        "Paging large reads",
                        "Process data in chunks so large exports do not require loading everything at once.",
                        List.of(
                                "PageRequest.java",
                                "Page.java",
                                "OrderRepository.java",
                                "InMemoryOrderRepository.java",
                                "OrderExportService.java",
                                "OrderExportReport.java"
                        ),
                        List.of(
                                "OrderExportServiceTest.java"
                        ),
                        List.of(
                                "Compare exportByLoadingAll with exportInPages.",
                                "Change page size and predict how many repository calls happen.",
                                "Explain why paging protects memory even when it uses more repository calls."
                        )
                ),
                new PracticeStage(
                        15,
                        "Cursor paging for deep reads",
                        "Use last-seen cursors instead of large offsets when reading deep ordered datasets.",
                        List.of(
                                "CursorPageRequest.java",
                                "CursorPage.java",
                                "CursorOrderRepository.java",
                                "InMemoryCursorOrderRepository.java",
                                "CursorOrderExportService.java"
                        ),
                        List.of(
                                "CursorOrderExportServiceTest.java"
                        ),
                        List.of(
                                "Read the first cursor page and identify the next cursor.",
                                "Pass the next cursor into the second request and verify no page number is needed.",
                                "Explain why cursor pagination is often better than offset pagination for deep pages."
                        )
                ),
                new PracticeStage(
                        16,
                        "Idempotent side effects",
                        "Prevent duplicate notifications when retries or message redelivery repeat the same operation.",
                        List.of(
                                "IdempotencyStore.java",
                                "InMemoryIdempotencyStore.java",
                                "IdempotentNotificationClient.java",
                                "NotificationClient.java"
                        ),
                        List.of(
                                "IdempotentNotificationClientTest.java"
                        ),
                        List.of(
                                "Call the same welcome notification twice and confirm only one message is sent.",
                                "Change the user id and explain why the idempotency key changes.",
                                "Explain why idempotency is critical when retry and message redelivery exist."
                        )
                ),
                new PracticeStage(
                        17,
                        "Bulkhead concurrency limits",
                        "Protect the app from too many simultaneous calls to a slow dependency.",
                        List.of(
                                "BulkheadNotificationClient.java",
                                "BlockingNotificationClient.java",
                                "NotificationClient.java"
                        ),
                        List.of(
                                "BulkheadNotificationClientTest.java"
                        ),
                        List.of(
                                "Hold one notification call in flight and confirm the next call is rejected.",
                                "Force the delegate to fail and confirm the bulkhead slot is released.",
                                "Explain how bulkhead differs from retry and circuit breaker."
                        )
                ),
                new PracticeStage(
                        18,
                        "Timeout boundaries for slow calls",
                        "Set a time budget for downstream calls so slow dependencies do not hold requests forever.",
                        List.of(
                                "TimeoutNotificationClient.java",
                                "LatencySimulatingNotificationClient.java",
                                "ManualTimeSource.java",
                                "NotificationClient.java"
                        ),
                        List.of(
                                "TimeoutNotificationClientTest.java"
                        ),
                        List.of(
                                "Run a fast simulated call and confirm it passes.",
                                "Run a slow simulated call and confirm timeout is reported.",
                                "Explain why production timeouts should be enforced by real network clients too."
                        )
                ),
                new PracticeStage(
                        19,
                        "Batching small downstream calls",
                        "Group small operations together to reduce downstream round trips.",
                        List.of(
                                "NotificationBatchClient.java",
                                "InMemoryNotificationBatchClient.java",
                                "WelcomeNotificationBatcher.java"
                        ),
                        List.of(
                                "WelcomeNotificationBatcherTest.java"
                        ),
                        List.of(
                                "Enqueue two messages with batch size two and confirm automatic flush.",
                                "Flush a partial batch manually and confirm no message is lost.",
                                "Explain the latency versus throughput trade-off of batching."
                        )
                ),
                new PracticeStage(
                        20,
                        "Dead-letter capture for failed side effects",
                        "Store failed downstream work so it can be inspected or replayed later.",
                        List.of(
                                "DeadLetterMessage.java",
                                "DeadLetterStore.java",
                                "InMemoryDeadLetterStore.java",
                                "DeadLetteringNotificationClient.java"
                        ),
                        List.of(
                                "DeadLetteringNotificationClientTest.java"
                        ),
                        List.of(
                                "Force a notification failure and inspect the stored dead-letter message.",
                                "Run a successful notification and confirm no dead-letter is created.",
                                "Explain why failed side effects should be queryable, not only logged."
                        )
                ),
                new PracticeStage(
                        21,
                        "Optimistic locking for concurrent writes",
                        "Reject stale writes so concurrent requests do not overwrite each other silently.",
                        List.of(
                                "VersionedInventoryItem.java",
                                "VersionedInventoryRepository.java",
                                "InMemoryVersionedInventoryRepository.java",
                                "OptimisticLockException.java",
                                "StockReservationService.java"
                        ),
                        List.of(
                                "StockReservationServiceTest.java"
                        ),
                        List.of(
                                "Read the same SKU twice and save the first update.",
                                "Try to save the second stale update and confirm a version conflict.",
                                "Explain why optimistic locking protects data without locking every read."
                        )
                ),
                new PracticeStage(
                        22,
                        "Outbox pattern for reliable publishing",
                        "Write domain data and a publishable event together, then dispatch the event later.",
                        List.of(
                                "OutboxEvent.java",
                                "OutboxEventStore.java",
                                "InMemoryOutboxEventStore.java",
                                "OutboxRegistrationService.java",
                                "OutboxDispatcher.java",
                                "OutboxEventPublisher.java"
                        ),
                        List.of(
                                "OutboxRegistrationServiceTest.java"
                        ),
                        List.of(
                                "Register a user and confirm a pending outbox event exists.",
                                "Dispatch the pending event and confirm it becomes published.",
                                "Force publish failure and explain why the event can be retried later."
                        )
                ),
                new PracticeStage(
                        23,
                        "Outbox retry backoff",
                        "Delay retries progressively so a failing dependency is not hammered in a tight loop.",
                        List.of(
                                "OutboxRetryPolicy.java",
                                "OutboxRetryPlan.java",
                                "OutboxRetryPlanner.java",
                                "OutboxEvent.java"
                        ),
                        List.of(
                                "OutboxRetryPolicyTest.java"
                        ),
                        List.of(
                                "Calculate retry delay for attempt counts 0, 1, 2, and 3.",
                                "Confirm retry stops after maxAttempts.",
                                "Explain why backoff matters when many outbox events fail at the same time."
                        )
                ),
                new PracticeStage(
                        24,
                        "Deduplicate batch lookups",
                        "Remove duplicate ids before querying, then restore the caller's original order.",
                        List.of(
                                "CustomerLookupService.java",
                                "CustomerLookupResult.java",
                                "CustomerDirectory.java",
                                "InMemoryCustomerDirectory.java"
                        ),
                        List.of(
                                "CustomerLookupServiceTest.java"
                        ),
                        List.of(
                                "Request customers with repeated ids and count unique lookups.",
                                "Confirm the returned customer list still follows the original request order.",
                                "Explain why de-duplication is useful before database, cache, or HTTP batch calls."
                        )
                ),
                new PracticeStage(
                        25,
                        "Top N without full sorting",
                        "Use a bounded heap when only the best N items are needed from a large list.",
                        List.of(
                                "TopInventorySelector.java",
                                "InventoryItem.java"
                        ),
                        List.of(
                                "TopInventorySelectorTest.java"
                        ),
                        List.of(
                                "Compare topByFullSort and topByBoundedHeap for the same input.",
                                "Change the limit and predict which items stay in the heap.",
                                "Explain why heap size N is useful when the input list is much larger than N."
                        )
                ),
                new PracticeStage(
                        26,
                        "One-pass aggregation",
                        "Calculate summary statistics without extra intermediate collections.",
                        List.of(
                                "InventoryStockSummarizer.java",
                                "InventoryStockSummary.java",
                                "InventoryItem.java"
                        ),
                        List.of(
                                "InventoryStockSummarizerTest.java"
                        ),
                        List.of(
                                "Trace count, total, min, and max through one loop.",
                                "Change quantities and calculate the average before running the test.",
                                "Explain why one-pass aggregation helps when processing large lists or streams."
                        )
                ),
                new PracticeStage(
                        27,
                        "Indexed lookups",
                        "Build an index for repeated lookups by a secondary field.",
                        List.of(
                                "IndexedInventoryCatalog.java",
                                "InventoryItem.java"
                        ),
                        List.of(
                                "IndexedInventoryCatalogTest.java"
                        ),
                        List.of(
                                "Compare indexed lookup with scanning lookup for the same category.",
                                "Add a new category and predict indexedCategoryCount.",
                                "Explain the memory versus lookup-speed trade-off of prebuilt indexes."
                        )
                ),
                new PracticeStage(
                        28,
                        "Chunked batch processing",
                        "Split large inputs into bounded chunks before calling downstream systems.",
                        List.of(
                                "BatchPartitioner.java",
                                "ChunkedNotificationSender.java",
                                "NotificationBatchClient.java"
                        ),
                        List.of(
                                "BatchPartitionerTest.java"
                        ),
                        List.of(
                                "Partition five items with chunk size two and predict the chunks.",
                                "Send five notifications with chunk size two and count downstream calls.",
                                "Explain why chunking protects APIs with request-size or timeout limits."
                        )
                ),
                new PracticeStage(
                        29,
                        "Sliding-window rate limiting",
                        "Limit requests using recent timestamps instead of coarse fixed windows.",
                        List.of(
                                "SlidingWindowRateLimiter.java",
                                "FixedWindowRateLimiter.java",
                                "ManualTimeSource.java",
                                "TimeSource.java"
                        ),
                        List.of(
                                "SlidingWindowRateLimiterTest.java",
                                "FixedWindowRateLimiterTest.java"
                        ),
                        List.of(
                                "Compare fixed-window reset behavior with sliding-window expiration.",
                                "Advance ManualTimeSource until only the oldest request expires.",
                                "Explain why sliding windows reduce burstiness at window boundaries."
                        )
                ),
                new PracticeStage(
                        30,
                        "Token-bucket rate limiting",
                        "Allow controlled bursts while enforcing a steady refill rate over time.",
                        List.of(
                                "TokenBucketRateLimiter.java",
                                "SlidingWindowRateLimiter.java",
                                "ManualTimeSource.java",
                                "TimeSource.java"
                        ),
                        List.of(
                                "TokenBucketRateLimiterTest.java",
                                "SlidingWindowRateLimiterTest.java"
                        ),
                        List.of(
                                "Consume all initial tokens and confirm the next request is rejected.",
                                "Advance ManualTimeSource and confirm tokens refill gradually.",
                                "Explain when token bucket is better than fixed or sliding windows."
                        )
                ),
                new PracticeStage(
                        31,
                        "Skip no-op writes",
                        "Detect unchanged updates so the service avoids unnecessary repository writes.",
                        List.of(
                                "UserProfileUpdateService.java",
                                "ProfileUpdateResult.java",
                                "CountingUserProfileRepository.java",
                                "UserProfileRepository.java"
                        ),
                        List.of(
                                "UserProfileUpdateServiceTest.java"
                        ),
                        List.of(
                                "Update a profile with the same normalized email and confirm saveCount stays zero.",
                                "Update with a different email and confirm exactly one save occurs.",
                                "Explain why skipping no-op writes reduces DB load and avoids unnecessary side effects."
                        )
                ),
                new PracticeStage(
                        32,
                        "Incremental aggregation",
                        "Update a summary as data changes instead of recomputing from the full list every time.",
                        List.of(
                                "IncrementalInventorySummary.java",
                                "InventoryStockSummary.java",
                                "InventoryItem.java"
                        ),
                        List.of(
                                "IncrementalInventorySummaryTest.java"
                        ),
                        List.of(
                                "Add items and trace how total, min, and max change.",
                                "Update an existing item and confirm item count does not grow.",
                                "Explain when incremental state is worth the extra bookkeeping."
                        )
                ),
                new PracticeStage(
                        33,
                        "Coalesce duplicate in-flight lookups",
                        "Share one downstream lookup when concurrent callers request the same key.",
                        List.of(
                                "CoalescingCustomerDirectory.java",
                                "CustomerDirectory.java",
                                "Customer.java"
                        ),
                        List.of(
                                "CoalescingCustomerDirectoryTest.java"
                        ),
                        List.of(
                                "Hold one lookup in flight and start a second lookup for the same id.",
                                "Confirm the delegate is called once while both callers receive the same result.",
                                "Explain how request coalescing differs from caching."
                        )
                ),
                new PracticeStage(
                        34,
                        "Negative caching",
                        "Cache short-lived not-found results so missing keys do not repeatedly hit storage.",
                        List.of(
                                "NegativeCachingCustomerDirectory.java",
                                "InMemoryCustomerDirectory.java",
                                "CustomerDirectory.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "NegativeCachingCustomerDirectoryTest.java"
                        ),
                        List.of(
                                "Look up the same missing id twice and confirm the delegate is called once.",
                                "Advance ManualTimeSource past TTL and confirm the missing id is loaded again.",
                                "Explain why negative cache TTL should usually be shorter than positive cache TTL."
                        )
                ),
                new PracticeStage(
                        35,
                        "Stale-while-revalidate cache",
                        "Serve recently stale data during short downstream failures while attempting refresh.",
                        List.of(
                                "StaleCustomerCache.java",
                                "CustomerDirectory.java",
                                "ManualTimeSource.java",
                                "TimeSource.java"
                        ),
                        List.of(
                                "StaleCustomerCacheTest.java"
                        ),
                        List.of(
                                "Read within fresh TTL and confirm delegate is not called again.",
                                "Advance into stale window and confirm refresh can update the value.",
                                "Force refresh failure and explain why stale data is still returned temporarily."
                        )
                ),
                new PracticeStage(
                        36,
                        "Jittered retry backoff",
                        "Spread retry delays so many failed callers do not retry at the exact same time.",
                        List.of(
                                "JitteredBackoffPolicy.java",
                                "OutboxRetryPolicy.java",
                                "ResilientNotificationClient.java"
                        ),
                        List.of(
                                "JitteredBackoffPolicyTest.java"
                        ),
                        List.of(
                                "Compare plain exponential delays with jittered delays.",
                                "Set jitter to zero and confirm the behavior becomes deterministic exponential backoff.",
                                "Explain why jitter protects a recovering downstream dependency from retry waves."
                        )
                ),
                new PracticeStage(
                        37,
                        "Retry budget",
                        "Cap retry traffic in a rolling window so retries do not amplify outages.",
                        List.of(
                                "RetryBudget.java",
                                "ManualTimeSource.java",
                                "JitteredBackoffPolicy.java",
                                "ResilientNotificationClient.java"
                        ),
                        List.of(
                                "RetryBudgetTest.java"
                        ),
                        List.of(
                                "Acquire retry budget until the limit is exhausted.",
                                "Advance ManualTimeSource past the window and confirm budget is available again.",
                                "Explain the difference between limiting normal traffic and limiting retry traffic."
                        )
                ),
                new PracticeStage(
                        38,
                        "Bounded dead-letter store",
                        "Keep failed messages visible without allowing memory to grow without limit.",
                        List.of(
                                "BoundedDeadLetterStore.java",
                                "DeadLetterStore.java",
                                "DeadLetterMessage.java",
                                "DeadLetteringNotificationClient.java"
                        ),
                        List.of(
                                "BoundedDeadLetterStoreTest.java"
                        ),
                        List.of(
                                "Save messages until the store reaches capacity.",
                                "Add one more message and confirm the oldest message is dropped.",
                                "Explain why droppedCount is important for monitoring data loss."
                        )
                ),
                new PracticeStage(
                        39,
                        "Two-level customer cache",
                        "Use a small L1 cache and a larger L2 cache to reduce repeated repository calls.",
                        List.of(
                                "TwoLevelCustomerCache.java",
                                "CustomerDirectory.java",
                                "InMemoryCustomerDirectory.java",
                                "Customer.java"
                        ),
                        List.of(
                                "TwoLevelCustomerCacheTest.java"
                        ),
                        List.of(
                                "Read one customer twice and confirm the delegate is called once.",
                                "Evict a customer from L1 and confirm L2 can promote it back without delegate lookup.",
                                "Explain why each cache layer needs a capacity limit."
                        )
                ),
                new PracticeStage(
                        40,
                        "Cache warmup",
                        "Preload important customer ids so first real requests can hit cache.",
                        List.of(
                                "CacheWarmupService.java",
                                "CacheWarmupReport.java",
                                "TwoLevelCustomerCache.java",
                                "CustomerDirectory.java"
                        ),
                        List.of(
                                "CacheWarmupServiceTest.java"
                        ),
                        List.of(
                                "Warm up duplicate ids and confirm each unique id is loaded once.",
                                "Include a missing id and inspect the warmup report.",
                                "Explain when warmup helps and when it wastes startup time."
                        )
                ),
                new PracticeStage(
                        41,
                        "Snapshot diff before write",
                        "Compare old and new user profile snapshots so writes and events only happen for real changes.",
                        List.of(
                                "UserProfileDiffService.java",
                                "UserProfileDiff.java",
                                "FieldChange.java",
                                "UserProfile.java"
                        ),
                        List.of(
                                "UserProfileDiffServiceTest.java"
                        ),
                        List.of(
                                "Compare identical profiles and confirm the diff is empty.",
                                "Change email and region, then inspect each FieldChange.",
                                "Explain how diffing helps skip writes, reduce events, and build audit logs."
                        )
                ),
                new PracticeStage(
                        42,
                        "Selective change events",
                        "Publish downstream events only when a profile diff contains real changes.",
                        List.of(
                                "SelectiveProfileChangePublisher.java",
                                "UserProfileChangeEvent.java",
                                "InMemoryUserProfileChangePublisher.java",
                                "UserProfileDiffService.java"
                        ),
                        List.of(
                                "SelectiveProfileChangePublisherTest.java"
                        ),
                        List.of(
                                "Compare unchanged profiles and confirm no event is published.",
                                "Change email or region and inspect the event payload.",
                                "Explain how selective events reduce consumer load and duplicate processing."
                        )
                ),
                new PracticeStage(
                        43,
                        "Coalesced change events",
                        "Merge multiple profile change events for the same user before sending a batch downstream.",
                        List.of(
                                "ProfileChangeEventCoalescer.java",
                                "UserProfileChangeEvent.java",
                                "FieldChange.java"
                        ),
                        List.of(
                                "ProfileChangeEventCoalescerTest.java"
                        ),
                        List.of(
                                "Send two email changes for one user and confirm only one event remains.",
                                "Change a field and then change it back, then confirm the no-op field is removed.",
                                "Explain why coalescing reduces consumer load but should preserve ordering rules."
                        )
                ),
                new PracticeStage(
                        44,
                        "Priority job queue",
                        "Process higher priority background jobs first while preserving FIFO order within a priority.",
                        List.of(
                                "PriorityJobQueue.java",
                                "BackgroundJob.java",
                                "JobPriority.java"
                        ),
                        List.of(
                                "PriorityJobQueueTest.java"
                        ),
                        List.of(
                                "Enqueue low, normal, and high priority jobs, then poll them in priority order.",
                                "Enqueue three jobs with the same priority and confirm FIFO order.",
                                "Explain when priority queues help and when starvation protection is needed."
                        )
                ),
                new PracticeStage(
                        45,
                        "Aging priority queue",
                        "Increase a waiting job's effective priority so low-priority work is not starved forever.",
                        List.of(
                                "AgingPriorityJobQueue.java",
                                "PriorityJobQueue.java",
                                "BackgroundJob.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "AgingPriorityJobQueueTest.java"
                        ),
                        List.of(
                                "Enqueue a low-priority job, advance time, then enqueue a high-priority job.",
                                "Inspect effectiveWeight and confirm the old low-priority job can run first.",
                                "Explain how aging balances urgency with fairness."
                        )
                ),
                new PracticeStage(
                        46,
                        "Deadline job queue",
                        "Schedule jobs by earliest deadline first and detect work that is already overdue.",
                        List.of(
                                "DeadlineJobQueue.java",
                                "DeadlineJob.java",
                                "BackgroundJob.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "DeadlineJobQueueTest.java"
                        ),
                        List.of(
                                "Enqueue jobs with different deadlines and confirm the earliest deadline runs first.",
                                "Use the same deadline with different priorities and inspect the tie-breaker.",
                                "Advance ManualTimeSource and explain how overdueJobs helps monitoring."
                        )
                ),
                new PracticeStage(
                        47,
                        "SLA budget tracker",
                        "Track availability and remaining error budget inside a rolling window.",
                        List.of(
                                "SlaBudgetTracker.java",
                                "SlaBudgetSnapshot.java",
                                "ServiceCallOutcome.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "SlaBudgetTrackerTest.java"
                        ),
                        List.of(
                                "Record success and failure calls, then inspect availability and error rate.",
                                "Advance ManualTimeSource past the window and confirm old failures are evicted.",
                                "Explain how error budget can control retries, deploys, and alerts."
                        )
                ),
                new PracticeStage(
                        48,
                        "Adaptive retry controller",
                        "Allow retries only when local retry budget and service SLA budget both have room.",
                        List.of(
                                "AdaptiveRetryController.java",
                                "RetryDecision.java",
                                "RetryBudget.java",
                                "SlaBudgetTracker.java"
                        ),
                        List.of(
                                "AdaptiveRetryControllerTest.java"
                        ),
                        List.of(
                                "Allow one retry while both budgets have capacity.",
                                "Exhaust retry budget and confirm the second retry is blocked.",
                                "Exhaust SLA budget and confirm retry budget is not consumed."
                        )
                ),
                new PracticeStage(
                        49,
                        "Load shedding controller",
                        "Reject lower-priority work under queue pressure or exhausted SLA budget.",
                        List.of(
                                "LoadSheddingController.java",
                                "LoadSheddingDecision.java",
                                "IncomingRequest.java",
                                "SlaBudgetTracker.java"
                        ),
                        List.of(
                                "LoadSheddingControllerTest.java"
                        ),
                        List.of(
                                "Send low-priority work at the soft queue limit and confirm it is shed.",
                                "Send high-priority work near the hard queue limit and confirm it is still accepted.",
                                "Exhaust SLA budget and explain why non-high-priority work is rejected."
                        )
                ),
                new PracticeStage(
                        50,
                        "Graceful degradation",
                        "Return a smaller response for shed low-priority work instead of failing every request.",
                        List.of(
                                "GracefulDegradationController.java",
                                "DegradationDecision.java",
                                "ResponseMode.java",
                                "LoadSheddingController.java"
                        ),
                        List.of(
                                "GracefulDegradationControllerTest.java"
                        ),
                        List.of(
                                "Send a healthy request and confirm FULL mode.",
                                "Trigger load shedding for a low-priority request and confirm DEGRADED mode.",
                                "Hit the hard limit and explain why REJECTED is still necessary."
                        )
                ),
                new PracticeStage(
                        51,
                        "Feature flag rollout",
                        "Enable a feature for a stable percentage of users so rollout can grow gradually.",
                        List.of(
                                "featureflag/FeatureFlagEvaluator.java",
                                "featureflag/FeatureFlagRule.java",
                                "featureflag/FeatureFlagEvaluation.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagEvaluatorTest.java"
                        ),
                        List.of(
                                "Evaluate the same user twice and confirm the bucket stays stable.",
                                "Set rollout to zero and one hundred, then compare behavior.",
                                "Explain how percentage rollout reduces deployment risk."
                        )
                ),
                new PracticeStage(
                        52,
                        "Adaptive feature rollout",
                        "Reduce feature rollout automatically when SLA budget is exhausted.",
                        List.of(
                                "featureflag/AdaptiveFeatureFlagController.java",
                                "featureflag/FeatureFlagEvaluator.java",
                                "featureflag/FeatureFlagRule.java",
                                "SlaBudgetTracker.java"
                        ),
                        List.of(
                                "featureflag/AdaptiveFeatureFlagControllerTest.java"
                        ),
                        List.of(
                                "Evaluate a feature while SLA budget is healthy and confirm original rollout is used.",
                                "Exhaust SLA budget and confirm rollout is reduced.",
                                "Explain why degraded rollout should never increase the original rollout percentage."
                        )
                ),
                new PracticeStage(
                        53,
                        "Feature flag registry",
                        "Manage many feature flag rules as immutable snapshots with safe defaults.",
                        List.of(
                                "featureflag/FeatureFlagRegistry.java",
                                "featureflag/FeatureFlagEvaluator.java",
                                "featureflag/FeatureFlagRule.java",
                                "featureflag/FeatureFlagEvaluation.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagRegistryTest.java"
                        ),
                        List.of(
                                "Look up a configured flag and a missing flag.",
                                "Update a rule and confirm unchanged updates are skipped.",
                                "Explain why snapshot readers should not mutate registry state."
                        )
                ),
                new PracticeStage(
                        54,
                        "Feature flag reload diff",
                        "Apply a new flag configuration snapshot while reporting added, updated, removed, and unchanged flags.",
                        List.of(
                                "featureflag/FeatureFlagReloader.java",
                                "featureflag/FeatureFlagReloadReport.java",
                                "featureflag/FeatureFlagRegistry.java",
                                "featureflag/FeatureFlagRule.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloaderTest.java"
                        ),
                        List.of(
                                "Reload config with one added, one updated, one removed, and one unchanged flag.",
                                "Reload the same config twice and confirm the second report has no changes.",
                                "Explain why config reload should avoid unnecessary writes and noisy logs."
                        )
                ),
                new PracticeStage(
                        55,
                        "Feature flag audit log",
                        "Write audit events for real feature flag config changes while skipping unchanged reloads.",
                        List.of(
                                "featureflag/AuditedFeatureFlagReloader.java",
                                "featureflag/FeatureFlagAuditEvent.java",
                                "featureflag/InMemoryFeatureFlagAuditLog.java",
                                "featureflag/FeatureFlagReloader.java"
                        ),
                        List.of(
                                "featureflag/AuditedFeatureFlagReloaderTest.java"
                        ),
                        List.of(
                                "Reload changed config and inspect the audit event.",
                                "Reload unchanged config and confirm no audit event is written.",
                                "Explain why audit logs should capture real changes without noisy duplicates."
                        )
                ),
                new PracticeStage(
                        56,
                        "Feature flag rollback plan",
                        "Generate rollback actions from feature flag audit events.",
                        List.of(
                                "featureflag/FeatureFlagRollbackPlanner.java",
                                "featureflag/FeatureFlagRollbackPlan.java",
                                "featureflag/FeatureFlagRollbackAction.java",
                                "featureflag/FeatureFlagAuditEvent.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagRollbackPlannerTest.java"
                        ),
                        List.of(
                                "Build a plan from added, updated, and removed flags.",
                                "Confirm an empty audit event produces an empty plan.",
                                "Explain why rollback plans need old config history for updated and removed flags."
                        )
                ),
                new PracticeStage(
                        57,
                        "Versioned feature flag snapshots",
                        "Store immutable feature flag config versions so rollback can restore an older snapshot.",
                        List.of(
                                "featureflag/FeatureFlagSnapshotStore.java",
                                "featureflag/FeatureFlagSnapshot.java",
                                "featureflag/FeatureFlagRegistry.java",
                                "featureflag/FeatureFlagRule.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagSnapshotStoreTest.java"
                        ),
                        List.of(
                                "Save two snapshots and inspect the latest version.",
                                "Restore version one and confirm the old rollout percentage returns.",
                                "Explain why rollback needs old rules, not only audit event names."
                        )
                ),
                new PracticeStage(
                        58,
                        "Feature flag snapshot retention",
                        "Keep only the newest feature flag snapshots so rollback history does not grow forever.",
                        List.of(
                                "featureflag/FeatureFlagSnapshotRetentionPolicy.java",
                                "featureflag/FeatureFlagSnapshotRetentionReport.java",
                                "featureflag/FeatureFlagSnapshotStore.java",
                                "featureflag/FeatureFlagSnapshot.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagSnapshotRetentionPolicyTest.java"
                        ),
                        List.of(
                                "Apply retention to four snapshots and keep only the newest two.",
                                "Pass snapshots out of order and confirm versions are sorted first.",
                                "Explain the trade-off between rollback depth and storage growth."
                        )
                ),
                new PracticeStage(
                        59,
                        "Feature flag reload validation",
                        "Reject risky feature flag config before it mutates the live registry.",
                        List.of(
                                "featureflag/FeatureFlagReloadValidator.java",
                                "featureflag/FeatureFlagReloadValidationReport.java",
                                "featureflag/FeatureFlagRegistry.java",
                                "featureflag/FeatureFlagRule.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadValidatorTest.java"
                        ),
                        List.of(
                                "Validate a safe rollout increase and confirm the report is accepted.",
                                "Try duplicate flag names and a large rollout jump, then inspect violations.",
                                "Explain why validation should happen before reload side effects."
                        )
                ),
                new PracticeStage(
                        60,
                        "Safe feature flag reload",
                        "Run validation and only mutate the registry when the new config is accepted.",
                        List.of(
                                "featureflag/SafeFeatureFlagReloader.java",
                                "featureflag/SafeFeatureFlagReloadResult.java",
                                "featureflag/FeatureFlagReloadValidator.java",
                                "featureflag/FeatureFlagReloader.java"
                        ),
                        List.of(
                                "featureflag/SafeFeatureFlagReloaderTest.java"
                        ),
                        List.of(
                                "Apply a safe config and inspect the reload diff.",
                                "Reject a risky config and confirm the registry stays unchanged.",
                                "Explain why validation and mutation should be treated as one workflow."
                        )
                ),
                new PracticeStage(
                        61,
                        "Feature flag config fingerprint",
                        "Create an order-independent digest so unchanged config can skip expensive reload work.",
                        List.of(
                                "featureflag/FeatureFlagConfigFingerprinter.java",
                                "featureflag/FeatureFlagConfigFingerprint.java",
                                "featureflag/FeatureFlagRule.java",
                                "featureflag/FeatureFlagRegistry.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagConfigFingerprinterTest.java"
                        ),
                        List.of(
                                "Hash the same rules in two different orders and confirm the digest matches.",
                                "Change rollout percentage and confirm the digest changes.",
                                "Explain why canonical ordering matters before hashing config."
                        )
                ),
                new PracticeStage(
                        62,
                        "Fingerprinting feature flag reload",
                        "Compare config fingerprints before validation and reload so unchanged snapshots can be skipped.",
                        List.of(
                                "featureflag/FingerprintingFeatureFlagReloader.java",
                                "featureflag/FingerprintingFeatureFlagReloadResult.java",
                                "featureflag/FeatureFlagConfigFingerprinter.java",
                                "featureflag/SafeFeatureFlagReloader.java"
                        ),
                        List.of(
                                "featureflag/FingerprintingFeatureFlagReloaderTest.java"
                        ),
                        List.of(
                                "Reload the same config in a different order and confirm work is skipped.",
                                "Change a rollout and confirm safe reload is executed.",
                                "Explain why fingerprint checks should not bypass validation for changed config."
                        )
                ),
                new PracticeStage(
                        63,
                        "Rate-limited feature flag reload",
                        "Use a token bucket to prevent noisy callers from triggering reload work too frequently.",
                        List.of(
                                "featureflag/RateLimitedFeatureFlagReloader.java",
                                "featureflag/RateLimitedFeatureFlagReloadResult.java",
                                "TokenBucketRateLimiter.java",
                                "featureflag/FingerprintingFeatureFlagReloader.java"
                        ),
                        List.of(
                                "featureflag/RateLimitedFeatureFlagReloaderTest.java"
                        ),
                        List.of(
                                "Allow the first reload and block the second reload in the same time window.",
                                "Advance the manual clock and confirm a new token allows reload again.",
                                "Explain why rate limiting belongs outside validation and reload logic."
                        )
                ),
                new PracticeStage(
                        64,
                        "Debounced feature flag reload",
                        "Coalesce rapid config updates and reload only the latest config after a quiet period.",
                        List.of(
                                "featureflag/DebouncedFeatureFlagReloader.java",
                                "featureflag/DebouncedFeatureFlagReloadResult.java",
                                "featureflag/DebouncedReloadStatus.java",
                                "featureflag/RateLimitedFeatureFlagReloader.java"
                        ),
                        List.of(
                                "featureflag/DebouncedFeatureFlagReloaderTest.java"
                        ),
                        List.of(
                                "Submit a config and confirm reload waits until the quiet period expires.",
                                "Submit two configs quickly and confirm only the latest config is applied.",
                                "Explain why debounce is different from rate limiting."
                        )
                ),
                new PracticeStage(
                        65,
                        "Instrumented debounced feature flag reload",
                        "Collect metrics for debounce, rate limit, fingerprint, and safe reload outcomes.",
                        List.of(
                                "featureflag/InstrumentedDebouncedFeatureFlagReloader.java",
                                "featureflag/FeatureFlagReloadMetrics.java",
                                "featureflag/FeatureFlagReloadMetricsSnapshot.java",
                                "featureflag/DebouncedFeatureFlagReloader.java"
                        ),
                        List.of(
                                "featureflag/InstrumentedDebouncedFeatureFlagReloaderTest.java"
                        ),
                        List.of(
                                "Record idle, waiting, and applied reload outcomes.",
                                "Trigger fingerprint skip and rate-limit block, then inspect counters.",
                                "Explain why optimization work should start with observable metrics."
                        )
                ),
                new PracticeStage(
                        66,
                        "Feature flag reload health analyzer",
                        "Turn reload metrics into health status and operator warnings.",
                        List.of(
                                "featureflag/FeatureFlagReloadHealthAnalyzer.java",
                                "featureflag/FeatureFlagReloadHealthReport.java",
                                "featureflag/FeatureFlagReloadHealthStatus.java",
                                "featureflag/FeatureFlagReloadMetricsSnapshot.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadHealthAnalyzerTest.java"
                        ),
                        List.of(
                                "Analyze a healthy metrics snapshot and inspect the rates.",
                                "Raise block and rejection rates above thresholds and inspect warnings.",
                                "Explain why health checks should use ratios, not only raw counters."
                        )
                ),
                new PracticeStage(
                        67,
                        "Feature flag reload alert policy",
                        "Convert reload health reports into alert decisions with severity and details.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertPolicy.java",
                                "featureflag/FeatureFlagReloadAlert.java",
                                "featureflag/FeatureFlagReloadHealthReport.java",
                                "featureflag/FeatureFlagReloadHealthStatus.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertPolicyTest.java"
                        ),
                        List.of(
                                "Evaluate a healthy report and confirm no alert is active.",
                                "Toggle warning alerts on and off, then inspect severity.",
                                "Confirm critical health always produces an active alert."
                        )
                ),
                new PracticeStage(
                        68,
                        "Feature flag reload alert suppressor",
                        "Suppress duplicate reload alerts during a cooldown window.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertSuppressor.java",
                                "featureflag/FeatureFlagReloadAlertDecision.java",
                                "featureflag/FeatureFlagReloadAlert.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertSuppressorTest.java"
                        ),
                        List.of(
                                "Emit the first active alert and suppress the immediate duplicate.",
                                "Advance the manual clock and confirm the same alert can be emitted again.",
                                "Explain why alert suppression should use a stable fingerprint of alert content."
                        )
                ),
                new PracticeStage(
                        69,
                        "Feature flag reload alert router",
                        "Route emitted reload alerts to dashboard or on-call channels based on severity.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertRouter.java",
                                "featureflag/FeatureFlagReloadAlertRoute.java",
                                "featureflag/FeatureFlagReloadAlertChannel.java",
                                "featureflag/FeatureFlagReloadAlertDecision.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertRouterTest.java"
                        ),
                        List.of(
                                "Route a suppressed alert and confirm the channel is NONE.",
                                "Route a warning alert to DASHBOARD and a critical alert to ON_CALL.",
                                "Explain why routing should happen after duplicate suppression."
                        )
                ),
                new PracticeStage(
                        70,
                        "Feature flag reload alert dispatcher",
                        "Deliver routed feature flag reload alerts to a sink while skipping non-deliverable routes.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDispatcher.java",
                                "featureflag/FeatureFlagReloadAlertDispatchResult.java",
                                "featureflag/FeatureFlagReloadAlertDelivery.java",
                                "featureflag/InMemoryFeatureFlagReloadAlertSink.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDispatcherTest.java"
                        ),
                        List.of(
                                "Dispatch a NONE route and confirm nothing is delivered.",
                                "Dispatch DASHBOARD and ON_CALL routes and inspect the sink.",
                                "Explain why routing and delivery are separated."
                        )
                ),
                new PracticeStage(
                        71,
                        "Feature flag reload alert retry policy",
                        "Plan bounded retries with backoff for failed alert deliveries.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertRetryPolicy.java",
                                "featureflag/FeatureFlagReloadAlertRetryPlan.java",
                                "featureflag/FeatureFlagReloadAlertRetryDecision.java",
                                "featureflag/FeatureFlagReloadAlertDelivery.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertRetryPolicyTest.java"
                        ),
                        List.of(
                                "Plan retry attempts and inspect the next attempt time.",
                                "Exhaust max attempts and confirm the plan gives up.",
                                "Explain why retry planning should be bounded for alert delivery."
                        )
                ),
                new PracticeStage(
                        72,
                        "Feature flag reload alert dead-letter store",
                        "Keep failed alert deliveries after retry attempts are exhausted.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterStore.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetter.java",
                                "featureflag/FeatureFlagReloadAlertRetryPlan.java",
                                "featureflag/FeatureFlagReloadAlertDelivery.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterStoreTest.java"
                        ),
                        List.of(
                                "Record only GIVE_UP retry plans and ignore RETRY_LATER plans.",
                                "Exceed store capacity and confirm the oldest records are dropped.",
                                "Explain why dead-letter stores should be bounded."
                        )
                ),
                new PracticeStage(
                        73,
                        "Feature flag reload alert dead-letter replay",
                        "Replay bounded alert deliveries from the dead-letter store.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterReplayer.java",
                                "featureflag/FeatureFlagReloadAlertReplayResult.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterStore.java",
                                "featureflag/FeatureFlagReloadAlertDispatcher.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterReplayerTest.java"
                        ),
                        List.of(
                                "Replay only the first N dead-letter records and inspect delivered payloads.",
                                "Confirm replay does not remove records from the dead-letter store.",
                                "Explain why replay should be bounded per run."
                        )
                ),
                new PracticeStage(
                        74,
                        "Feature flag reload alert replay cleanup",
                        "Remove dead-letter records only after replay delivery succeeds.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterReplayCoordinator.java",
                                "featureflag/FeatureFlagReloadAlertReplayCleanupResult.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterStore.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterReplayer.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterReplayCoordinatorTest.java"
                        ),
                        List.of(
                                "Replay a limited batch and confirm delivered records are removed.",
                                "Confirm records outside the limit remain in the store.",
                                "Explain why cleanup should happen only after successful delivery."
                        )
                ),
                new PracticeStage(
                        75,
                        "Feature flag reload alert dead-letter monitor",
                        "Analyze dead-letter backlog pressure and dropped records.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterMonitor.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterHealthReport.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterStore.java",
                                "featureflag/FeatureFlagReloadHealthStatus.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterMonitorTest.java"
                        ),
                        List.of(
                                "Fill the store below the warning threshold and inspect a healthy report.",
                                "Cross warning and critical thresholds, then compare report details.",
                                "Explain why dropped dead-letter records should be treated as critical."
                        )
                ),
                new PracticeStage(
                        76,
                        "Feature flag reload alert dead-letter alert policy",
                        "Convert dead-letter backlog health into operator alerts.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertPolicy.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterHealthReport.java",
                                "featureflag/FeatureFlagReloadAlert.java",
                                "featureflag/FeatureFlagReloadHealthStatus.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertPolicyTest.java"
                        ),
                        List.of(
                                "Evaluate a healthy dead-letter report and confirm no alert is active.",
                                "Toggle warning alerts on and off, then compare alert output.",
                                "Explain why critical dead-letter backlog should always alert."
                        )
                ),
                new PracticeStage(
                        77,
                        "Feature flag reload alert dead-letter alert workflow",
                        "Run dead-letter health, alert policy, suppression, routing, and dispatch together.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflow.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowResult.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterMonitor.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertPolicy.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowTest.java"
                        ),
                        List.of(
                                "Run the workflow against a critical backlog and inspect the delivered alert.",
                                "Run the same workflow twice and confirm the second alert is suppressed.",
                                "Explain why orchestration code should stay thin and delegate decisions."
                        )
                ),
                new PracticeStage(
                        78,
                        "Instrumented feature flag reload alert dead-letter workflow",
                        "Collect counters for dead-letter alert workflow outcomes.",
                        List.of(
                                "featureflag/InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflow.java"
                        ),
                        List.of(
                                "featureflag/InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflowTest.java"
                        ),
                        List.of(
                                "Run healthy, delivered, and suppressed workflow outcomes.",
                                "Inspect the metrics snapshot after each run.",
                                "Explain why instrumentation should observe decisions without changing them."
                        )
                ),
                new PracticeStage(
                        79,
                        "Feature flag reload alert dead-letter workflow health analyzer",
                        "Convert dead-letter alert workflow counters into health status.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot.java",
                                "featureflag/FeatureFlagReloadHealthStatus.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzerTest.java"
                        ),
                        List.of(
                                "Analyze a healthy empty snapshot and confirm all rates are zero.",
                                "Raise critical and suppression rates, then inspect warnings.",
                                "Explain why critical alerts with zero delivery should be treated as critical."
                        )
                ),
                new PracticeStage(
                        80,
                        "Feature flag reload alert dead-letter workflow alert policy",
                        "Convert dead-letter alert workflow health into alert payloads.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport.java",
                                "featureflag/FeatureFlagReloadAlert.java",
                                "featureflag/FeatureFlagReloadHealthStatus.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicyTest.java"
                        ),
                        List.of(
                                "Evaluate healthy workflow health and confirm no alert is active.",
                                "Toggle warning workflow alerts on and off.",
                                "Explain why critical workflow health should always emit an alert."
                        )
                ),
                new PracticeStage(
                        81,
                        "Feature flag reload alert dead-letter workflow alert pipeline",
                        "Dispatch alerts about dead-letter alert workflow health.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipelineTest.java"
                        ),
                        List.of(
                                "Run a critical workflow metrics snapshot and inspect the on-call delivery.",
                                "Run the same snapshot twice and confirm suppression prevents duplicate delivery.",
                                "Explain why alert pipelines should also be monitored and alerted on."
                        )
                ),
                new PracticeStage(
                        82,
                        "Feature flag reload alert dead-letter workflow incident log",
                        "Keep bounded incident history for alerts about alert workflow health.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogTest.java"
                        ),
                        List.of(
                                "Record only active workflow alerts and inspect the stored incident.",
                                "Exceed incident log capacity and confirm the oldest incident is dropped.",
                                "Explain why audit history should be bounded in memory."
                        )
                ),
                new PracticeStage(
                        83,
                        "Feature flag reload alert dead-letter workflow incident log monitor",
                        "Analyze incident log utilization, undelivered incidents, and dropped history.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitorTest.java"
                        ),
                        List.of(
                                "Analyze a lightly used incident log and confirm it is healthy.",
                                "Fill the log past warning and critical thresholds.",
                                "Explain why undelivered incidents and dropped history should raise severity."
                        )
                ),
                new PracticeStage(
                        84,
                        "Feature flag reload alert dead-letter workflow incident log alert policy",
                        "Convert incident log health into alert payloads.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport.java",
                                "featureflag/FeatureFlagReloadAlert.java",
                                "featureflag/FeatureFlagReloadHealthStatus.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicyTest.java"
                        ),
                        List.of(
                                "Evaluate a healthy incident log report and confirm no alert is active.",
                                "Toggle warning incident log alerts on and off.",
                                "Explain why dropped incident history should always produce critical alerts."
                        )
                ),
                new PracticeStage(
                        85,
                        "Feature flag reload alert dead-letter workflow incident log alert pipeline",
                        "Dispatch alerts about incident-log health.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipelineTest.java"
                        ),
                        List.of(
                                "Run a critical incident log and inspect the on-call alert delivery.",
                                "Run the same log twice and confirm duplicate suppression.",
                                "Explain why audit-log health alerts should use the same delivery pipeline."
                        )
                ),
                new PracticeStage(
                        86,
                        "Feature flag reload alert dead-letter workflow incident log summary",
                        "Build dashboard counters from retained workflow incidents.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizerTest.java"
                        ),
                        List.of(
                                "Summarize incidents by status, channel, and delivery outcome.",
                                "Exceed log capacity and confirm dropped incidents are represented.",
                                "Explain why dashboards should read derived summaries, not mutate audit logs."
                        )
                ),
                new PracticeStage(
                        87,
                        "Feature flag reload alert dead-letter workflow incident triage planner",
                        "Turn incident-log summary counters into prioritized operator actions.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlannerTest.java"
                        ),
                        List.of(
                                "Plan actions from a clean summary and confirm the plan is empty.",
                                "Plan actions from dropped and undelivered incidents, then inspect priority order.",
                                "Explain why summary interpretation should be separated from summary calculation."
                        )
                ),
                new PracticeStage(
                        88,
                        "Feature flag reload alert dead-letter workflow incident triage formatter",
                        "Render triage plans as stable operator text.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatterTest.java"
                        ),
                        List.of(
                                "Format an empty plan and confirm the no-action message.",
                                "Format multiple actions and confirm priority order is preserved.",
                                "Explain why rendering should not recalculate triage rules."
                        )
                ),
                new PracticeStage(
                        89,
                        "Feature flag reload alert dead-letter workflow incident triage digest",
                        "Package summary, triage plan, formatted text, and timestamp for export.",
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer.java",
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter.java"
                        ),
                        List.of(
                                "featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilderTest.java"
                        ),
                        List.of(
                                "Build a digest from a clean log and confirm it has no actions.",
                                "Build a digest from dropped incidents and inspect summary, plan, and text.",
                                "Explain why digest builders should compose collaborators instead of duplicating rules."
                        )
                ),
                new PracticeStage(
                        90,
                        "Rolling latency metrics window",
                        "Track recent latency statistics with bounded memory and constant-time reads.",
                        List.of(
                                "metrics/RollingLatencyWindow.java",
                                "metrics/LatencyWindowSnapshot.java"
                        ),
                        List.of(
                                "metrics/RollingLatencyWindowTest.java"
                        ),
                        List.of(
                                "Record more samples than the window capacity and identify which values remain.",
                                "Trace how the ring buffer updates total latency without scanning all samples.",
                                "Explain why monotonic queues make min/max reads cheap under load."
                        )
                ),
                new PracticeStage(
                        91,
                        "Latency spike detection",
                        "Compare new latency samples with a rolling baseline before recording them.",
                        List.of(
                                "metrics/LatencySpikeDetector.java",
                                "metrics/LatencySpikeDecision.java",
                                "metrics/RollingLatencyWindow.java"
                        ),
                        List.of(
                                "metrics/LatencySpikeDetectorTest.java"
                        ),
                        List.of(
                                "Warm up the baseline with normal latency samples, then add a spike.",
                                "Explain why the detector evaluates before recording the current sample.",
                                "Change the spike multiplier and predict which test expectation changes."
                        )
                ),
                new PracticeStage(
                        92,
                        "Latency percentile histogram",
                        "Estimate p50, p95, and p99 latency with fixed memory and predictable read cost.",
                        List.of(
                                "metrics/LatencyHistogram.java",
                                "metrics/LatencyPercentileSnapshot.java"
                        ),
                        List.of(
                                "metrics/LatencyHistogramTest.java"
                        ),
                        List.of(
                                "Record samples across multiple buckets and inspect bucket counts.",
                                "Compare percentile estimation with exact sorting for a small data set.",
                                "Explain why overflow samples are clamped into the last bucket."
                        )
                ),
                new PracticeStage(
                        93,
                        "Rolling error-rate window",
                        "Track recent failure ratio with fixed memory for adaptive controls.",
                        List.of(
                                "metrics/RollingErrorRateWindow.java",
                                "metrics/ErrorRateSnapshot.java",
                                "ServiceCallOutcome.java"
                        ),
                        List.of(
                                "metrics/RollingErrorRateWindowTest.java"
                        ),
                        List.of(
                                "Record success and failure outcomes, then calculate the error rate by hand.",
                                "Push more outcomes than capacity and identify which samples remain.",
                                "Explain when a count-based window is cheaper than a time-based SLA tracker."
                        )
                ),
                new PracticeStage(
                        94,
                        "Adaptive concurrency limit",
                        "Raise and lower concurrency from rolling health signals.",
                        List.of(
                                "autoscaling/AdaptiveConcurrencyLimiter.java",
                                "autoscaling/AdaptiveConcurrencySnapshot.java",
                                "metrics/RollingErrorRateWindow.java"
                        ),
                        List.of(
                                "autoscaling/AdaptiveConcurrencyLimiterTest.java"
                        ),
                        List.of(
                                "Acquire slots until the current limit rejects new work.",
                                "Feed an unhealthy error-rate snapshot and inspect how quickly the limit drops.",
                                "Feed healthy completions and explain why recovery is intentionally slower."
                        )
                ),
                new PracticeStage(
                        95,
                        "Adaptive timeout policy",
                        "Calculate timeout values from latency percentiles while keeping safe min/max bounds.",
                        List.of(
                                "metrics/AdaptiveTimeoutPolicy.java",
                                "metrics/AdaptiveTimeoutDecision.java",
                                "metrics/LatencyHistogram.java"
                        ),
                        List.of(
                                "metrics/AdaptiveTimeoutPolicyTest.java"
                        ),
                        List.of(
                                "Inspect the minimum timeout fallback before any latency samples exist.",
                                "Record latency samples and calculate percentile plus margin by hand.",
                                "Explain why adaptive timeouts still need configured lower and upper bounds."
                        )
                ),
                new PracticeStage(
                        96,
                        "Bucketed throughput window",
                        "Track recent request rate with fixed time buckets and bounded memory.",
                        List.of(
                                "metrics/BucketedThroughputWindow.java",
                                "metrics/ThroughputSnapshot.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "metrics/BucketedThroughputWindowTest.java"
                        ),
                        List.of(
                                "Record multiple events in one bucket and calculate events per second.",
                                "Advance time across buckets and inspect which counters expire.",
                                "Explain why bucket counters are cheaper than storing every timestamp."
                        )
                ),
                new PracticeStage(
                        97,
                        "Autoscaling decision policy",
                        "Combine throughput, latency, and error-rate signals into a scale decision.",
                        List.of(
                                "autoscaling/AutoscalingPolicy.java",
                                "autoscaling/AutoscalingDecision.java",
                                "autoscaling/ScalingAction.java",
                                "metrics/ThroughputSnapshot.java"
                        ),
                        List.of(
                                "autoscaling/AutoscalingPolicyTest.java"
                        ),
                        List.of(
                                "Evaluate high throughput, high latency, and unhealthy error-rate separately.",
                                "Compare scale-in and scale-out thresholds to understand the stable band.",
                                "Explain why scale-out is prioritized over scale-in when signals conflict."
                        )
                ),
                new PracticeStage(
                        98,
                        "Autoscaling cooldown control",
                        "Suppress repeated scaling actions to reduce capacity flapping.",
                        List.of(
                                "autoscaling/AutoscalingCooldownController.java",
                                "autoscaling/AutoscalingDecision.java",
                                "ManualTimeSource.java"
                        ),
                        List.of(
                                "autoscaling/AutoscalingCooldownControllerTest.java"
                        ),
                        List.of(
                                "Apply one scale-out decision and inspect the cooldown timestamp.",
                                "Apply another scaling decision before cooldown expires and explain why it is held.",
                                "Advance ManualTimeSource past cooldown and confirm scaling is allowed again."
                        )
                ),
                new PracticeStage(
                        99,
                        "Autoscaling decision audit log",
                        "Keep bounded recent scaling decisions for diagnostics and dashboard summaries.",
                        List.of(
                                "autoscaling/AutoscalingDecisionLog.java",
                                "autoscaling/AutoscalingDecisionEvent.java",
                                "autoscaling/AutoscalingDecisionLogSummary.java"
                        ),
                        List.of(
                                "autoscaling/AutoscalingDecisionLogTest.java"
                        ),
                        List.of(
                                "Record several scale decisions and inspect their timestamps.",
                                "Exceed log capacity and identify which decision was evicted.",
                                "Explain why bounded diagnostic history is safer than an unbounded in-memory list."
                        )
                )
        );
    }

    public record PracticeStage(
            int order,
            String title,
            String goal,
            List<String> sourceFiles,
            List<String> testFiles,
            List<String> exercises) {

        /**
         * Creates one roadmap stage and defensively copies list inputs.
         *
         * @throws IllegalArgumentException when required text is blank or {@code order} is not
         *         positive
         * @throws NullPointerException when any list is {@code null}
         */
        public PracticeStage {
            if (order <= 0) {
                throw new IllegalArgumentException("order must be positive");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("title must not be blank");
            }
            if (goal == null || goal.isBlank()) {
                throw new IllegalArgumentException("goal must not be blank");
            }

            sourceFiles = List.copyOf(sourceFiles);
            testFiles = List.copyOf(testFiles);
            exercises = List.copyOf(exercises);
        }
    }
}

