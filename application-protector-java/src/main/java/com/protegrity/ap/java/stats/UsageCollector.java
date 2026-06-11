package com.protegrity.ap.java.stats;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe in-memory usage statistics collector.
 *
 * <p>Accumulates operation counts per data element and policy user during the JVM lifetime.
 * Stats are flushed to disk via JVM shutdown hook.
 *
 * <p>Stats are only collected when running in Developer Edition mode (DEV_EDITION_* vars present).
 *
 * @since 1.1.0
 */
public class UsageCollector {

    private final boolean enabled;
    private final ConcurrentHashMap<String, DataElementStats> dataElements = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PolicyUserStats> policyUsers = new ConcurrentHashMap<>();

    public UsageCollector(boolean developerEditionMode) {
        String explicit = System.getenv("PTY_STATS");
        if (explicit != null && !explicit.isEmpty()) {
            String val = explicit.toLowerCase();
            this.enabled = !val.equals("false") && !val.equals("0")
                && !val.equals("no") && !val.equals("off");
        } else {
            // Default: enabled only in DE mode
            this.enabled = developerEditionMode;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Record a session creation for a policy user.
     */
    public void recordSession(String user) {
        if (!enabled) return;
        policyUsers.computeIfAbsent(user, k -> new PolicyUserStats()).recordSession();
    }

    /**
     * Record a protect operation for a data element.
     */
    public void recordProtect(String dataElement) {
        if (!enabled) return;
        dataElements.computeIfAbsent(dataElement, k -> new DataElementStats()).recordProtect();
    }

    /**
     * Record an unprotect operation for a data element.
     */
    public void recordUnprotect(String dataElement) {
        if (!enabled) return;
        dataElements.computeIfAbsent(dataElement, k -> new DataElementStats()).recordUnprotect();
    }

    /**
     * Record a reprotect operation (both source and target).
     */
    public void recordReprotect(String sourceDataElement, String targetDataElement) {
        if (!enabled) return;
        dataElements.computeIfAbsent(sourceDataElement, k -> new DataElementStats()).recordReprotectSource();
        dataElements.computeIfAbsent(targetDataElement, k -> new DataElementStats()).recordReprotectTarget();
    }

    /**
     * Get session data as a map structure suitable for JSON serialization.
     */
    public Map<String, Object> getSessionData() {
        Map<String, Object> result = new ConcurrentHashMap<>();

        Map<String, Map<String, Object>> deMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, DataElementStats> entry : dataElements.entrySet()) {
            deMap.put(entry.getKey(), entry.getValue().toMap());
        }
        result.put("data_elements", deMap);

        Map<String, Map<String, Object>> userMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, PolicyUserStats> entry : policyUsers.entrySet()) {
            userMap.put(entry.getKey(), entry.getValue().toMap());
        }
        result.put("policy_users", userMap);

        return result;
    }

    /**
     * Per-data-element statistics accumulator.
     */
    static class DataElementStats {
        private final AtomicInteger protectCount = new AtomicInteger(0);
        private final AtomicInteger unprotectCount = new AtomicInteger(0);
        private final AtomicInteger reprotectSourceCount = new AtomicInteger(0);
        private final AtomicInteger reprotectTargetCount = new AtomicInteger(0);
        private volatile String firstUsed = LocalDate.now().toString();
        private volatile String lastUsed = LocalDate.now().toString();

        void recordProtect() {
            protectCount.incrementAndGet();
            lastUsed = LocalDate.now().toString();
        }

        void recordUnprotect() {
            unprotectCount.incrementAndGet();
            lastUsed = LocalDate.now().toString();
        }

        void recordReprotectSource() {
            reprotectSourceCount.incrementAndGet();
            lastUsed = LocalDate.now().toString();
        }

        void recordReprotectTarget() {
            reprotectTargetCount.incrementAndGet();
            lastUsed = LocalDate.now().toString();
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new ConcurrentHashMap<>();
            map.put("protect_count", protectCount.get());
            map.put("unprotect_count", unprotectCount.get());
            map.put("reprotect_source_count", reprotectSourceCount.get());
            map.put("reprotect_target_count", reprotectTargetCount.get());
            map.put("first_used", firstUsed);
            map.put("last_used", lastUsed);
            return map;
        }
    }

    /**
     * Per-policy-user statistics accumulator.
     */
    static class PolicyUserStats {
        private final AtomicInteger sessionCount = new AtomicInteger(0);
        private volatile String firstUsed = LocalDate.now().toString();
        private volatile String lastUsed = LocalDate.now().toString();

        void recordSession() {
            sessionCount.incrementAndGet();
            lastUsed = LocalDate.now().toString();
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new ConcurrentHashMap<>();
            map.put("session_count", sessionCount.get());
            map.put("first_used", firstUsed);
            map.put("last_used", lastUsed);
            return map;
        }
    }
}
