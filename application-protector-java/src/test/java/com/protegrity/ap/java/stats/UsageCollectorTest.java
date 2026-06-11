package com.protegrity.ap.java.stats;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Unit tests for UsageCollector (PTY-151136).
 */
public class UsageCollectorTest {

    // ──────────────────────────────────────────────────────────
    // Opt-in/opt-out
    // ──────────────────────────────────────────────────────────

    @Test
    public void testEnabledInDeveloperEditionMode() {
        UsageCollector collector = new UsageCollector(true);
        assertTrue(collector.isEnabled());
    }

    @Test
    public void testDisabledWhenNotDeveloperEdition() {
        // Note: PTY_STATS env var is not set in test env
        UsageCollector collector = new UsageCollector(false);
        assertFalse(collector.isEnabled());
    }

    // ──────────────────────────────────────────────────────────
    // Accumulation
    // ──────────────────────────────────────────────────────────

    @Test
    public void testRecordProtect() {
        UsageCollector collector = new UsageCollector(true);
        collector.recordProtect("SSN");

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> elements =
            (Map<String, Map<String, Object>>) data.get("data_elements");

        assertNotNull(elements.get("SSN"));
        assertEquals(1, elements.get("SSN").get("protect_count"));
    }

    @Test
    public void testRecordUnprotect() {
        UsageCollector collector = new UsageCollector(true);
        collector.recordUnprotect("CC");

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> elements =
            (Map<String, Map<String, Object>>) data.get("data_elements");

        assertEquals(1, elements.get("CC").get("unprotect_count"));
    }

    @Test
    public void testRecordReprotect() {
        UsageCollector collector = new UsageCollector(true);
        collector.recordReprotect("SSN_V1", "SSN_V2");

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> elements =
            (Map<String, Map<String, Object>>) data.get("data_elements");

        assertEquals(1, elements.get("SSN_V1").get("reprotect_source_count"));
        assertEquals(1, elements.get("SSN_V2").get("reprotect_target_count"));
    }

    @Test
    public void testMultipleOperationsAccumulate() {
        UsageCollector collector = new UsageCollector(true);
        collector.recordProtect("SSN");
        collector.recordProtect("SSN");
        collector.recordProtect("SSN");
        collector.recordUnprotect("SSN");

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> elements =
            (Map<String, Map<String, Object>>) data.get("data_elements");

        assertEquals(3, elements.get("SSN").get("protect_count"));
        assertEquals(1, elements.get("SSN").get("unprotect_count"));
    }

    @Test
    public void testDisabledDoesNotRecord() {
        UsageCollector collector = new UsageCollector(false);
        collector.recordProtect("SSN");
        collector.recordUnprotect("CC");

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> elements =
            (Map<String, Map<String, Object>>) data.get("data_elements");

        assertTrue(elements.isEmpty());
    }

    @Test
    public void testRecordSession() {
        UsageCollector collector = new UsageCollector(true);
        collector.recordSession("testuser");

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> users =
            (Map<String, Map<String, Object>>) data.get("policy_users");

        assertNotNull(users.get("testuser"));
        assertEquals(1, users.get("testuser").get("session_count"));
    }

    // ──────────────────────────────────────────────────────────
    // Thread safety
    // ──────────────────────────────────────────────────────────

    @Test
    public void testConcurrentProtectOperations() throws InterruptedException {
        UsageCollector collector = new UsageCollector(true);
        int threadCount = 10;
        int operationsPerThread = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    collector.recordProtect("SSN");
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> elements =
            (Map<String, Map<String, Object>>) data.get("data_elements");

        assertEquals(threadCount * operationsPerThread, elements.get("SSN").get("protect_count"));
    }

    @Test
    public void testConcurrentMultipleDataElements() throws InterruptedException {
        UsageCollector collector = new UsageCollector(true);
        int threadCount = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String[] dataElements = {"SSN", "CC", "EMAIL", "PHONE", "DOB"};
        for (int i = 0; i < threadCount; i++) {
            final String de = dataElements[i];
            executor.submit(() -> {
                for (int j = 0; j < 50; j++) {
                    collector.recordProtect(de);
                    collector.recordUnprotect(de);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        Map<String, Object> data = collector.getSessionData();
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> elements =
            (Map<String, Map<String, Object>>) data.get("data_elements");

        for (String de : dataElements) {
            assertEquals(50, elements.get(de).get("protect_count"));
            assertEquals(50, elements.get(de).get("unprotect_count"));
        }
    }

    // ──────────────────────────────────────────────────────────
    // Session data structure
    // ──────────────────────────────────────────────────────────

    @Test
    public void testGetSessionDataStructure() {
        UsageCollector collector = new UsageCollector(true);
        collector.recordProtect("SSN");

        Map<String, Object> data = collector.getSessionData();
        assertNotNull(data.get("data_elements"));
        assertNotNull(data.get("policy_users"));
    }
}
