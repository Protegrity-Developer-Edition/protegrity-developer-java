package com.protegrity.devedition.utils;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigTest {

    @Before
    public void setUp() {
        // Reset to defaults before each test
        Config.setEndpointUrl("http://localhost:8580/pty/data-discovery/v1.1/classify");
        Config.setNamedEntityMap(new HashMap<>());
        Config.setMaskingChar("#");
        Config.setClassificationScoreThreshold(0.6);
        Config.setMethod("redact");
        Config.setEnableLogging(true);
        Config.setLogLevel("INFO");
    }

    @Test
    public void testGetDefaultEndpointUrl() {
        String url = Config.getEndpointUrl();
        assertNotNull(url);
        assertTrue(url.contains("data-discovery"));
    }

    @Test
    public void testSetEndpointUrl() {
        String customUrl = "http://custom-endpoint:9090/api/classify";
        Config.setEndpointUrl(customUrl);
        assertEquals(customUrl, Config.getEndpointUrl());
    }

    @Test
    public void testGetDefaultNamedEntityMap() {
        Map<String, String> map = Config.getNamedEntityMap();
        assertNotNull(map);
    }

    @Test
    public void testSetNamedEntityMap() {
        Map<String, String> customMap = new HashMap<>();
        customMap.put("CREDIT_CARD", "ccn");
        customMap.put("EMAIL_ADDRESS", "email");
        
        Config.setNamedEntityMap(customMap);
        
        Map<String, String> retrieved = Config.getNamedEntityMap();
        assertEquals(2, retrieved.size());
        assertEquals("ccn", retrieved.get("CREDIT_CARD"));
        assertEquals("email", retrieved.get("EMAIL_ADDRESS"));
    }

    @Test
    public void testGetDefaultMaskingChar() {
        String maskChar = Config.getMaskingChar();
        assertEquals("#", maskChar);
    }

    @Test
    public void testSetMaskingChar() {
        Config.setMaskingChar("*");
        assertEquals("*", Config.getMaskingChar());
    }

    @Test
    public void testGetDefaultClassificationScoreThreshold() {
        double threshold = Config.getClassificationScoreThreshold();
        assertEquals(0.6, threshold, 0.001);
    }

    @Test
    public void testSetClassificationScoreThreshold() {
        Config.setClassificationScoreThreshold(0.8);
        assertEquals(0.8, Config.getClassificationScoreThreshold(), 0.001);
    }

    @Test
    public void testSetClassificationScoreThresholdBoundaries() {
        Config.setClassificationScoreThreshold(0.0);
        assertEquals(0.0, Config.getClassificationScoreThreshold(), 0.001);
        
        Config.setClassificationScoreThreshold(1.0);
        assertEquals(1.0, Config.getClassificationScoreThreshold(), 0.001);
    }

    @Test
    public void testGetDefaultMethod() {
        String method = Config.getMethod();
        assertEquals("redact", method);
    }

    @Test
    public void testSetMethodRedact() {
        Config.setMethod("redact");
        assertEquals("redact", Config.getMethod());
    }

    @Test
    public void testSetMethodMask() {
        Config.setMethod("mask");
        assertEquals("mask", Config.getMethod());
    }

    @Test
    public void testSetMethodInvalid() {
        Config.setMethod("redact");
        Config.setMethod("invalid_method");
        // Should not change when invalid
        assertEquals("redact", Config.getMethod());
    }

    @Test
    public void testIsEnableLoggingDefault() {
        boolean enabled = Config.isEnableLogging();
        assertTrue(enabled);
    }

    @Test
    public void testSetEnableLogging() {
        Config.setEnableLogging(false);
        assertFalse(Config.isEnableLogging());
        
        Config.setEnableLogging(true);
        assertTrue(Config.isEnableLogging());
    }

    @Test
    public void testGetDefaultLogLevel() {
        String level = Config.getLogLevel();
        assertEquals("INFO", level);
    }

    @Test
    public void testSetLogLevel() {
        Config.setLogLevel("DEBUG");
        assertEquals("DEBUG", Config.getLogLevel());
        
        Config.setLogLevel("ERROR");
        assertEquals("ERROR", Config.getLogLevel());
    }

    @Test
    public void testConfigureWithAllParameters() {
        Map<String, String> entityMap = new HashMap<>();
        entityMap.put("CREDIT_CARD", "ccn");
        
        Config.configure(
            "http://test:8080/api",
            entityMap,
            "*",
            0.75,
            "mask",
            false,
            "WARN"
        );
        
        assertEquals("http://test:8080/api", Config.getEndpointUrl());
        assertEquals(entityMap, Config.getNamedEntityMap());
        assertEquals("*", Config.getMaskingChar());
        assertEquals(0.75, Config.getClassificationScoreThreshold(), 0.001);
        assertEquals("mask", Config.getMethod());
        assertFalse(Config.isEnableLogging());
        assertEquals("WARN", Config.getLogLevel());
    }

    @Test
    public void testConfigureWithNullParameters() {
        String originalUrl = Config.getEndpointUrl();
        
        Config.configure(null, null, null, null, null, null, null);
        
        // Should not change when nulls are passed
        assertEquals(originalUrl, Config.getEndpointUrl());
    }

    @Test
    public void testConfigurePartialParameters() {
        Config.configure(
            "http://partial:8080/api",
            null,
            null,
            0.9,
            null,
            null,
            "ERROR"
        );
        
        assertEquals("http://partial:8080/api", Config.getEndpointUrl());
        assertEquals(0.9, Config.getClassificationScoreThreshold(), 0.001);
        assertEquals("ERROR", Config.getLogLevel());
    }

    @Test
    public void testMaskingCharCanBeMultipleCharacters() {
        Config.setMaskingChar("***");
        assertEquals("***", Config.getMaskingChar());
    }

    @Test
    public void testNamedEntityMapIsModifiable() {
        Map<String, String> map = new HashMap<>();
        map.put("TEST", "test_value");
        Config.setNamedEntityMap(map);
        
        Map<String, String> retrieved = Config.getNamedEntityMap();
        retrieved.put("NEW_KEY", "new_value");
        
        // Should be the same reference
        assertEquals(2, Config.getNamedEntityMap().size());
    }
}
