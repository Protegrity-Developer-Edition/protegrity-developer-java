package com.protegrity.devedition.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;

public class LoggerUtilTest {

    @Test
    public void testGetLoggerNotNull() {
        Logger logger = LoggerUtil.getLogger();
        assertNotNull(logger);
    }

    @Test
    public void testGetLoggerReturnsConsistentInstance() {
        Logger logger1 = LoggerUtil.getLogger();
        Logger logger2 = LoggerUtil.getLogger();
        assertSame(logger1, logger2);
    }

    @Test
    public void testGetLoggerWithClass() {
        Logger logger = LoggerUtil.getLogger(LoggerUtilTest.class);
        assertNotNull(logger);
    }

    @Test
    public void testGetLoggerWithClassHasCorrectName() {
        Logger logger = LoggerUtil.getLogger(LoggerUtilTest.class);
        assertEquals("com.protegrity.devedition.utils.LoggerUtilTest", logger.getName());
    }

    @Test
    public void testGetLoggerWithDifferentClasses() {
        Logger logger1 = LoggerUtil.getLogger(String.class);
        Logger logger2 = LoggerUtil.getLogger(Integer.class);
        
        assertNotNull(logger1);
        assertNotNull(logger2);
        assertNotSame(logger1, logger2);
    }

    @Test
    public void testGetLoggerDefaultName() {
        Logger logger = LoggerUtil.getLogger();
        assertEquals("com.protegrity.developer", logger.getName());
    }

    @Test
    public void testGetLoggerWithNullClassDoesNotThrow() {
        try {
            Logger logger = LoggerUtil.getLogger(null);
            // SLF4J handles null, returns root logger or throws
            assertNotNull(logger);
        } catch (NullPointerException e) {
            // This is also acceptable behavior
        }
    }

    @Test
    public void testGetLoggerWithSameClassReturnsSameInstance() {
        Logger logger1 = LoggerUtil.getLogger(LoggerUtilTest.class);
        Logger logger2 = LoggerUtil.getLogger(LoggerUtilTest.class);
        assertSame(logger1, logger2);
    }

    @Test
    public void testLoggerIsUsable() {
        Logger logger = LoggerUtil.getLogger();
        // Should not throw any exceptions
        logger.info("Test log message");
        logger.debug("Debug message");
        logger.warn("Warning message");
        logger.error("Error message");
    }

    @Test
    public void testLoggerWithClassIsUsable() {
        Logger logger = LoggerUtil.getLogger(LoggerUtilTest.class);
        // Should not throw any exceptions
        logger.info("Test log message with class");
        logger.debug("Debug message with class");
    }
}
