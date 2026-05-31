# Lo trinh thuc hanh Java theo learning site

File JSON trong `docs/data/` dang giu phan ly thuyet, cau hoi phong van, quiz va roadmap.
Module `basic/` la noi bien cac noi dung do thanh code Java de doc, chay, sua va viet test.

## Cach hoc moi bai

1. Mo stage trong `PracticeRoadmap.java`.
2. Doc cac file trong `sourceFiles`.
3. Doc test tuong ung trong `testFiles`.
4. Chay rieng test cua bai do.
5. Sua mot input nho, doan test nao se fail, roi chay lai.
6. Ghi lai bang loi cua ban: input vao dau, method nao xu ly, output/exception ra sao.

## Lenh chay

```bash
cd basic
./mvnw test
./mvnw -q -DskipTests compile
java -cp target/classes com.example.javalabs.basic.LearningApp
```

Tren Windows PowerShell co the dung:

```powershell
cd basic
.\mvnw.cmd test
.\mvnw.cmd -q -DskipTests compile
java -cp target/classes com.example.javalabs.basic.LearningApp
```

## Thu tu nen hoc

| Cap | Trong learning site | Code nen doc | Test nen doc |
| --- | --- | --- | --- |
| 1 | Java Core: bien, method, if-else, loop | `ControlFlowExamples`, `StringToolkit` | `ControlFlowExamplesTest`, `StringToolkitTest` |
| 2 | OOP, class, object, invariant, exception | `BankAccount`, `ExceptionPlayground` | `BankAccountTest`, `ExceptionPlaygroundTest` |
| 3 | Collection, stream, sort, filter | `StudentAnalytics`, `InventoryAnalytics` | `StudentAnalyticsTest`, `InventoryAnalyticsTest` |
| 4 | Interface, enum, generic, polymorphism | `Shape`, `Circle`, `Rectangle`, `DifficultyLevel`, `Pair` | `ShapeTest`, `DifficultyLevelTest`, `PairTest` |
| 5 | Data, file, SQL, JDBC | `FileReport`, `JdbcExamples` | `FileReportTest`, `JdbcExamplesTest` |
| 6 | Backend building blocks | `FixedWindowRateLimiter`, `SimpleConnectionPool`, `MultiDatabaseUserProfileRepository` | Cac test cung ten |
| 7 | Service flow, async, side effect | `AsyncExamples`, `RegistrationService` | `AsyncExamplesTest`, `RegistrationServiceTest` |
| 8 | Toi uu backend, tranh N+1 lookup | `OrderSummaryService`, `CustomerDirectory`, `InMemoryCustomerDirectory` | `OrderSummaryServiceTest` |
| 9 | Cache TTL va invalidate | `CachedCustomerDirectory`, `TimeSource`, `ManualTimeSource` | `CachedCustomerDirectoryTest` |
| 10 | Retry co gioi han cho downstream call | `ResilientNotificationClient`, `FlakyNotificationClient`, `NotificationClient` | `ResilientNotificationClientTest` |
| 11 | Circuit breaker cho dependency dang loi | `CircuitBreakerNotificationClient`, `CircuitBreakerState`, `FlakyNotificationClient` | `CircuitBreakerNotificationClientTest` |
| 12 | Do luong truoc khi toi uu | `InstrumentedNotificationClient`, `ClientCallMetrics`, `LatencySimulatingNotificationClient` | `InstrumentedNotificationClientTest` |
| 13 | Cache co gioi han kich thuoc | `CachedCustomerDirectory`, `InMemoryCustomerDirectory`, `Customer` | `CachedCustomerDirectoryTest` |
| 14 | Paging du lieu lon | `OrderExportService`, `PageRequest`, `Page`, `InMemoryOrderRepository` | `OrderExportServiceTest` |
| 15 | Cursor paging cho page sau | `CursorOrderExportService`, `CursorPageRequest`, `CursorPage`, `InMemoryCursorOrderRepository` | `CursorOrderExportServiceTest` |
| 16 | Idempotency cho side effect | `IdempotentNotificationClient`, `IdempotencyStore`, `InMemoryIdempotencyStore` | `IdempotentNotificationClientTest` |
| 17 | Bulkhead gioi han call dong thoi | `BulkheadNotificationClient`, `BlockingNotificationClient`, `NotificationClient` | `BulkheadNotificationClientTest` |
| 18 | Timeout cho dependency cham | `TimeoutNotificationClient`, `LatencySimulatingNotificationClient`, `ManualTimeSource` | `TimeoutNotificationClientTest` |
| 19 | Batch cac call nho | `WelcomeNotificationBatcher`, `NotificationBatchClient`, `InMemoryNotificationBatchClient` | `WelcomeNotificationBatcherTest` |
| 20 | Dead-letter cho side effect that bai | `DeadLetteringNotificationClient`, `DeadLetterStore`, `DeadLetterMessage` | `DeadLetteringNotificationClientTest` |
| 21 | Optimistic locking cho ghi dong thoi | `StockReservationService`, `VersionedInventoryItem`, `InMemoryVersionedInventoryRepository` | `StockReservationServiceTest` |
| 22 | Outbox pattern cho publish tin cay | `OutboxRegistrationService`, `OutboxDispatcher`, `OutboxEventStore` | `OutboxRegistrationServiceTest` |
| 23 | Backoff retry cho outbox | `OutboxRetryPolicy`, `OutboxRetryPlanner`, `OutboxRetryPlan` | `OutboxRetryPolicyTest` |
| 24 | Deduplicate batch lookup | `CustomerLookupService`, `CustomerLookupResult`, `CustomerDirectory` | `CustomerLookupServiceTest` |
| 25 | Top N khong can sort tat ca | `TopInventorySelector`, `InventoryItem` | `TopInventorySelectorTest` |
| 26 | Aggregate mot luot quet | `InventoryStockSummarizer`, `InventoryStockSummary`, `InventoryItem` | `InventoryStockSummarizerTest` |
| 27 | Index lookup theo field phu | `IndexedInventoryCatalog`, `InventoryItem` | `IndexedInventoryCatalogTest` |
| 28 | Chia batch thanh chunk nho | `BatchPartitioner`, `ChunkedNotificationSender`, `NotificationBatchClient` | `BatchPartitionerTest` |
| 29 | Sliding-window rate limit | `SlidingWindowRateLimiter`, `FixedWindowRateLimiter`, `ManualTimeSource` | `SlidingWindowRateLimiterTest` |
| 30 | Token-bucket rate limit | `TokenBucketRateLimiter`, `SlidingWindowRateLimiter`, `ManualTimeSource` | `TokenBucketRateLimiterTest` |
| 31 | Bo qua write khong thay doi | `UserProfileUpdateService`, `CountingUserProfileRepository`, `ProfileUpdateResult` | `UserProfileUpdateServiceTest` |
| 32 | Aggregate incremental | `IncrementalInventorySummary`, `InventoryStockSummary`, `InventoryItem` | `IncrementalInventorySummaryTest` |
| 33 | Gom lookup dang in-flight | `CoalescingCustomerDirectory`, `CustomerDirectory`, `Customer` | `CoalescingCustomerDirectoryTest` |
| 34 | Negative cache cho not-found | `NegativeCachingCustomerDirectory`, `InMemoryCustomerDirectory`, `ManualTimeSource` | `NegativeCachingCustomerDirectoryTest` |
| 35 | Stale-while-revalidate cache | `StaleCustomerCache`, `CustomerDirectory`, `ManualTimeSource` | `StaleCustomerCacheTest` |
| 36 | Backoff retry co jitter | `JitteredBackoffPolicy`, `OutboxRetryPolicy`, `ResilientNotificationClient` | `JitteredBackoffPolicyTest` |
| 37 | Retry budget | `RetryBudget`, `ManualTimeSource`, `JitteredBackoffPolicy` | `RetryBudgetTest` |
| 38 | Dead-letter store co gioi han | `BoundedDeadLetterStore`, `DeadLetterStore`, `DeadLetterMessage` | `BoundedDeadLetterStoreTest` |
| 39 | Two-level customer cache | `TwoLevelCustomerCache`, `CustomerDirectory`, `InMemoryCustomerDirectory` | `TwoLevelCustomerCacheTest` |
| 40 | Cache warmup | `CacheWarmupService`, `CacheWarmupReport`, `TwoLevelCustomerCache` | `CacheWarmupServiceTest` |
| 41 | Snapshot diff truoc khi write | `UserProfileDiffService`, `UserProfileDiff`, `FieldChange` | `UserProfileDiffServiceTest` |
| 42 | Publish event co chon loc | `SelectiveProfileChangePublisher`, `UserProfileChangeEvent`, `UserProfileDiffService` | `SelectiveProfileChangePublisherTest` |
| 43 | Gop change event theo user | `ProfileChangeEventCoalescer`, `UserProfileChangeEvent`, `FieldChange` | `ProfileChangeEventCoalescerTest` |
| 44 | Priority job queue | `PriorityJobQueue`, `BackgroundJob`, `JobPriority` | `PriorityJobQueueTest` |
| 45 | Aging priority queue | `AgingPriorityJobQueue`, `PriorityJobQueue`, `ManualTimeSource` | `AgingPriorityJobQueueTest` |
| 46 | Deadline job queue | `DeadlineJobQueue`, `DeadlineJob`, `BackgroundJob` | `DeadlineJobQueueTest` |
| 47 | SLA budget tracker | `SlaBudgetTracker`, `SlaBudgetSnapshot`, `ServiceCallOutcome` | `SlaBudgetTrackerTest` |
| 48 | Adaptive retry controller | `AdaptiveRetryController`, `RetryDecision`, `RetryBudget`, `SlaBudgetTracker` | `AdaptiveRetryControllerTest` |
| 49 | Load shedding controller | `LoadSheddingController`, `LoadSheddingDecision`, `IncomingRequest` | `LoadSheddingControllerTest` |
| 50 | Graceful degradation | `GracefulDegradationController`, `DegradationDecision`, `ResponseMode` | `GracefulDegradationControllerTest` |
| 51 | Feature flag rollout | `FeatureFlagEvaluator`, `FeatureFlagRule`, `FeatureFlagEvaluation` | `FeatureFlagEvaluatorTest` |
| 52 | Adaptive feature rollout | `AdaptiveFeatureFlagController`, `FeatureFlagEvaluator`, `SlaBudgetTracker` | `AdaptiveFeatureFlagControllerTest` |
| 53 | Feature flag registry | `FeatureFlagRegistry`, `FeatureFlagEvaluator`, `FeatureFlagRule` | `FeatureFlagRegistryTest` |
| 54 | Feature flag reload diff | `FeatureFlagReloader`, `FeatureFlagReloadReport`, `FeatureFlagRegistry` | `FeatureFlagReloaderTest` |
| 55 | Feature flag audit log | `AuditedFeatureFlagReloader`, `FeatureFlagAuditEvent`, `InMemoryFeatureFlagAuditLog` | `AuditedFeatureFlagReloaderTest` |
| 56 | Feature flag rollback plan | `FeatureFlagRollbackPlanner`, `FeatureFlagRollbackPlan`, `FeatureFlagRollbackAction` | `FeatureFlagRollbackPlannerTest` |
| 57 | Versioned feature flag snapshots | `FeatureFlagSnapshotStore`, `FeatureFlagSnapshot`, `FeatureFlagRegistry` | `FeatureFlagSnapshotStoreTest` |
| 58 | Feature flag snapshot retention | `FeatureFlagSnapshotRetentionPolicy`, `FeatureFlagSnapshotRetentionReport`, `FeatureFlagSnapshotStore` | `FeatureFlagSnapshotRetentionPolicyTest` |
| 59 | Feature flag reload validation | `FeatureFlagReloadValidator`, `FeatureFlagReloadValidationReport`, `FeatureFlagRegistry` | `FeatureFlagReloadValidatorTest` |
| 60 | Safe feature flag reload | `SafeFeatureFlagReloader`, `SafeFeatureFlagReloadResult`, `FeatureFlagReloadValidator` | `SafeFeatureFlagReloaderTest` |
| 61 | Feature flag config fingerprint | `FeatureFlagConfigFingerprinter`, `FeatureFlagConfigFingerprint`, `FeatureFlagRegistry` | `FeatureFlagConfigFingerprinterTest` |
| 62 | Fingerprinting feature flag reload | `FingerprintingFeatureFlagReloader`, `FingerprintingFeatureFlagReloadResult`, `FeatureFlagConfigFingerprinter` | `FingerprintingFeatureFlagReloaderTest` |
| 63 | Rate-limited feature flag reload | `RateLimitedFeatureFlagReloader`, `RateLimitedFeatureFlagReloadResult`, `TokenBucketRateLimiter` | `RateLimitedFeatureFlagReloaderTest` |
| 64 | Debounced feature flag reload | `DebouncedFeatureFlagReloader`, `DebouncedFeatureFlagReloadResult`, `DebouncedReloadStatus` | `DebouncedFeatureFlagReloaderTest` |
| 65 | Instrumented debounced feature flag reload | `InstrumentedDebouncedFeatureFlagReloader`, `FeatureFlagReloadMetrics`, `FeatureFlagReloadMetricsSnapshot` | `InstrumentedDebouncedFeatureFlagReloaderTest` |
| 66 | Feature flag reload health analyzer | `FeatureFlagReloadHealthAnalyzer`, `FeatureFlagReloadHealthReport`, `FeatureFlagReloadHealthStatus` | `FeatureFlagReloadHealthAnalyzerTest` |
| 67 | Feature flag reload alert policy | `FeatureFlagReloadAlertPolicy`, `FeatureFlagReloadAlert`, `FeatureFlagReloadHealthReport` | `FeatureFlagReloadAlertPolicyTest` |
| 68 | Feature flag reload alert suppressor | `FeatureFlagReloadAlertSuppressor`, `FeatureFlagReloadAlertDecision`, `FeatureFlagReloadAlert` | `FeatureFlagReloadAlertSuppressorTest` |
| 69 | Feature flag reload alert router | `FeatureFlagReloadAlertRouter`, `FeatureFlagReloadAlertRoute`, `FeatureFlagReloadAlertChannel` | `FeatureFlagReloadAlertRouterTest` |
| 70 | Feature flag reload alert dispatcher | `FeatureFlagReloadAlertDispatcher`, `FeatureFlagReloadAlertDispatchResult`, `InMemoryFeatureFlagReloadAlertSink` | `FeatureFlagReloadAlertDispatcherTest` |
| 71 | Feature flag reload alert retry policy | `FeatureFlagReloadAlertRetryPolicy`, `FeatureFlagReloadAlertRetryPlan`, `FeatureFlagReloadAlertRetryDecision` | `FeatureFlagReloadAlertRetryPolicyTest` |
| 72 | Feature flag reload alert dead-letter store | `FeatureFlagReloadAlertDeadLetterStore`, `FeatureFlagReloadAlertDeadLetter`, `FeatureFlagReloadAlertRetryPlan` | `FeatureFlagReloadAlertDeadLetterStoreTest` |
| 73 | Feature flag reload alert dead-letter replay | `FeatureFlagReloadAlertDeadLetterReplayer`, `FeatureFlagReloadAlertReplayResult`, `FeatureFlagReloadAlertDeadLetterStore` | `FeatureFlagReloadAlertDeadLetterReplayerTest` |
| 74 | Feature flag reload alert replay cleanup | `FeatureFlagReloadAlertDeadLetterReplayCoordinator`, `FeatureFlagReloadAlertReplayCleanupResult`, `FeatureFlagReloadAlertDeadLetterStore` | `FeatureFlagReloadAlertDeadLetterReplayCoordinatorTest` |
| 75 | Feature flag reload alert dead-letter monitor | `FeatureFlagReloadAlertDeadLetterMonitor`, `FeatureFlagReloadAlertDeadLetterHealthReport`, `FeatureFlagReloadAlertDeadLetterStore` | `FeatureFlagReloadAlertDeadLetterMonitorTest` |
| 76 | Feature flag reload alert dead-letter alert policy | `FeatureFlagReloadAlertDeadLetterAlertPolicy`, `FeatureFlagReloadAlertDeadLetterHealthReport`, `FeatureFlagReloadAlert` | `FeatureFlagReloadAlertDeadLetterAlertPolicyTest` |
| 77 | Feature flag reload alert dead-letter alert workflow | `FeatureFlagReloadAlertDeadLetterAlertWorkflow`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowResult`, `FeatureFlagReloadAlertDeadLetterAlertPolicy` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowTest` |
| 78 | Instrumented feature flag reload alert dead-letter workflow | `InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot` | `InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflowTest` |
| 79 | Feature flag reload alert dead-letter workflow health analyzer | `FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzerTest` |
| 80 | Feature flag reload alert dead-letter workflow alert policy | `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport`, `FeatureFlagReloadAlert` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicyTest` |
| 81 | Feature flag reload alert dead-letter workflow alert pipeline | `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipelineTest` |
| 82 | Feature flag reload alert dead-letter workflow incident log | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogTest` |
| 83 | Feature flag reload alert dead-letter workflow incident log monitor | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitorTest` |
| 84 | Feature flag reload alert dead-letter workflow incident log alert policy | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport`, `FeatureFlagReloadAlert` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicyTest` |
| 85 | Feature flag reload alert dead-letter workflow incident log alert pipeline | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipelineTest` |
| 86 | Feature flag reload alert dead-letter workflow incident log summary | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizerTest` |
| 87 | Feature flag reload alert dead-letter workflow incident triage planner | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlannerTest` |
| 88 | Feature flag reload alert dead-letter workflow incident triage formatter | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatterTest` |
| 89 | Feature flag reload alert dead-letter workflow incident triage digest | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest`, `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer` | `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilderTest` |

## Cach doc mot file Java

Khi mo `BankAccount.java`, dung thu tu nay:

1. Doc field truoc: class nay giu state nao?
2. Doc constructor: object hop le can dieu kien gi?
3. Doc public method: nguoi ben ngoai duoc lam gi voi object?
4. Doc exception: input sai bi chan o dau?
5. Doc test: behavior nao dang duoc bao ve?

Vi du voi `BankAccount`:

- `accountNumber`, `ownerName`, `balance` la state.
- Constructor chan account rong, owner rong, balance am.
- `deposit` va `withdraw` la cach duy nhat doi balance.
- `withdraw` nem loi khi rut qua so du.
- `BankAccountTest` cho thay input nao thanh cong va input nao fail.

## Cach noi voi JSON cua site

- `docs/data/roadmap/backend-roadmap.vi.json`: xem minh dang o giai doan nao.
- `docs/data/content/question-bank.vi.json`: doc cau hoi, mo phan `Code`, roi tim code that trong `basic/`.
- `docs/data/quizzes/quiz-bank.vi.json`: lam quiz de biet minh yeu muc nao, sau do quay lai stage tuong ung trong `PracticeRoadmap`.

Muc tieu khong phai hoc thuoc dap an. Muc tieu la doc duoc code, giai thich duoc code chay ra sao, va tu viet duoc test nho de chung minh minh hieu.

## Bai toi uu nen doc ky

`OrderSummaryService` co hai cach build ket qua:

- `buildSummariesWithRepeatedLookup`: moi order goi `findById` mot lan. Code de viet, nhung neu co 100 order thi co the thanh 100 lan goi database.
- `buildSummariesWithBatchLookup`: gom customer id truoc, goi `findByIds` mot lan, roi map du lieu trong bo nho.

Day la cach hoc toi uu dung huong: khong toi uu cam tinh. Hay giu output giong nhau, viet test do so lan lookup, roi moi ket luan cach nao tot hon trong boi canh backend.

## Bai cache nen doc ky

`CachedCustomerDirectory` la decorator boc quanh `CustomerDirectory`:

- Lan doc dau tien chua co cache nen goi delegate.
- Lan doc thu hai trong TTL lay tu cache, khong goi delegate nua.
- Khi TTL het han, entry bi xem la cu va se load lai.
- Khi co write/update, `invalidate(id)` xoa cache cua customer do de lan doc sau lay du lieu moi.

Day la pattern hay gap trong backend: cache giup giam tai database, nhung phai co gioi han stale data. Test dung `ManualTimeSource` de dieu khien thoi gian thay vi phu thuoc dong ho that.

## Bai retry nen doc ky

`ResilientNotificationClient` la decorator boc quanh `NotificationClient`:

- Neu downstream loi tam thoi, wrapper thu lai toi da `maxAttempts`.
- Neu thanh cong o lan thu 2 hoac 3, service phia tren khong can biet chi tiet loi tam thoi.
- Neu van fail sau so lan cho phep, wrapper nem loi ro rang thay vi lap vo han.

Day la tu duy reliability can co khi phat trien backend: retry phai co gioi han, phai gan voi side effect, va khong nen rai logic retry vao moi method nghiep vu.

## Bai circuit breaker nen doc ky

`CircuitBreakerNotificationClient` bao ve he thong khi downstream lien tuc loi:

- `CLOSED`: goi binh thuong, neu loi thi dem failure.
- `OPEN`: sau khi loi du nguong, fail nhanh ma khong goi delegate nua.
- `HALF_OPEN`: sau cooldown, cho mot lan goi thu de xem downstream da hoi phuc chua.

Pattern nay giup tranh viec moi request deu tiep tuc doi mot service dang chet. Day la toi uu o muc he thong: giam latency, giam tai, va lam loi ro rang hon.

## Bai metrics nen doc ky

`InstrumentedNotificationClient` la decorator dung de do client call:

- Dem tong so call.
- Dem call thanh cong va call that bai.
- Do tong thoi gian va average duration.
- Van nem loi nhu delegate goc, khong nuot exception.

Day la nen tang cua toi uu co co so. Truoc khi toi uu, hay do xem bottleneck nam o dau: so lan goi qua nhieu, latency cao, hay failure rate tang.

## Bai bounded cache nen doc ky

TTL chi giai quyet van de stale data, khong giai quyet viec cache co qua nhieu key. `CachedCustomerDirectory` co constructor nhan `maxEntries` de gioi han so entry trong cache:

- Khi cache chua day, entry moi duoc them binh thuong.
- Khi vuot qua `maxEntries`, entry it duoc dung gan day nhat se bi evict.
- Khi key da bi evict duoc doc lai, delegate se bi goi lai.

Day la bai hoc quan trong: toi uu bang cache phai di cung gioi han memory. Cache nhanh nhung khong co gioi han co the lam ung dung cham hoac het bo nho.

## Bai paging nen doc ky

`OrderExportService` co hai cach xu ly export:

- `exportByLoadingAll`: goi `findAll`, load tat ca order vao memory roi xu ly.
- `exportInPages`: doc tung page nho, tinh tong va tiep tuc page tiep theo.

Ket qua nghiep vu giong nhau, nhung trade-off khac nhau. Load all it repository call hon nhung ton memory hon. Paging nhieu call hon, nhung an toan hon khi du lieu lon.

## Bai cursor paging nen doc ky

Offset paging dung `page` va `size`, rat de hieu. Nhưng khi page sau qua sau, database thuong phai bo qua rat nhieu dong truoc khi lay du lieu can thiet.

`CursorOrderExportService` dung cursor:

- Page dau tien khong co `lastSeenId`.
- Moi page tra ve `nextCursor` la id cuoi cung da doc.
- Request tiep theo truyen cursor do vao `CursorPageRequest`.

Day la cach doc du lieu lon on dinh hon khi can di sau nhieu page, dac biet voi danh sach co sort key ro rang nhu id hoac created time.

## Bai idempotency nen doc ky

Retry va message redelivery co the lam cung mot thao tac chay lai. Neu thao tac do la side effect, vi du gui notification, tru tien hoac tao event, duplicate co the gay loi nghiem trong.

`IdempotentNotificationClient` dung key on dinh:

- Key welcome notification la `WELCOME:{userId}`.
- Lan dau tien key duoc mark va delegate duoc goi.
- Lan sau voi cung key se bi skip, khong gui notification trung.

Day la pattern phai hoc khi di vao backend nang cao: retry giup tang kha nang thanh cong, idempotency giup retry khong tao side effect trung.

## Bai bulkhead nen doc ky

`BulkheadNotificationClient` gioi han so call dang chay cung luc:

- Neu so call dang chay nho hon gioi han, delegate duoc goi.
- Neu dependency dang cham va slot da day, call moi fail nhanh voi loi `bulkhead is full`.
- Slot luon duoc giai phong trong `finally`, ke ca khi delegate nem exception.

Bulkhead khac retry va circuit breaker. Retry thu lai khi loi tam thoi. Circuit breaker dung goi khi dependency dang loi lien tuc. Bulkhead bao ve tai nguyen khi dependency cham hoac co qua nhieu call dong thoi.

## Bai timeout nen doc ky

`TimeoutNotificationClient` dat ngan sach thoi gian cho mot downstream call:

- Neu call xong trong timeout, request duoc xem la hop le.
- Neu thoi gian vuot qua timeout, wrapper bao loi ro rang.
- Neu delegate tu nem loi, loi goc van duoc truyen ra.

Trong production, timeout nen duoc cau hinh o HTTP client, database driver hoac message client that. Vi du nay dung `ManualTimeSource` de ban doc va test duoc y tuong cot loi ma khong can cho thoi gian that.

## Bai batching nen doc ky

`WelcomeNotificationBatcher` gom nhieu notification nho truoc khi goi downstream:

- Du batch size thi tu dong flush.
- Chua du batch size thi co the flush thu cong.
- 5 message voi batch size 2 chi can 3 downstream calls.

Batching toi uu throughput va giam round-trip, nhung co trade-off: message co the doi trong buffer lau hon. Khi dung batching, phai quyet dinh batch size, thoi diem flush, va cach xu ly loi khi mot batch fail.

## Bai dead-letter nen doc ky

Khi side effect that bai sau retry, khong nen chi de loi bien mat trong log. `DeadLetteringNotificationClient` luu that bai vao store:

- Key cho biet thao tac nao that bai.
- Payload giu du lieu can de dieu tra hoac replay.
- Reason giu nguyen nhan loi.

Dead-letter giup van hanh he thong thuc te: ban co the xem viec nao fail, can replay viec nao, va do failure rate thay vi chi doan qua log.

## Bai optimistic locking nen doc ky

Khi hai request cung doc mot record roi cung ghi lai, request sau co the vo tinh ghi de thay doi cua request truoc. `VersionedInventoryItem` dung field `version` de chan lost update:

- Moi read nhan duoc quantity va version hien tai.
- Save can truyen `expectedVersion`.
- Neu version trong repository da doi, save bi reject bang `OptimisticLockException`.
- Neu save thanh cong, version tang len 1.

Pattern nay hay gap trong database qua cot `version` hoac `updated_at`. No giup bao ve consistency ma khong can lock moi read.

## Bai outbox nen doc ky

Neu service vua save database vua publish event truc tiep, co mot diem nguy hiem: save thanh cong nhung publish fail. Khi do du lieu da thay doi nhung downstream khong biet.

`OutboxRegistrationService` tach flow nay:

- Save `UserProfile`.
- Save `OutboxEvent` trang thai `PENDING`.
- `OutboxDispatcher` doc pending event va publish sau.
- Neu publish fail, event duoc mark `FAILED` va co the retry.
- Neu publish thanh cong, event duoc mark `PUBLISHED`.

Outbox la pattern rat quan trong cho backend co event, notification, Kafka/RabbitMQ hoac bat ky side effect nao can tin cay.

## Bai outbox retry backoff nen doc ky

Outbox giu event de retry, nhung retry lien tuc ngay lap tuc co the lam dependency dang loi cang qua tai. `OutboxRetryPolicy` tinh delay tang dan:

- Attempt 0: delay co ban.
- Attempt 1: delay gap doi.
- Attempt 2: delay tiep tuc tang.
- Delay bi gioi han boi `maxDelayMillis`.
- Qua `maxAttempts` thi khong retry nua.

Backoff giup he thong co thoi gian hoi phuc va tranh viec hang nghin event fail cung luc tao thanh vong lap retry qua tai.

## Bai deduplicate batch lookup nen doc ky

Khi caller gui danh sach id co trung lap, service khong nen day tat ca id trung xuong database/cache/HTTP batch call. `CustomerLookupService` lam 2 viec:

- Dung `LinkedHashSet` de lay unique id theo thu tu on dinh.
- Goi repository mot lan voi unique id.
- Map ket qua ve lai dung thu tu request ban dau, ke ca khi id bi lap.

Day la toi uu nho nhung hay gap: giam input thua truoc khi vao tang ton chi phi hon, nhung van giu contract output cho caller.

## Bai top N nen doc ky

Neu chi can top 10 item trong hang tram nghin item, sort toan bo list co the la viec thua. `TopInventorySelector` cho ban so sanh hai cach:

- `topByFullSort`: sort tat ca item roi lay N item dau.
- `topByBoundedHeap`: chi giu heap kich thuoc N trong khi quet danh sach.

Full sort de doc va tot voi list nho. Bounded heap huu ich khi input rat lon nhung N nho. Day la cach hoc toi uu thuat toan: giu output giong nhau, doi data structure de giam viec thua.

## Bai one-pass aggregation nen doc ky

`InventoryStockSummarizer` tinh count, total, min va max trong mot vong lap:

- Khong can tao collection trung gian.
- Khong can quet list nhieu lan cho tung thong ke.
- Average duoc tinh tu total va count sau khi quet xong.

Day la toi uu nen nam vung khi xu ly du lieu lon, file lon, stream event hoac report. Diem quan trong la giu code ro rang, validate input som, va chi toi uu khi cach tinh mot luot van de doc.

## Bai indexed lookup nen doc ky

Neu lap lai viec tim item theo category, scan toan bo list moi lan la viec thua. `IndexedInventoryCatalog` build index `category -> items` mot lan:

- `findByCategoryScanning` la baseline de doc va so sanh.
- `findByCategoryIndexed` lay tu map da build san.
- Category duoc normalize de tranh bug do hoa thuong/khoang trang.

Trade-off: index tang memory va can rebuild/update khi data thay doi, nhung lookup lap lai se nhanh va ro rang hon.

## Bai chunked batch processing nen doc ky

Batching gom nhieu item de giam round-trip, nhung batch qua lon lai co the qua gioi han API, timeout hoac ton memory. `BatchPartitioner` chia list lon thanh chunk nho:

- Giu nguyen thu tu input.
- Chunk cuoi co the nho hon chunk size.
- `ChunkedNotificationSender` gui tung chunk qua batch client.

Pattern nay hay dung khi goi API co limit, insert database theo lo, export du lieu, hoac xu ly job nen co kich thuoc buoc ro rang.

## Bai sliding-window rate limiter nen doc ky

`FixedWindowRateLimiter` de doc va re, nhung co the cho burst o ranh gioi window. `SlidingWindowRateLimiter` luu timestamp gan day cua tung key:

- Moi request moi se xoa timestamp da het han.
- Neu so timestamp con lai da dat limit, request bi tu choi.
- Khi request cu nhat het han, chi slot do duoc mo lai, khong reset toan bo window.

Trade-off: sliding window muot hon nhung ton memory hon vi can luu timestamp gan day cho tung key.

## Bai token-bucket rate limiter nen doc ky

`TokenBucketRateLimiter` mo hinh moi client co mot bucket token:

- Bucket day san luc dau, nen cho phep burst co gioi han.
- Moi request thanh cong tieu thu mot token.
- Token duoc refill dan theo thoi gian.
- Token khong vuot qua capacity.

Pattern nay hay dung khi muon cho phep client burst ngan sau thoi gian idle, nhung van giu toc do trung binh on dinh. So sanh no voi fixed window va sliding window de hieu trade-off ve burst, memory va do muot.

## Bai skip no-op write nen doc ky

Khong phai update nao cung can ghi database. `UserProfileUpdateService` doc profile hien tai, normalize email moi, roi so sanh:

- Neu email khong doi, tra ve `email unchanged` va khong goi `save`.
- Neu email doi, tao `UserProfile` moi va save mot lan.
- `CountingUserProfileRepository` giup test thay ro so lan write that.

Pattern nay giam DB write, giam lock/contention, va tranh side effect khong can thiet nhu audit/event khi du lieu thuc chat khong thay doi.

## Bai incremental aggregation nen doc ky

`InventoryStockSummarizer` tinh summary bang cach quet list mot lan. Cach do tot khi can tinh lai tu dau. Nhung neu du lieu thay doi tung item nho, co the cap nhat summary incremental:

- `addOrUpdate` them item moi hoac thay the item cu.
- `remove` xoa item va cap nhat total/min/max.
- `TreeMap` giu count theo quantity de lay min/max sau khi remove ma khong scan lai toan bo.

Trade-off: incremental aggregation nhanh cho update nho lap lai, nhung code phuc tap hon va phai dam bao state khong bi lech.

## Bai coalescing lookup nen doc ky

Cache luu ket qua sau khi call xong. Coalescing thi khac: no gom cac call trung key dang dien ra cung luc.

`CoalescingCustomerDirectory` hoat dong nhu sau:

- Thread dau tien tao lookup in-flight va goi delegate.
- Thread thu hai hoi cung id trong luc lookup chua xong se cho chung `CompletableFuture`.
- Khi lookup xong, ca hai caller nhan cung ket qua.
- Sau khi xong, in-flight entry bi xoa; call sau do khong phai cache hit.

Pattern nay huu ich de tranh cache stampede hoac duplicate downstream calls khi nhieu request dong thoi hoi cung mot key.

## Bai negative caching nen doc ky

Cache thong thuong hay luu ket qua tim thay. Nhung voi key khong ton tai, neu request lap lai lien tuc thi DB van bi hit lien tuc. `NegativeCachingCustomerDirectory` cache ca `Optional.empty()`:

- Missing lookup lan dau goi delegate.
- Missing lookup lan hai trong TTL tra tu cache.
- Qua TTL thi load lai.
- Co `invalidate` de xoa cache khi biet du lieu vua duoc tao.

Negative cache huu ich cho id sai, username khong ton tai, feature flag missing, hoac lookup external system. TTL nen ngan de tranh giu "not found" qua lau khi du lieu moi vua xuat hien.

## Bai stale-while-revalidate nen doc ky

TTL cache binh thuong se het han roi bat buoc load lai. Neu downstream dang loi dung luc do, request fail. `StaleCustomerCache` them mot stale window:

- Trong fresh TTL: tra cache binh thuong.
- Qua fresh TTL nhung con trong stale TTL: thu refresh, nhung van co the tra stale value.
- Qua stale TTL: stale value khong con duoc dung, reload fail thi request fail.

Pattern nay huu ich cho read-heavy data chap nhan stale ngan han, vi no tang availability trong loi ngan cua downstream. Trade-off la caller co the nhan du lieu cu trong mot khoang thoi gian co kiem soat.

## Bai jittered backoff nen doc ky

`OutboxRetryPolicy` dung exponential backoff de retry cach nhau xa dan. Nhung neu nhieu request cung fail mot luc, tat ca co the retry lai cung thoi diem. `JitteredBackoffPolicy` them jitter vao delay:

- Delay goc van tang theo exponential backoff.
- `jitterRatio` tao khoang dao dong quanh delay goc.
- Delay cuoi cung khong vuot qua `maxDelayMillis`.
- Khi jitter bang 0, behavior quay ve exponential backoff binh thuong.

Pattern nay hay dung cho retry HTTP, publish event, job queue, hoac call external API. Muc tieu khong phai retry nhanh nhat, ma retry deu hon de dependency dang hoi phuc khong bi danh sap lan nua.

## Bai retry budget nen doc ky

Retry giup he thong vuot qua loi tam thoi, nhung retry qua nhieu co the lam dependency dang loi bi qua tai nang hon. `RetryBudget` dat gioi han retry trong rolling window:

- Moi retry thanh cong trong viec xin budget se duoc ghi timestamp.
- Khi so retry trong window dat limit, retry tiep theo bi tu choi.
- Khi timestamp cu het window, budget duoc mo lai.
- `remainingRetries` chi xem con bao nhieu budget, khong tieu thu budget.

Pattern nay nen ket hop voi exponential backoff, jitter, timeout va circuit breaker. Diem can nho: retry budget gioi han retry traffic, khac voi rate limiter gioi han request thuong.

## Bai bounded dead-letter store nen doc ky

Dead-letter giup giu lai message that bai de dieu tra hoac replay. Nhung neu he thong loi lau, viec giu tat ca message trong memory co the lam app sap. `BoundedDeadLetterStore` dat capacity ro rang:

- Store giu message moi nhat theo thu tu insert.
- Khi day capacity, message cu nhat bi drop.
- `droppedCount` cho biet da mat bao nhieu message.
- `findAll` tra snapshot copy de code ben ngoai khong sua truc tiep state ben trong.

Pattern nay hay dung trong in-memory buffer, log tam, dead-letter tam thoi, hoac queue nho trong service. Trong production, neu message quan trong thi can persistent store, alert theo dropped count, va chinh sach replay ro rang.

## Bai two-level cache nen doc ky

Mot cache duy nhat de doc, nhung trong he thong lon thuong co nhieu tang cache. `TwoLevelCustomerCache` mo phong L1 nho/nhanh va L2 lon hon:

- Lookup truoc het tim trong L1.
- Neu L1 miss nhung L2 hit, value duoc promote lai vao L1.
- Neu ca L1 va L2 deu miss, moi goi delegate repository.
- Ca L1 va L2 deu co capacity de tranh tang memory vo han.

Pattern nay hay gap o CPU cache, local in-memory cache + distributed cache, hoac service cache + database. Bai nay nen so sanh voi `CachedCustomerDirectory`: TTL cache tap trung vao thoi gian het han, two-level cache tap trung vao tang luu tru va promotion.

## Bai cache warmup nen doc ky

Cache thuong chi co du lieu sau request dau tien, nen request dau tien phai chiu miss. `CacheWarmupService` preload cac id quan trong truoc:

- Input duoc de-duplicate nhung giu thu tu.
- Moi id unique duoc lookup mot lan qua cache-backed directory.
- Id tim thay duoc tinh vao `loadedIds`.
- Id khong tim thay duoc tinh vao `missingIds`, khong lam fail ca dot warmup.

Pattern nay huu ich cho du lieu hot nhu user VIP, feature config, product ban chay, hoac dashboard hay mo. Trade-off: warmup ton thoi gian luc start/schedule, nen chi nen warmup tap key that su co kha nang duoc doc som.

## Bai snapshot diff nen doc ky

Skip no-op write chi tra loi cau hoi "co can ghi khong?". Snapshot diff tra loi tiep cau hoi "field nao da doi?". `UserProfileDiffService` so sanh hai snapshot:

- Hai profile phai cung `userId`.
- Email doi thi tao `FieldChange` cho email.
- Region doi thi tao `FieldChange` cho region.
- Neu khong co field nao doi, `hasChanges` la false.

Pattern nay huu ich khi can audit log, publish event chi chua field thay doi, giam write khong can thiet, hoac build UI hien thi lich su thay doi. Nen doc no sau `UserProfileUpdateService` de thay khac biet giua skip write va diff chi tiet.

## Bai selective change event nen doc ky

Sau khi co diff, khong phai luc nao cung can publish event. `SelectiveProfileChangePublisher` chi publish khi `UserProfileDiff` co thay doi:

- Profile khong doi thi tra `false` va khong tao event.
- Profile co thay doi thi tao `UserProfileChangeEvent`.
- Event chi chua danh sach `FieldChange`, khong can gui thua du lieu.
- `InMemoryUserProfileChangePublisher` giup test thay ro so event da publish.

Pattern nay giam event rac, giam tai consumer, va tranh duplicate processing. Trong he thong lon, cach nay thuong di cung outbox pattern de dam bao event da duoc tao thi se publish tin cay.

## Bai coalesce change event nen doc ky

Neu mot user bi update nhieu lan trong mot batch, consumer co the khong can xu ly tung event nho. `ProfileChangeEventCoalescer` gop event theo `userId`:

- Giu thu tu user xuat hien dau tien trong batch.
- Voi cung mot field, giu `oldValue` dau tien va `newValue` cuoi cung.
- Neu field doi roi quay ve gia tri cu, field do bi loai bo vi khong con net change.
- Neu mot user khong con field nao thay doi, event cua user do khong duoc emit.

Pattern nay giam so event, giam work cho consumer, va lam payload gon hon. Trade-off la can hieu ro ordering va business rule: khong nen coalesce neu consumer can thay tung buoc trung gian.

## Bai priority job queue nen doc ky

Queue FIFO don gian xu ly job theo thu tu vao hang doi. Nhung background worker thuong can xu ly viec quan trong truoc. `PriorityJobQueue` dung `PriorityQueue` de sap xep:

- `JobPriority.HIGH` duoc xu ly truoc `NORMAL` va `LOW`.
- Neu hai job cung priority, sequence number giu FIFO.
- `poll` lay mot job tiep theo.
- `drain(maxJobs)` lay nhieu job theo dung thu tu uu tien.

Pattern nay huu ich cho payment timeout, security task, retry quan trong, hoac notification khan. Trade-off: neu high-priority den lien tuc, low-priority co the bi doi lau; he thong production thuong can aging hoac quota de tranh starvation.

## Bai aging priority queue nen doc ky

Priority queue thuong co rui ro starvation: job `LOW` co the cho rat lau neu job `HIGH` den lien tuc. `AgingPriorityJobQueue` them aging:

- Moi job co priority goc va thoi diem enqueue.
- Cu moi `agingIntervalMillis`, effective weight tang them 1.
- Job cu hon co the vuot job moi hon neu da cho du lau.
- Neu effective weight bang nhau, sequence number giu FIFO.

Pattern nay huu ich cho worker xu ly background job, retry queue, batch processing, hoac scheduler noi bo. Trade-off: moi lan poll phai tinh effective weight theo thoi gian, phuc tap hon priority queue tinh.

## Bai deadline job queue nen doc ky

Priority cho biet job nao quan trong hon, nhung deadline cho biet job nao sap tre han. `DeadlineJobQueue` chon job theo earliest deadline first:

- Deadline som hon duoc xu ly truoc.
- Neu cung deadline, priority cao hon duoc xu ly truoc.
- Neu van bang nhau, FIFO theo sequence.
- `overdueJobs` giup thay job nao da qua deadline.
- `millisUntilNextDeadline` giup worker/monitoring biet con bao lau toi deadline gan nhat.

Pattern nay huu ich cho SLA task, job co timeout, workflow co due date, hoac batch can tranh tre han. Trade-off: job rat quan trong nhung deadline xa co the bi doi sau job deadline gan hon, nen can chon rule scheduling theo dung business.

## Bai SLA budget tracker nen doc ky

Neu chi biet "service dang loi" thi kho quyet dinh hanh dong. Error budget bien reliability thanh con so do duoc. `SlaBudgetTracker` ghi nhan call trong rolling window:

- `SUCCESS` tang tong call thanh cong.
- `FAILURE` tang tong call loi.
- Snapshot tinh availability, error rate, allowed failures va remaining budget.
- Call qua rolling window bi loai bo khoi snapshot.
- Khi failure vuot allowed failures, `budgetExhausted` la true.

Pattern nay hay dung de dieu khien retry budget, circuit breaker, deploy freeze, alert, hoac degrade feature. Diem can nho: error budget khong sua loi, no giup he thong quyet dinh luc nao nen giam rui ro.

## Bai adaptive retry controller nen doc ky

Retry khong nen chi dua vao so lan thu lai. Khi service dang mat error budget, retry them co the lam tinh hinh xau hon. `AdaptiveRetryController` ghep hai dieu kien:

- `SlaBudgetTracker` cho biet service con error budget khong.
- `RetryBudget` gioi han retry traffic cuc bo.
- Neu SLA budget da can, retry bi chan truoc va retry budget khong bi tieu thu.
- Neu retry budget da can, retry bi chan du SLA van con tot.
- `preview` chi xem quyet dinh, khong tieu thu budget.

Pattern nay hay dung trong client goi downstream, job retry, event publisher, hoac API gateway. Diem can nho: retry la cong cu phuc hoi loi tam thoi, khong phai cach xu ly khi service da qua tai.

## Bai load shedding controller nen doc ky

Khi he thong qua tai, co luc can tu choi bot request de giu phan quan trong con song. `LoadSheddingController` quyet dinh dua tren queue pressure va SLA budget:

- Duoi soft limit thi request duoc accept.
- Dat soft limit thi low-priority request bi shed.
- Dat hard limit thi moi priority deu bi shed.
- Neu SLA budget da can, non-high-priority request bi shed truoc.
- High-priority request duoc uu tien den khi cham hard limit.

Pattern nay hay dung trong API gateway, worker queue, search/autocomplete, report job, hoac downstream client. Trade-off: load shedding lam mot so request fail nhanh, nhung giup he thong tranh sap toan bo.

## Bai graceful degradation nen doc ky

Load shedding thuong tra loi "accept hay reject". Graceful degradation them lua chon thu ba: tra response nho hon khi request co the chap nhan tinh nang rut gon.

- Neu load shedding accept, response mode la `FULL`.
- Neu request bi shed nhung `canDegrade` la true va khong phai high-priority, response mode la `DEGRADED`.
- Neu hard limit hoac request khong the degrade, response mode la `REJECTED`.
- `DegradationDecision` giu mode va reason de caller log/metric ro rang.

Pattern nay hay dung cho search bo bot ranking phu, dashboard bo bot widget phu, API tra du lieu cached/rut gon, hoac UI an tinh nang phu khi he thong qua tai.

## Bai feature flag rollout nen doc ky

Deploy code khac voi bat tinh nang. Feature flag giup dua code len production nhung chi mo cho mot phan user. `FeatureFlagEvaluator` dung stable bucket:

- Cung `flagName` va `userId` luon ra cung bucket tu 0 den 99.
- `rolloutPercentage` quyet dinh bucket nao duoc bat.
- Flag disabled thi tat cho moi user.
- Rollout 0% tat het, rollout 100% bat het.

Pattern nay huu ich cho canary release, A/B test, rollback nhanh, va giam rui ro khi SLA budget dang xau. Khi doc bai nay, so sanh voi load shedding: feature flag giam rui ro truoc/sau deploy, load shedding bao ve he thong khi dang qua tai.

## Bai adaptive feature rollout nen doc ky

Feature flag rollout thuong la cau hinh tinh. Nhung khi SLA budget can, co the can giam rollout tu dong de bao ve he thong. `AdaptiveFeatureFlagController` ghep feature flag voi SLA budget:

- Khi SLA budget con tot, dung rollout goc.
- Khi SLA budget da can, dung rollout nho hon.
- Degraded rollout khong bao gio duoc tang cao hon rollout goc.
- Bucket van on dinh theo user, nen user khong bi bat/tat ngau nhien moi request.

Pattern nay huu ich cho canary release, tinh nang moi co rui ro cao, hoac rollout phu thuoc health cua service. Diem can nho: adaptive rollout la co che giam rui ro, khong thay the monitoring va rollback that.

## Bai feature flag registry nen doc ky

Mot evaluator chi xu ly mot rule. Trong app that, ban can quan ly nhieu flag va cap nhat config an toan. `FeatureFlagRegistry` giu rules theo ten flag:

- `find` tra rule neu da cau hinh.
- `findOrDisabled` tra default disabled cho flag missing.
- `upsert` tra `false` neu rule khong doi, giup skip reload khong can thiet.
- `snapshot` tra immutable map de caller khong sua state ben trong.
- `evaluate` ket hop registry voi `FeatureFlagEvaluator`.

Pattern nay huu ich khi load flag tu file, database, config service, hoac admin UI. Diem can nho: missing flag nen fail-safe thanh disabled, khong nen mac dinh bat tinh nang moi.

## Bai feature flag reload diff nen doc ky

Reload config lien tuc nhung khong phai lan nao cung co thay doi. Neu moi reload deu ghi log/write DB/publish event, he thong se bi nhieu noise. `FeatureFlagReloader` so sanh snapshot moi voi registry hien tai:

- Flag moi xuat hien duoc tinh la added.
- Flag co cung ten nhung rule khac duoc tinh la updated.
- Flag cu khong con trong snapshot moi duoc tinh la removed.
- Flag khong doi duoc tinh la unchanged.
- Report co `hasChanges` va `changeCount` de caller quyet dinh co can lam tiep khong.

Pattern nay huu ich cho config reload, feature flag sync, cache refresh, va admin config. Nguyen tac chinh: chi lam side effect khi config that su doi.

## Bai feature flag audit log nen doc ky

Sau khi reload config, ban thuong can audit de biet ai/lan nao da thay doi flag. Nhung reload khong doi thi khong nen ghi log. `AuditedFeatureFlagReloader` boc `FeatureFlagReloader`:

- Reload config nhu binh thuong.
- Neu report co changes, tao `FeatureFlagAuditEvent`.
- Neu report khong doi, khong ghi audit event.
- Audit event chi giu added/updated/removed, bo qua unchanged de payload gon.

Pattern nay huu ich cho audit trail, compliance, debugging rollout, va admin UI. Diem can nho: audit log nen du de truy vet thay doi, nhung khong nen tao noise cho moi reload dinh ky.

## Bai feature flag rollback plan nen doc ky

Audit cho biet flag nao da doi. Rollback plan bien thong tin do thanh hanh dong van hanh. `FeatureFlagRollbackPlanner` doc `FeatureFlagAuditEvent`:

- Flag moi added thi rollback bang cach disable hoac remove flag.
- Flag updated thi can restore rule cu tu config history.
- Flag removed thi can re-add rule cu tu config history.
- Audit rong thi plan rong.

Pattern nay huu ich sau khi rollout gay loi va can rollback nhanh. Diem can nho: audit event chi co ten flag la chua du de auto-restore rule cu; production can luu old/new config snapshot de rollback tu dong an toan.

## Bai versioned feature flag snapshot nen doc ky

Rollback can du lieu cu, khong chi can ten flag. `FeatureFlagSnapshotStore` luu cac snapshot config theo version:

- Moi snapshot la immutable map cua flag rules.
- `latest` tra version moi nhat.
- `restore(version)` tao lai `FeatureFlagRegistry` tu snapshot cu.
- Duplicate flag trong cung snapshot lay rule cuoi cung.
- Khong cho luu trung version de tranh rollback mo ho.

Pattern nay huu ich cho config service, feature flag admin, rollback production, va audit history. Diem can nho: snapshot versioning ton storage hon, nhung doi lai rollback nhanh va ro rang.

## Bai feature flag snapshot retention nen doc ky

Luu snapshot giup rollback, nhung giu tat ca version mai mai se lam storage tang. `FeatureFlagSnapshotRetentionPolicy` tinh retention:

- Sap xep snapshot theo version.
- Giu lai N version moi nhat.
- Version cu hon duoc dua vao `prunedVersions`.
- Report cho biet kept/pruned de caller quyet dinh xoa that hay chi log.

Pattern nay huu ich cho config history, audit storage, backup metadata, va rollback window. Trade-off: giu it snapshot thi tiet kiem storage nhung rollback duoc it moc hon.

## Bai feature flag reload validation nen doc ky

Truoc khi reload config vao registry dang chay, nen co buoc pre-flight validation de chan loi van hanh. `FeatureFlagReloadValidator` kiem tra:

- Duplicate flag trong snapshot moi.
- Flag moi bat ngay voi rollout qua lon.
- Flag cu tang rollout qua nhanh so voi nguong cho phep.
- Flag disabled khong bi chan boi rollout percentage vi no khong mo traffic.
- Report tra danh sach violation de caller quyet dinh co reload hay khong.

Pattern nay huu ich cho admin UI, config service, canary rollout, va release automation. Nguyen tac chinh: validate truoc side effect, reload sau khi config da du an toan.

## Bai safe feature flag reload nen doc ky

Validation rieng la chua du neu caller van co the quen kiem tra report va reload truc tiep. `SafeFeatureFlagReloader` gom hai buoc vao mot workflow:

- Goi `FeatureFlagReloadValidator` truoc.
- Neu validation rejected, tra result co violations va khong sua registry.
- Neu validation accepted, goi `FeatureFlagReloader` de apply config.
- Result co `applied`, `rejected`, va optional reload diff.

Pattern nay huu ich khi side effect nguy hiem va can mot API kho dung sai. Nguyen tac chinh: dua thu tu dung vao trong code, de caller khong phai nho tung buoc thu cong.

## Bai feature flag config fingerprint nen doc ky

Khi config reload theo lich, nhieu lan snapshot moi thuc ra giong snapshot cu. `FeatureFlagConfigFingerprinter` tao digest on dinh:

- Sort rule theo ten flag truoc khi hash.
- Cung config nhung khac thu tu van ra cung fingerprint.
- Doi enabled hoac rollout percentage se doi digest.
- Fingerprint co algorithm, digest va rule count de debug.
- Registry snapshot cung co the duoc fingerprint truc tiep.

Pattern nay huu ich de skip validate/reload/audit khi config khong doi, hoac de so sanh config giua cac node. Nguyen tac chinh: truoc khi hash phai canonicalize du lieu, neu khong thu tu input co the tao false change.

## Bai fingerprinting feature flag reload nen doc ky

Fingerprint rieng cho biet config co doi hay khong. `FingerprintingFeatureFlagReloader` dung no de toi uu workflow reload:

- Tinh fingerprint cua registry hien tai.
- Tinh fingerprint cua config moi.
- Neu hai fingerprint giong nhau, skip validation va reload.
- Neu fingerprint khac, van goi `SafeFeatureFlagReloader` de validate va apply an toan.
- Result cho biet reload bi skip hay da chay safe reload.

Pattern nay huu ich cho config sync chay dinh ky, noi nhieu node doc cung mot config source. Diem can nho: chi skip khi fingerprint giong nhau; config da doi van phai qua validation.

## Bai rate-limited feature flag reload nen doc ky

Neu admin UI, webhook, hoac config watcher goi reload qua day, he thong co the ton CPU cho validate/diff/audit lien tuc. `RateLimitedFeatureFlagReloader` dat token bucket o ngoai workflow:

- Moi reload attempt can mot token.
- Het token thi tra result blocked va khong goi delegate.
- Khi thoi gian troi qua, bucket refill va reload lai duoc phep.
- Delegate ben trong van la `FingerprintingFeatureFlagReloader`, nen config khong doi van duoc skip.
- Result cho biet allowed/blocked, token con lai, va optional fingerprint result.

Pattern nay huu ich cho config sync, admin operation, webhook consumer, va job scheduler. Nguyen tac chinh: rate limit la vong bao ve ben ngoai, khong tron vao validation hay business diff.

## Bai debounced feature flag reload nen doc ky

Rate limit chan tan suat qua cao, con debounce gom nhieu update gan nhau thanh mot lan reload. `DebouncedFeatureFlagReloader` hoat dong nhu sau:

- `submit` luu config moi nhat va dat lai thoi diem due.
- `flushIfDue` tra waiting neu chua qua quiet period.
- Neu da due, chi reload config moi nhat dang pending.
- Neu khong co pending config, result la idle.
- Delegate ben trong van co rate limit, fingerprint, validation va safe reload.

Pattern nay huu ich khi file watcher, webhook, hoac admin UI co the ban nhieu update gan nhau. Nguyen tac chinh: debounce giam so lan xu ly bang cach doi he thong yen lang mot khoang ngan truoc khi chay.

## Bai instrumented debounced feature flag reload nen doc ky

Sau khi co debounce, rate limit, fingerprint va safe reload, ban can do ket qua de biet toi uu co tac dung khong. `InstrumentedDebouncedFeatureFlagReloader` boc delegate va ghi metrics:

- So lan submit config.
- So lan flush idle hoac waiting.
- So attempt da flushed.
- So lan bi rate limit block.
- So lan fingerprint skip vi config khong doi.
- So safe reload applied hoac rejected.

Pattern nay huu ich cho monitoring, dashboard, alert, va capacity tuning. Nguyen tac chinh: toi uu phai do duoc, neu khong ban chi dang doan.

## Bai feature flag reload health analyzer nen doc ky

Metrics raw chi la so dem. De van hanh duoc, can bien no thanh health status va warning ro rang. `FeatureFlagReloadHealthAnalyzer` doc `FeatureFlagReloadMetricsSnapshot`:

- Tinh block rate tren so flushed attempts.
- Tinh rejection rate tren so config da thay doi.
- Tinh fingerprint skip rate de biet config source co nhieu lan khong doi khong.
- Tra `HEALTHY`, `WARNING`, hoac `CRITICAL`.
- Tra danh sach warning de operator biet can xu ly gi.

Pattern nay huu ich cho dashboard, alerting, runbook, va capacity tuning. Nguyen tac chinh: dung ti le de so sanh cong bang giua he thong it traffic va nhieu traffic.

## Bai feature flag reload alert policy nen doc ky

Health report cho biet trang thai, nhung he thong van hanh can quyet dinh co gui alert hay khong. `FeatureFlagReloadAlertPolicy` xu ly quy tac nay:

- `HEALTHY` khong tao alert.
- `WARNING` co the bat hoac tat alert tuy policy.
- `CRITICAL` luon tao active alert.
- Alert giu severity, message ngan va details tu health report.

Pattern nay huu ich cho alert routing, dashboard badge, runbook trigger, va on-call workflow. Nguyen tac chinh: tach health analysis khoi alert policy de doi nguong va cach gui alert ma khong doi logic tinh health.

## Bai feature flag reload alert suppressor nen doc ky

Alert policy quyet dinh co can canh bao hay khong, nhung neu cung mot loi lap lai lien tuc thi on-call se bi noise. `FeatureFlagReloadAlertSuppressor` them cooldown:

- Alert inactive luon bi suppress.
- Alert active dau tien duoc emitted.
- Alert trung noi dung trong cooldown bi suppress.
- Het cooldown thi cung alert do duoc emitted lai.
- Alert khac details duoc xem la alert khac.

Pattern nay huu ich cho alert routing, webhook notification, Slack/email alert, va runbook automation. Nguyen tac chinh: suppress duplicate theo fingerprint noi dung alert, khong suppress tat ca alert chung mot cach mu quang.

## Bai feature flag reload alert router nen doc ky

Sau khi alert da qua suppression, he thong can quyet dinh gui di dau. `FeatureFlagReloadAlertRouter` tach quy tac routing:

- Alert suppressed di vao channel `NONE`.
- Alert warning di vao `DASHBOARD`.
- Alert critical di vao `ON_CALL`.
- Route giu lai decision goc va summary ngan gon.

Pattern nay huu ich khi cung mot alert co the can dashboard badge, Slack channel, PagerDuty, email, hoac log-only. Nguyen tac chinh: policy quyet dinh co alert, suppressor quyet dinh co gui bay gio, router quyet dinh gui den dau.

## Bai feature flag reload alert dispatcher nen doc ky

Router chi quyet dinh channel, con dispatcher moi lam side effect gui alert. `FeatureFlagReloadAlertDispatcher` tach phan delivery:

- Route `NONE` duoc skip, khong ghi vao sink.
- Route `DASHBOARD` va `ON_CALL` duoc chuyen thanh delivery payload.
- Sink interface giup thay in-memory bang Slack, email, PagerDuty hoac log.
- Dispatch result cho biet delivered hay skipped.

Pattern nay huu ich khi can test logic delivery ma khong goi service that. Nguyen tac chinh: tach route decision khoi side effect de code de test va de thay doi kenh gui.

## Bai feature flag reload alert retry policy nen doc ky

Alert delivery co the fail tam thoi vi Slack, email, webhook hoac pager service loi. `FeatureFlagReloadAlertRetryPolicy` chi lap ke hoach retry:

- Neu chua het `maxAttempts`, tra `RETRY_LATER`.
- Moi lan retry co `nextAttemptAtMillis`.
- Delay co the exponential backoff bang multiplier.
- Het attempt thi tra `GIVE_UP`.
- Policy khong tu deliver, chi tinh ke hoach de dispatcher/worker dung.

Pattern nay huu ich cho side effect reliability. Nguyen tac chinh: retry phai co gioi han, neu khong he thong canh bao co the tu tao them tai khi dependency dang loi.

## Bai feature flag reload alert dead-letter store nen doc ky

Khi retry da het ma alert van khong gui duoc, ban khong nen lam mat payload. `FeatureFlagReloadAlertDeadLetterStore` giu lai delivery da give up:

- Chi record khi retry plan la `GIVE_UP`.
- Luu delivery, failed attempt, failed time va reason.
- Store co capacity co dinh.
- Vuot capacity thi drop record cu nhat va tang `droppedCount`.
- `findAll` tra immutable list de caller khong sua state noi bo.

Pattern nay huu ich cho replay thu cong, audit loi delivery, va on-call debugging. Nguyen tac chinh: dead-letter store phai bounded de loi delivery khong lam day memory.

## Bai feature flag reload alert dead-letter replay nen doc ky

Dead-letter chi huu ich khi co cach doc va replay lai payload da loi. `FeatureFlagReloadAlertDeadLetterReplayer` lay delivery tu store va gui lai qua dispatcher:

- Replay co `limit` de tranh gui qua nhieu alert cu trong mot lan chay.
- Tao lai route tu delivery da luu, giu nguyen channel, severity, message va details.
- Dispatch qua sink binh thuong de dung chung pipeline delivery hien co.
- Ket qua gom attempted, delivered, skipped va tung dispatch result.
- Replay khong xoa record; production co the can them ack/remove rieng sau khi gui thanh cong.

Pattern nay huu ich cho van hanh sau su co delivery. Nguyen tac chinh: replay phai bounded va observable, khong nen doc het dead-letter store roi gui hang loat khong kiem soat.

## Bai feature flag reload alert replay cleanup nen doc ky

Replay thanh cong nhung khong cleanup se lam cung mot dead-letter bi gui lai nhieu lan. `FeatureFlagReloadAlertDeadLetterReplayCoordinator` gom hai buoc replay va acknowledge:

- Lay mot batch co gioi han tu dead-letter store.
- Dispatch tung record qua route duoc tao lai tu delivery cu.
- Chi goi `remove` khi dispatch result la delivered.
- Record bi skipped hoac replay loi logic thi van nam trong store de xu ly sau.
- Ket qua cleanup noi ro attempted, delivered, skipped, removed va remaining.

Pattern nay huu ich khi van hanh can retry thu cong nhung khong muon mat payload chua gui duoc. Nguyen tac chinh: xoa dead-letter sau side effect thanh cong, khong xoa truoc.

## Bai feature flag reload alert dead-letter monitor nen doc ky

Khi dead-letter store bat dau day, he thong can biet som truoc khi payload loi bi drop. `FeatureFlagReloadAlertDeadLetterMonitor` bien size, capacity va dropped count thanh health report:

- `utilization` = backlog size / capacity.
- Vuot warning threshold thi status la `WARNING`.
- Vuot critical threshold hoac co dropped records qua gioi han thi status la `CRITICAL`.
- Report giu backlog size, capacity, utilization, dropped count va warnings.
- Store co `capacity()` de monitor doc gioi han cong khai thay vi dung state noi bo.

Pattern nay huu ich cho dashboard va alerting. Nguyen tac chinh: dead-letter khong chi can replay, no con can duoc do luong de tranh mat du lieu im lang.

## Bai feature flag reload alert dead-letter alert policy nen doc ky

Monitor chi tao health report, con policy moi quyet dinh co can bao dong hay khong. `FeatureFlagReloadAlertDeadLetterAlertPolicy` bien report thanh `FeatureFlagReloadAlert`:

- Healthy report tra ve inactive alert.
- Warning co the bat/tat bang `alertOnWarning`.
- Critical luon tao active alert.
- Details gom warnings, backlog size/capacity va dropped count.
- Message rieng giup phan biet loi dead-letter backlog voi loi reload workflow chung.

Pattern nay huu ich khi muon dua dead-letter backlog vao cung pipeline suppress, route va dispatch alert. Nguyen tac chinh: health analysis va alert policy nen tach rieng de de test va de chinh nguong van hanh.

## Bai feature flag reload alert dead-letter alert workflow nen doc ky

Sau khi co monitor va policy, workflow ghep cac buoc nho thanh mot luong van hanh hoan chinh. `FeatureFlagReloadAlertDeadLetterAlertWorkflow` thuc hien:

- Analyze dead-letter store de tao health report.
- Evaluate policy de tao alert active/inactive.
- Chay suppressor de chan alert trung lap trong cooldown.
- Route alert den dashboard/on-call/none.
- Dispatch route vao sink va tra ve result day du.

Pattern nay huu ich khi code production can orchestration ro rang nhung khong muon logic dieu kien nam het trong mot method lon. Nguyen tac chinh: workflow nen mong, con quyet dinh nen nam trong cac dependency da test rieng.

## Bai instrumented feature flag reload alert dead-letter workflow nen doc ky

Workflow da chay duoc thi buoc tiep theo la do luong ket qua. `InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow` boc workflow goc va ghi metrics sau moi lan run:

- Dem tong so lan workflow chay.
- Dem health report theo `HEALTHY`, `WARNING`, `CRITICAL`.
- Dem active alerts va suppressed alerts.
- Dem delivered alerts va skipped dispatches.
- Snapshot bat bien giup doc counters ma khong sua state noi bo.

Pattern nay huu ich cho dashboard va regression check. Nguyen tac chinh: instrumentation chi quan sat ket qua, khong thay doi quyet dinh cua workflow.

## Bai feature flag reload alert dead-letter workflow health analyzer nen doc ky

Metrics chi la counter; analyzer bien counter thanh trang thai van hanh. `FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer` doc snapshot va tinh:

- `criticalRate`: so critical reports / tong so workflow runs.
- `suppressionRate`: so suppressed alerts / active alerts.
- `deliveryRate`: so delivered alerts / active alerts.
- Vuot nguong warning/critical thi tao warnings ro rang.
- Co critical alerts nhung delivered bang 0 thi luon la `CRITICAL`.

Pattern nay huu ich khi dashboard can status ngan gon thay vi nhin nhieu counter rieng le. Nguyen tac chinh: dung ty le de danh gia xu huong, va danh dau critical khi alert nghiem trong khong den duoc noi nhan.

## Bai feature flag reload alert dead-letter workflow alert policy nen doc ky

Health analyzer chi noi workflow dang khoe hay dang loi; policy bien ket qua do thanh alert payload. `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy` thuc hien:

- Healthy report tra inactive alert.
- Warning co the bat/tat bang `alertOnWarning`.
- Critical luon tao active alert.
- Details gom warnings va cac rate quan trong: critical, suppression, delivery.
- Message rieng giup phan biet loi workflow alert voi loi backlog dead-letter.

Pattern nay huu ich de dua chinh workflow canh bao vao cung pipeline alert chung. Nguyen tac chinh: canh bao ve he thong canh bao cung can ro rang, de tranh truong hop alert pipeline hong ma khong ai biet.

## Bai feature flag reload alert dead-letter workflow alert pipeline nen doc ky

Policy tao alert, nhung pipeline moi chay du luong gui alert ra ngoai. `FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline` ghep cac thanh phan:

- Analyze metrics snapshot thanh workflow health report.
- Evaluate policy thanh alert active/inactive.
- Suppress duplicate alert trong cooldown.
- Route critical den on-call, warning den dashboard, inactive den none.
- Dispatch route va tra ve result day du de debug.

Pattern nay huu ich cho meta-monitoring: canh bao khi chinh he thong canh bao co dau hieu bat thuong. Nguyen tac chinh: alert pipeline cung la mot subsystem can duoc quan sat, suppress va dispatch nhu cac luong van hanh khac.

## Bai feature flag reload alert dead-letter workflow incident log nen doc ky

Khi pipeline meta-alert phat hien bat thuong, ta can mot lich su ngan de debug sau nay. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog` luu incident tu alert result:

- Chi record alert active, bo qua healthy/inactive result.
- Luu status, channel, delivered flag, message va warnings.
- Log co capacity co dinh.
- Vuot capacity thi drop incident cu nhat va tang `droppedCount`.
- `findAll` tra immutable list de caller khong sua state noi bo.

Pattern nay huu ich cho audit va on-call debugging. Nguyen tac chinh: incident history phai bounded, vi he thong dang loi co the tao rat nhieu alert trong thoi gian ngan.

## Bai feature flag reload alert dead-letter workflow incident log monitor nen doc ky

Incident log giup audit, nhung ban van can biet log co dang gan day hay mat lich su khong. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor` doc incident log va tao health report:

- `utilization` = incident count / capacity.
- Vuot warning threshold thi status la `WARNING`.
- Vuot critical threshold thi status la `CRITICAL`.
- Qua nhieu incident undelivered thi status la `CRITICAL`.
- Co dropped incident thi status la `CRITICAL` neu vuot nguong.

Pattern nay huu ich cho meta-monitoring sau cung: khong chi can ghi incident, ma can biet incident log co dang bi qua tai hay mat du lieu. Nguyen tac chinh: audit store cung can health check rieng.

## Bai feature flag reload alert dead-letter workflow incident log alert policy nen doc ky

Monitor incident log tao health report, con alert policy quyet dinh co can bao dong hay khong. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy` thuc hien:

- Healthy report tra inactive alert.
- Warning co the bat/tat bang `alertOnWarning`.
- Critical luon tao active alert.
- Details gom utilization, retained count, undelivered count va dropped count.
- Message rieng giup phan biet loi incident log voi loi workflow alert khac.

Pattern nay huu ich khi audit log cung tro thanh mot nguon rui ro van hanh. Nguyen tac chinh: khi log bat dau drop history, do la tin hieu nghiem trong vi ban dang mat ngu canh debug.

## Bai feature flag reload alert dead-letter workflow incident log alert pipeline nen doc ky

Policy tao alert payload, con pipeline moi chay du luong gui alert ra ngoai. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline` gom cac buoc:

- Monitor incident log de tao health report.
- Evaluate policy de tao alert active/inactive.
- Suppress duplicate alert trong cooldown.
- Route critical den on-call, warning den dashboard, inactive den none.
- Dispatch route va tra ve result day du de debug.

Pattern nay huu ich khi audit-log health cung can duoc bao dong nhu cac thanh phan van hanh khac. Nguyen tac chinh: mot pipeline alert chuan nen duoc tai su dung thay vi moi noi tu gui alert theo cach rieng.

## Bai feature flag reload alert dead-letter workflow incident log summary nen doc ky

Incident log giu raw audit events, con summary tao so lieu de doc tren dashboard. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer` tao cac counter:

- Tong incident dang duoc giu lai.
- So incident critical va warning.
- So incident delivered va undelivered.
- So incident theo channel: on-call, dashboard, suppressed.
- So incident da bi drop va delivery rate.

Pattern nay huu ich khi can hien thi nhanh tinh trang van hanh ma khong sua audit log. Nguyen tac chinh: dashboard nen doc derived summary, con audit log van la source of truth.

## Bai feature flag reload alert dead-letter workflow incident triage planner nen doc ky

Summary noi he thong dang co bao nhieu incident; triage planner bien cac so lieu do thanh viec can lam. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner` tao action theo uu tien:

- Drop history thi uu tien cao nhat vi dang mat ngu canh debug.
- Incident undelivered thi can kiem tra suppression va delivery sink.
- Critical incidents con ton tai thi can review workflow health.
- Plan rong nghia la summary khong tao viec van hanh moi.
- Action co priority, severity, title va detail de de render len UI/CLI.

Pattern nay huu ich khi dashboard khong chi hien thi so lieu ma con goi y buoc xu ly tiep theo. Nguyen tac chinh: tinh summary va dien giai summary nen tach rieng de code de test va de thay doi quy tac triage.

## Bai feature flag reload alert dead-letter workflow incident triage formatter nen doc ky

Planner tao action plan, formatter bien plan thanh text on dinh de hien thi. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter` lam cac viec sau:

- Plan rong tra ve thong bao khong co action.
- Plan co action thi in header kem highest severity.
- Moi action hien priority, severity, title va detail.
- Output co thu tu on dinh de de test snapshot/log.
- Formatter khong tinh lai rule triage.

Pattern nay huu ich khi cung mot triage plan can hien thi tren console, log, dashboard hoac test. Nguyen tac chinh: presentation layer chi render du lieu da duoc quyet dinh, khong tron them business rule.

## Bai feature flag reload alert dead-letter workflow incident triage digest nen doc ky

Sau khi co summary, plan va formatted text, digest gom cac view da tinh vao mot object de export/log/dashboard dung lai. `FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder` lam cac viec sau:

- Doc incident log bang summarizer, khong sua log goc.
- Tao triage plan tu summary de giu rule uu tien o mot noi.
- Format plan thanh text on dinh de operator doc nhanh.
- Gan timestamp tu `TimeSource` de test deterministic bang `ManualTimeSource`.
- Tra ve digest gom summary, plan, formatted text va severity helper.

Pattern nay huu ich khi mot dashboard hoac export job can du ngu canh ma khong muon tinh lai nhieu lan. Nguyen tac chinh: export object nen gom du ngu canh doc nhanh, nhung incident log van la source of truth.
