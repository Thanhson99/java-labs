package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.TimeSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Coalesces rapid feature flag config updates and reloads only after a quiet period.
 */
public final class DebouncedFeatureFlagReloader {

    private final long quietPeriodMillis;
    private final TimeSource timeSource;
    private final RateLimitedFeatureFlagReloader delegate;

    private List<FeatureFlagRule> pendingRules;
    private long dueAtMillis;

    /**
     * Creates a debounced reload workflow.
     *
     * @param quietPeriodMillis delay after the latest submit before flush can run
     * @param timeSource clock used to decide when reload is due
     * @param delegate rate-limited reload workflow
     * @throws IllegalArgumentException when inputs are invalid
     */
    public DebouncedFeatureFlagReloader(
            long quietPeriodMillis,
            TimeSource timeSource,
            RateLimitedFeatureFlagReloader delegate) {
        if (quietPeriodMillis <= 0) {
            throw new IllegalArgumentException("quietPeriodMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        this.quietPeriodMillis = quietPeriodMillis;
        this.timeSource = timeSource;
        this.delegate = delegate;
    }

    /**
     * Submits a desired config and replaces any pending config.
     *
     * @param newRules proposed complete rule set
     * @return timestamp when this config becomes eligible for flush
     * @throws IllegalArgumentException when {@code newRules} is invalid
     */
    public long submit(List<FeatureFlagRule> newRules) {
        pendingRules = copyRules(newRules);
        dueAtMillis = timeSource.currentTimeMillis() + quietPeriodMillis;
        return dueAtMillis;
    }

    /**
     * Flushes pending config when the quiet period has elapsed.
     *
     * @return idle, waiting, or flushed result
     */
    public DebouncedFeatureFlagReloadResult flushIfDue() {
        long now = timeSource.currentTimeMillis();
        if (pendingRules == null) {
            return DebouncedFeatureFlagReloadResult.idle(now);
        }
        if (now < dueAtMillis) {
            return DebouncedFeatureFlagReloadResult.waiting(dueAtMillis);
        }

        List<FeatureFlagRule> rulesToReload = pendingRules;
        long flushedDueAt = dueAtMillis;
        // Clear pending state before delegate call so a successful flush cannot be repeated.
        pendingRules = null;
        dueAtMillis = 0;
        return DebouncedFeatureFlagReloadResult.flushed(
                flushedDueAt,
                delegate.reloadIfAllowed(rulesToReload)
        );
    }

    /**
     * @return {@code true} when a submitted config is waiting for quiet-period flush
     */
    public boolean hasPendingReload() {
        return pendingRules != null;
    }

    /**
     * Defensively copies submitted rules so callers cannot mutate pending config.
     *
     * @param rules submitted rule list
     * @return immutable rule copy
     * @throws IllegalArgumentException when {@code rules} is {@code null} or contains {@code null}
     */
    private static List<FeatureFlagRule> copyRules(List<FeatureFlagRule> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        List<FeatureFlagRule> copy = new ArrayList<>(rules.size());
        for (FeatureFlagRule rule : rules) {
            if (rule == null) {
                throw new IllegalArgumentException("rules must not contain null");
            }
            copy.add(rule);
        }
        return List.copyOf(copy);
    }
}

