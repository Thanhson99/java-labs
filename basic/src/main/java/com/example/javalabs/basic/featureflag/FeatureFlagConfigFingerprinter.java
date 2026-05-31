package com.example.javalabs.basic.featureflag;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;

/**
 * Builds a deterministic fingerprint for feature flag rules.
 *
 * <p>The rules are sorted before hashing, so the same config produces the same digest even when
 * the source file or database returns rows in a different order.</p>
 */
public final class FeatureFlagConfigFingerprinter {

    private static final String ALGORITHM = "SHA-256";
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    /**
     * Calculates a deterministic fingerprint for a rule list.
     *
     * @param rules rules to hash
     * @return config fingerprint
     * @throws IllegalArgumentException when {@code rules} is {@code null} or contains {@code null}
     */
    public FeatureFlagConfigFingerprint fingerprint(List<FeatureFlagRule> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        for (FeatureFlagRule rule : rules) {
            if (rule == null) {
                throw new IllegalArgumentException("rules must not contain null");
            }
        }

        List<FeatureFlagRule> sortedRules = rules.stream()
                .sorted(Comparator.comparing(FeatureFlagRule::flagName)
                        .thenComparing(FeatureFlagRule::enabled)
                        .thenComparingInt(FeatureFlagRule::rolloutPercentage))
                .toList();

        MessageDigest digest = newDigest();
        for (FeatureFlagRule rule : sortedRules) {
            // Null separators make the hash unambiguous between adjacent fields.
            digest.update(rule.flagName().getBytes(StandardCharsets.UTF_8));
            digest.update((byte) 0);
            digest.update(Boolean.toString(rule.enabled()).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) 0);
            digest.update(Integer.toString(rule.rolloutPercentage()).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) '\n');
        }

        return new FeatureFlagConfigFingerprint(ALGORITHM, toHex(digest.digest()), sortedRules.size());
    }

    /**
     * Calculates a fingerprint from the registry's current snapshot.
     *
     * @param registry live feature flag registry
     * @return config fingerprint
     * @throws IllegalArgumentException when {@code registry} is {@code null}
     */
    public FeatureFlagConfigFingerprint fingerprint(FeatureFlagRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        return fingerprint(List.copyOf(registry.snapshot().values()));
    }

    /**
     * Creates a message-digest instance for the configured algorithm.
     *
     * @return message digest instance
     * @throws IllegalStateException when the JVM does not provide the algorithm
     */
    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(ALGORITHM + " is not available", exception);
        }
    }

    /**
     * Encodes digest bytes as lowercase hexadecimal text.
     *
     * @param bytes digest bytes
     * @return hex string
     */
    private static String toHex(byte[] bytes) {
        char[] output = new char[bytes.length * 2];
        for (int index = 0; index < bytes.length; index++) {
            int value = bytes[index] & 0xff;
            output[index * 2] = HEX[value >>> 4];
            output[index * 2 + 1] = HEX[value & 0x0f];
        }
        return new String(output);
    }
}
