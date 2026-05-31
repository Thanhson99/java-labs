package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Merges multiple profile change events for the same user into one compact event.
 *
 * <p>Batch consumers often receive several updates for the same entity. Coalescing keeps the first
 * old value, the last new value, and drops fields that end up with no net change.</p>
 */
public final class ProfileChangeEventCoalescer {

    /**
     * Merges repeated events for the same user into compact events.
     *
     * @param events profile change events to coalesce
     * @return compacted events in first-seen user order
     * @throws IllegalArgumentException when {@code events} is {@code null} or contains {@code null}
     */
    public List<UserProfileChangeEvent> coalesce(List<UserProfileChangeEvent> events) {
        if (events == null) {
            throw new IllegalArgumentException("events must not be null");
        }

        Map<String, Map<String, FieldChange>> changesByUser = new LinkedHashMap<>();
        for (UserProfileChangeEvent event : events) {
            if (event == null) {
                throw new IllegalArgumentException("events must not contain null");
            }

            Map<String, FieldChange> userChanges =
                    changesByUser.computeIfAbsent(event.userId(), ignored -> new LinkedHashMap<>());
            for (FieldChange change : event.changes()) {
                FieldChange existing = userChanges.get(change.fieldName());
                // Keep the first old value and replace the new value with the latest change.
                FieldChange merged = existing == null
                        ? change
                        : new FieldChange(change.fieldName(), existing.oldValue(), change.newValue());
                if (merged.oldValue().equals(merged.newValue())) {
                    // A field that returns to its original value has no net change.
                    userChanges.remove(change.fieldName());
                } else {
                    userChanges.put(change.fieldName(), merged);
                }
            }
        }

        List<UserProfileChangeEvent> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, FieldChange>> entry : changesByUser.entrySet()) {
            List<FieldChange> changes = new ArrayList<>(entry.getValue().values());
            if (!changes.isEmpty()) {
                // Users with no net changes are intentionally omitted from the compacted output.
                result.add(new UserProfileChangeEvent(entry.getKey(), changes));
            }
        }
        return result;
    }
}
