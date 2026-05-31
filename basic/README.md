# Basic Java Learning Module

This module is a reading-first Java playground.

The code is intentionally split into small classes so you can study one concept at a time:

## Package Layout

- `com.example.javalabs.basic`: core syntax, collections, services, resilience, caching, and roadmap examples
- `com.example.javalabs.basic.metrics`: fixed-memory latency, throughput, error-rate, and adaptive-timeout examples
- `com.example.javalabs.basic.autoscaling`: autoscaling, cooldown, audit-log, and adaptive-concurrency examples
- `com.example.javalabs.basic.featureflag`: rollout, reload safety, audit, rollback, alerting, and incident-triage examples

- `ControlFlowExamples`: variables, conditionals, loops, validation
- `StringToolkit`: strings, iteration, formatting, simple algorithms
- `BankAccount`: classes, encapsulation, invariants, exceptions
- `Student` and `StudentAnalytics`: records, collections, streams, comparators
- `Shape`, `Circle`, and `Rectangle`: interfaces, sealed hierarchies, polymorphism
- `AsyncExamples`: `CompletableFuture` and asynchronous composition
- `DifficultyLevel`: enums and fixed sets of constants
- `Pair`: generics and reusable type-safe containers
- `InventoryItem` and `InventoryAnalytics`: maps, grouping, sorting, and filtering
- `ExceptionPlayground`: validation and exception design
- `FileReport`: file I/O with `Path` and `Files`
- `JdbcExamples`: direct JDBC with SQL, prepared statements, and result-set mapping
- `FixedWindowRateLimiter`: fixed-window throttling for API-like workloads
- `SimpleConnectionPool` and `FakeDatabaseConnection`: connection reuse and pool limits
- `UserProfileRepository` family: repository abstraction, in-memory database, and region-based routing
- `RegistrationService`: a microservice-style orchestration layer with persistence and downstream notification
- `OrderSummaryService`: performance thinking with repeated lookup versus batch loading
- `CachedCustomerDirectory`: TTL caching, invalidation, and bounded LRU-style eviction around a repository interface
- `ResilientNotificationClient`: retry boundaries for temporary downstream failures
- `CircuitBreakerNotificationClient`: fail-fast protection when a dependency keeps failing
- `InstrumentedNotificationClient`: call count, failure count, and latency measurement around a client
- `RollingLatencyWindow`: bounded rolling latency statistics with ring buffer and monotonic queues
- `LatencySpikeDetector`: compare each latency sample with a rolling baseline before recording it
- `LatencyHistogram`: approximate percentile latency reads with fixed memory
- `BucketedThroughputWindow`: request-rate tracking with fixed time buckets
- `AutoscalingPolicy`: combine throughput, latency, and error-rate signals into scale decisions
- `AutoscalingCooldownController`: suppress repeated scaling actions during cooldown windows
- `AutoscalingDecisionLog`: bounded audit history and summary counters for scaling decisions
- `OrderExportService`: paging large reads instead of loading all data into memory
- `CursorOrderExportService`: cursor pagination for deep ordered reads
- `IdempotentNotificationClient`: duplicate side-effect protection for retries and redelivery
- `BulkheadNotificationClient`: concurrent call limit for slow or overloaded dependencies
- `TimeoutNotificationClient`: time budget for slow downstream calls
- `AdaptiveTimeoutPolicy`: calculate timeout values from percentile latency with safe clamps
- `WelcomeNotificationBatcher`: batch small notification calls to reduce downstream round trips
- `DeadLetteringNotificationClient`: capture failed side effects for later inspection or replay
- `StockReservationService`: optimistic locking for concurrent inventory writes
- `OutboxRegistrationService` and `OutboxDispatcher`: reliable event publishing with an outbox
- `OutboxRetryPolicy`: exponential backoff planning for failed outbox events
- `CustomerLookupService`: de-duplicate batch lookups while preserving response order
- `TopInventorySelector`: top-N selection with full sort versus bounded heap
- `InventoryStockSummarizer`: one-pass aggregation for stock statistics
- `IndexedInventoryCatalog`: indexed category lookup versus repeated list scanning
- `BatchPartitioner` and `ChunkedNotificationSender`: bounded chunk processing for large batches
- `SlidingWindowRateLimiter`: smoother request limiting than fixed-window counters
- `TokenBucketRateLimiter`: controlled bursts with steady token refill
- `UserProfileUpdateService`: skip no-op writes by detecting unchanged updates
- `IncrementalInventorySummary`: maintain stock summary as items change
- `CoalescingCustomerDirectory`: share duplicate in-flight lookups for the same key
- `NegativeCachingCustomerDirectory`: cache short-lived not-found results to reduce repeated misses
- `StaleCustomerCache`: stale-while-revalidate reads during short downstream failures
- `JitteredBackoffPolicy`: retry delays with jitter to avoid synchronized retry waves
- `RetryBudget`: rolling-window cap for retry traffic during dependency failures
- `BoundedDeadLetterStore`: capped failed-message storage with visible drop counts
- `TwoLevelCustomerCache`: L1/L2 cache promotion with bounded cache layers
- `CacheWarmupService`: preload important ids into cache before user-facing reads
- `UserProfileDiffService`: compare snapshots before writes, events, or audit logging
- `SelectiveProfileChangePublisher`: publish events only when a profile diff has real changes
- `ProfileChangeEventCoalescer`: merge repeated profile change events in a batch
- `PriorityJobQueue`: priority-based background job scheduling with FIFO tie-breaking
- `AgingPriorityJobQueue`: priority scheduling with aging to reduce starvation
- `DeadlineJobQueue`: earliest-deadline-first scheduling and overdue job detection
- `SlaBudgetTracker`: rolling-window availability and error budget tracking
- `RollingErrorRateWindow`: fixed-size ring-buffer error-rate tracking for adaptive controls
- `AdaptiveConcurrencyLimiter`: raise or lower concurrency limits from recent health signals
- `AdaptiveRetryController`: retry decisions based on retry budget and SLA budget
- `LoadSheddingController`: reject lower-priority work during overload or SLA burn
- `GracefulDegradationController`: choose full, degraded, or rejected response modes under pressure
- `FeatureFlagEvaluator`: stable percentage rollout for feature flags
- `AdaptiveFeatureFlagController`: lower feature rollout when SLA budget is exhausted
- `FeatureFlagRegistry`: immutable feature flag snapshots with safe missing-flag defaults
- `FeatureFlagReloader`: diff-based feature flag config reload reporting
- `AuditedFeatureFlagReloader`: audit feature flag config changes without logging unchanged reloads
- `FeatureFlagRollbackPlanner`: generate rollback actions from feature flag audit events
- `FeatureFlagSnapshotStore`: versioned feature flag snapshots for rollback
- `FeatureFlagSnapshotRetentionPolicy`: keep bounded feature flag rollback history
- `FeatureFlagReloadValidator`: pre-flight checks that reject risky feature flag reloads
- `SafeFeatureFlagReloader`: validate feature flag config before mutating the live registry
- `FeatureFlagConfigFingerprinter`: stable config digest for skipping unchanged feature flag reload work
- `FingerprintingFeatureFlagReloader`: skip safe reload when the proposed feature flag config is unchanged
- `RateLimitedFeatureFlagReloader`: token-bucket guard for noisy feature flag reload callers
- `DebouncedFeatureFlagReloader`: coalesce rapid feature flag config updates and reload only the latest one
- `InstrumentedDebouncedFeatureFlagReloader`: metrics around debounce, rate-limit, fingerprint, and reload outcomes
- `FeatureFlagReloadHealthAnalyzer`: convert reload metrics into health status and operator warnings
- `FeatureFlagReloadAlertPolicy`: convert reload health reports into alert decisions
- `FeatureFlagReloadAlertSuppressor`: cooldown-based suppression for duplicate reload alerts
- `FeatureFlagReloadAlertRouter`: route emitted reload alerts to dashboard or on-call channels
- `FeatureFlagReloadAlertDispatcher`: deliver routed reload alerts to an alert sink
- `FeatureFlagReloadAlertRetryPolicy`: bounded backoff retry planning for failed alert deliveries
- `FeatureFlagReloadAlertDeadLetterStore`: bounded storage for alert deliveries that exhausted retries
- `FeatureFlagReloadAlertDeadLetterReplayer`: bounded replay of alert deliveries from dead-letter storage
- `FeatureFlagReloadAlertDeadLetterReplayCoordinator`: replay and remove successfully delivered dead letters
- `FeatureFlagReloadAlertDeadLetterMonitor`: health analysis for dead-letter backlog pressure
- `FeatureFlagReloadAlertDeadLetterAlertPolicy`: convert dead-letter backlog health into alert payloads
- `FeatureFlagReloadAlertDeadLetterAlertWorkflow`: complete dead-letter alert flow from monitor to dispatch
- `InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow`: counters around dead-letter alert workflow outcomes
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer`: health analysis for dead-letter alert workflow metrics
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy`: convert dead-letter alert workflow health into alert payloads
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline`: dispatch alerts about alert workflow health
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog`: bounded incident history for alert workflow health alerts
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor`: health analysis for incident log pressure
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy`: convert incident log health into alert payloads
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline`: dispatch alerts about incident-log health
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer`: dashboard counters from retained incidents
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner`: prioritized actions from incident summaries
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter`: stable text rendering for triage plans
- `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder`: export-ready triage digest with summary, plan, text, and timestamp
- `PracticeRoadmap`: a code-first practice map that connects the learning-site JSON roadmap to concrete Java files and tests
- `LearningApp`: a single runnable entry point that exercises the examples

## Commands

Run the learning app:

```bash
./mvnw -q -DskipTests compile
java -cp target/classes com.example.javalabs.basic.LearningApp
```

Run unit tests:

```bash
./mvnw test
```

Generate JavaDoc from the source docblocks:

```bash
./mvnw javadoc:javadoc
```

Package the module:

```bash
./mvnw package
```

## How To Read This Module

1. Start with `LearningApp` to see the big picture.
2. Open `PracticeRoadmap` to choose the next source file and matching test.
3. Open one class at a time and read the JavaDoc above each method.
4. Read the corresponding test class to understand expected behavior.
5. Change a method, rerun the tests, and observe what breaks.
6. Generate JavaDoc when you want a browsable API reference from the same docblocks.

Vietnamese practice guide: [`PRACTICE_ROADMAP.vi.md`](PRACTICE_ROADMAP.vi.md).

## Suggested Study Order

1. `ControlFlowExamples`
2. `StringToolkit`
3. `BankAccount`
4. `StudentAnalytics`
5. `Shape`
6. `AsyncExamples`
7. `DifficultyLevel` and `Pair`
8. `InventoryAnalytics`
9. `ExceptionPlayground`
10. `FileReport`
11. `JdbcExamples`
12. `FixedWindowRateLimiter`
13. `SimpleConnectionPool`
14. `MultiDatabaseUserProfileRepository`
15. `RegistrationService`
16. `OrderSummaryService`
17. `CachedCustomerDirectory`
18. `ResilientNotificationClient`
19. `CircuitBreakerNotificationClient`
20. `InstrumentedNotificationClient`
21. `RollingLatencyWindow`
22. `LatencySpikeDetector`
23. `LatencyHistogram`
24. `BucketedThroughputWindow`
25. `AutoscalingPolicy`
26. `AutoscalingCooldownController`
27. `AutoscalingDecisionLog`
28. Bounded cache behavior in `CachedCustomerDirectory`
29. `OrderExportService`
30. `CursorOrderExportService`
31. `IdempotentNotificationClient`
32. `BulkheadNotificationClient`
33. `TimeoutNotificationClient`
34. `AdaptiveTimeoutPolicy`
35. `WelcomeNotificationBatcher`
36. `DeadLetteringNotificationClient`
37. `StockReservationService`
38. `OutboxRegistrationService` and `OutboxDispatcher`
39. `OutboxRetryPolicy`
40. `CustomerLookupService`
41. `TopInventorySelector`
42. `InventoryStockSummarizer`
43. `IndexedInventoryCatalog`
44. `BatchPartitioner` and `ChunkedNotificationSender`
45. `SlidingWindowRateLimiter`
46. `TokenBucketRateLimiter`
47. `UserProfileUpdateService`
48. `IncrementalInventorySummary`
49. `CoalescingCustomerDirectory`
50. `NegativeCachingCustomerDirectory`
51. `StaleCustomerCache`
52. `JitteredBackoffPolicy`
53. `RetryBudget`
54. `BoundedDeadLetterStore`
55. `TwoLevelCustomerCache`
56. `CacheWarmupService`
57. `UserProfileDiffService`
58. `SelectiveProfileChangePublisher`
59. `ProfileChangeEventCoalescer`
60. `PriorityJobQueue`
61. `AgingPriorityJobQueue`
62. `DeadlineJobQueue`
63. `SlaBudgetTracker`
64. `RollingErrorRateWindow`
65. `AdaptiveConcurrencyLimiter`
66. `AdaptiveRetryController`
67. `LoadSheddingController`
68. `GracefulDegradationController`
69. `FeatureFlagEvaluator`
70. `AdaptiveFeatureFlagController`
71. `FeatureFlagRegistry`
72. `FeatureFlagReloader`
73. `AuditedFeatureFlagReloader`
74. `FeatureFlagRollbackPlanner`
75. `FeatureFlagSnapshotStore`
76. `FeatureFlagSnapshotRetentionPolicy`
77. `FeatureFlagReloadValidator`
78. `SafeFeatureFlagReloader`
79. `FeatureFlagConfigFingerprinter`
80. `FingerprintingFeatureFlagReloader`
81. `RateLimitedFeatureFlagReloader`
82. `DebouncedFeatureFlagReloader`
83. `InstrumentedDebouncedFeatureFlagReloader`
84. `FeatureFlagReloadHealthAnalyzer`
85. `FeatureFlagReloadAlertPolicy`
86. `FeatureFlagReloadAlertSuppressor`
87. `FeatureFlagReloadAlertRouter`
88. `FeatureFlagReloadAlertDispatcher`
89. `FeatureFlagReloadAlertRetryPolicy`
90. `FeatureFlagReloadAlertDeadLetterStore`
91. `FeatureFlagReloadAlertDeadLetterReplayer`
92. `FeatureFlagReloadAlertDeadLetterReplayCoordinator`
93. `FeatureFlagReloadAlertDeadLetterMonitor`
94. `FeatureFlagReloadAlertDeadLetterAlertPolicy`
95. `FeatureFlagReloadAlertDeadLetterAlertWorkflow`
96. `InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow`
97. `FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer`
98. `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy`
99. `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline`
100. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog`
101. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor`
102. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy`
103. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline`
104. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer`
105. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner`
106. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter`
107. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder`

## JDBC Study Tip

The JDBC example is best learned through the unit test because it runs against an in-memory H2 database:

```bash
./mvnw -Dtest=JdbcExamplesTest test
```
