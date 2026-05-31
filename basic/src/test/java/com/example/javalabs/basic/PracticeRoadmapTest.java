package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PracticeRoadmapTest {

    @Test
    void stagesMoveFromBasicSyntaxToServiceLevelCode() {
        List<PracticeRoadmap.PracticeStage> stages = PracticeRoadmap.stages();

        assertEquals(89, stages.size());
        assertEquals("Java syntax and control flow", stages.get(0).title());
        assertEquals("Feature flag reload alert dead-letter workflow incident triage digest", stages.get(stages.size() - 1).title());
    }

    @Test
    void everyStagePointsToSourceTestsAndExercises() {
        for (PracticeRoadmap.PracticeStage stage : PracticeRoadmap.stages()) {
            assertFalse(stage.sourceFiles().isEmpty(), "source files missing for stage " + stage.order());
            assertFalse(stage.testFiles().isEmpty(), "test files missing for stage " + stage.order());
            assertFalse(stage.exercises().isEmpty(), "exercises missing for stage " + stage.order());
        }
    }

    @Test
    void roadmapCoversFilesBeginnersUsuallyOpenFirst() {
        List<String> allSourceFiles = PracticeRoadmap.stages().stream()
                .flatMap(stage -> stage.sourceFiles().stream())
                .toList();

        assertTrue(allSourceFiles.contains("ControlFlowExamples.java"));
        assertTrue(allSourceFiles.contains("BankAccount.java"));
        assertTrue(allSourceFiles.contains("RegistrationService.java"));
        assertTrue(allSourceFiles.contains("OrderSummaryService.java"));
        assertTrue(allSourceFiles.contains("CachedCustomerDirectory.java"));
        assertTrue(allSourceFiles.contains("ResilientNotificationClient.java"));
        assertTrue(allSourceFiles.contains("CircuitBreakerNotificationClient.java"));
        assertTrue(allSourceFiles.contains("InstrumentedNotificationClient.java"));
        assertTrue(allSourceFiles.contains("OrderExportService.java"));
        assertTrue(allSourceFiles.contains("CursorOrderExportService.java"));
        assertTrue(allSourceFiles.contains("IdempotentNotificationClient.java"));
        assertTrue(allSourceFiles.contains("BulkheadNotificationClient.java"));
        assertTrue(allSourceFiles.contains("TimeoutNotificationClient.java"));
        assertTrue(allSourceFiles.contains("WelcomeNotificationBatcher.java"));
        assertTrue(allSourceFiles.contains("DeadLetteringNotificationClient.java"));
        assertTrue(allSourceFiles.contains("StockReservationService.java"));
        assertTrue(allSourceFiles.contains("OutboxDispatcher.java"));
        assertTrue(allSourceFiles.contains("OutboxRetryPolicy.java"));
        assertTrue(allSourceFiles.contains("CustomerLookupService.java"));
        assertTrue(allSourceFiles.contains("TopInventorySelector.java"));
        assertTrue(allSourceFiles.contains("InventoryStockSummarizer.java"));
        assertTrue(allSourceFiles.contains("IndexedInventoryCatalog.java"));
        assertTrue(allSourceFiles.contains("BatchPartitioner.java"));
        assertTrue(allSourceFiles.contains("SlidingWindowRateLimiter.java"));
        assertTrue(allSourceFiles.contains("TokenBucketRateLimiter.java"));
        assertTrue(allSourceFiles.contains("UserProfileUpdateService.java"));
        assertTrue(allSourceFiles.contains("IncrementalInventorySummary.java"));
        assertTrue(allSourceFiles.contains("CoalescingCustomerDirectory.java"));
        assertTrue(allSourceFiles.contains("NegativeCachingCustomerDirectory.java"));
        assertTrue(allSourceFiles.contains("StaleCustomerCache.java"));
        assertTrue(allSourceFiles.contains("JitteredBackoffPolicy.java"));
        assertTrue(allSourceFiles.contains("RetryBudget.java"));
        assertTrue(allSourceFiles.contains("BoundedDeadLetterStore.java"));
        assertTrue(allSourceFiles.contains("TwoLevelCustomerCache.java"));
        assertTrue(allSourceFiles.contains("CacheWarmupService.java"));
        assertTrue(allSourceFiles.contains("UserProfileDiffService.java"));
        assertTrue(allSourceFiles.contains("SelectiveProfileChangePublisher.java"));
        assertTrue(allSourceFiles.contains("ProfileChangeEventCoalescer.java"));
        assertTrue(allSourceFiles.contains("PriorityJobQueue.java"));
        assertTrue(allSourceFiles.contains("AgingPriorityJobQueue.java"));
        assertTrue(allSourceFiles.contains("DeadlineJobQueue.java"));
        assertTrue(allSourceFiles.contains("SlaBudgetTracker.java"));
        assertTrue(allSourceFiles.contains("AdaptiveRetryController.java"));
        assertTrue(allSourceFiles.contains("LoadSheddingController.java"));
        assertTrue(allSourceFiles.contains("GracefulDegradationController.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagEvaluator.java"));
        assertTrue(allSourceFiles.contains("featureflag/AdaptiveFeatureFlagController.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagRegistry.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloader.java"));
        assertTrue(allSourceFiles.contains("featureflag/AuditedFeatureFlagReloader.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagRollbackPlanner.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagSnapshotStore.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagSnapshotRetentionPolicy.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadValidator.java"));
        assertTrue(allSourceFiles.contains("featureflag/SafeFeatureFlagReloader.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagConfigFingerprinter.java"));
        assertTrue(allSourceFiles.contains("featureflag/FingerprintingFeatureFlagReloader.java"));
        assertTrue(allSourceFiles.contains("featureflag/RateLimitedFeatureFlagReloader.java"));
        assertTrue(allSourceFiles.contains("featureflag/DebouncedFeatureFlagReloader.java"));
        assertTrue(allSourceFiles.contains("featureflag/InstrumentedDebouncedFeatureFlagReloader.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadHealthAnalyzer.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertPolicy.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertSuppressor.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertRouter.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDispatcher.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertRetryPolicy.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterStore.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterReplayer.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterReplayCoordinator.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterMonitor.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertPolicy.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflow.java"));
        assertTrue(allSourceFiles.contains("featureflag/InstrumentedFeatureFlagReloadAlertDeadLetterAlertWorkflow.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter.java"));
        assertTrue(allSourceFiles.contains("featureflag/FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder.java"));
    }
}

