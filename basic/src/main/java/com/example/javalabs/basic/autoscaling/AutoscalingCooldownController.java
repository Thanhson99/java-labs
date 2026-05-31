package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.TimeSource;

/**
 * Applies cooldown protection to autoscaling decisions.
 *
 * <p>Autoscaling policies can flap when metrics hover near thresholds. This controller allows a
 * scaling action, starts a cooldown window, and suppresses additional scale-in or scale-out actions
 * until the cooldown expires. {@link ScalingAction#HOLD} decisions pass through without starting a
 * cooldown.</p>
 */
public final class AutoscalingCooldownController {

    private final long cooldownMillis;
    private final TimeSource timeSource;
    private long cooldownUntilMillis;

    /**
     * Creates a cooldown controller.
     *
     * @param cooldownMillis time to wait after a scaling action before allowing another action
     * @param timeSource clock used to evaluate cooldown state
     * @throws IllegalArgumentException when cooldown or clock is invalid
     */
    public AutoscalingCooldownController(long cooldownMillis, TimeSource timeSource) {
        if (cooldownMillis <= 0) {
            throw new IllegalArgumentException("cooldownMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.cooldownMillis = cooldownMillis;
        this.timeSource = timeSource;
    }

    /**
     * Applies cooldown rules to a raw autoscaling decision.
     *
     * @param decision decision produced by an autoscaling policy
     * @param currentInstances current running instance count
     * @return original decision when allowed, or a hold decision while cooldown is active
     * @throws IllegalArgumentException when decision or instance count is invalid
     */
    public AutoscalingDecision apply(AutoscalingDecision decision, int currentInstances) {
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
        if (currentInstances <= 0) {
            throw new IllegalArgumentException("currentInstances must be positive");
        }
        if (decision.action() == ScalingAction.HOLD) {
            return decision;
        }

        long now = timeSource.currentTimeMillis();
        if (now < cooldownUntilMillis) {
            return new AutoscalingDecision(
                    ScalingAction.HOLD,
                    currentInstances,
                    "scaling cooldown active until " + cooldownUntilMillis
            );
        }

        cooldownUntilMillis = now + cooldownMillis;
        return decision;
    }

    /**
     * Returns whether a non-hold scaling decision would currently be suppressed.
     *
     * @return true when cooldown has not expired
     */
    public boolean coolingDown() {
        return timeSource.currentTimeMillis() < cooldownUntilMillis;
    }

    /**
     * Returns the timestamp at which cooldown ends.
     *
     * @return cooldown end timestamp, or {@code 0} before the first scaling action
     */
    public long cooldownUntilMillis() {
        return cooldownUntilMillis;
    }

    /**
     * Clears cooldown state, allowing the next scaling decision immediately.
     */
    public void reset() {
        cooldownUntilMillis = 0;
    }
}
