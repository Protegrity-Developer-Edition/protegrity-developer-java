package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;

public class InitializationExceptionTest {

    @Test
    public void testExceptionMessage() {
        InitializationException exception = new InitializationException("Test error");
        
        assertEquals("Initialization failed: Test error", exception.getMessage());
    }

    @Test
    public void testExceptionWithEmptyMessage() {
        InitializationException exception = new InitializationException("");
        
        assertEquals("Initialization failed: ", exception.getMessage());
    }

    @Test
    public void testExceptionIsRuntimeException() {
        InitializationException exception = new InitializationException("Test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testExceptionWithNullMessage() {
        InitializationException exception = new InitializationException(null);
        
        assertEquals("Initialization failed: null", exception.getMessage());
    }
}
