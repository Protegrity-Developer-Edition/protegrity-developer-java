package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProtectorExceptionTest {

    @Test
    public void testExceptionWithMessageOnly() {
        ProtectorException exception = new ProtectorException("Test error");
        
        assertEquals("Test error", exception.getErrorMessage());
        assertEquals("Test error", exception.getMessage());
    }

    @Test
    public void testExceptionWithErrorCodeAndMessage() {
        ProtectorException exception = new ProtectorException(100, "Test error");
        
        assertEquals(100, exception.getErrorCode());
        assertEquals("Test error", exception.getErrorMessage());
        assertEquals("Error Code: 100, Test error", exception.getMessage());
    }

    @Test
    public void testExceptionWithErrorCodeMessageAndList() {
        int[] errorList = {1, 2, 3};
        ProtectorException exception = new ProtectorException(200, "Multiple errors", errorList);
        
        assertEquals(200, exception.getErrorCode());
        assertEquals("Multiple errors", exception.getErrorMessage());
        assertArrayEquals(errorList, exception.getErrorList());
    }

    @Test
    public void testExceptionWithAllParameters() {
        int[] errorList = {10, 20};
        ProtectorException exception = new ProtectorException(300, "Access denied", errorList, 5);
        
        assertEquals(300, exception.getErrorCode());
        assertEquals("Access denied", exception.getErrorMessage());
        assertArrayEquals(errorList, exception.getErrorList());
        assertEquals(5, exception.getAccessOperation());
    }

    @Test
    public void testExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        ProtectorException exception = new ProtectorException("Wrapped error", cause);
        
        assertEquals("Wrapped error", exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testSetErrorCode() {
        ProtectorException exception = new ProtectorException("Test");
        exception.setErrorCode(999);
        
        assertEquals(999, exception.getErrorCode());
    }

    @Test
    public void testSetErrorMessage() {
        ProtectorException exception = new ProtectorException("Original");
        exception.setErrorMessage("Updated message");
        
        assertEquals("Updated message", exception.getErrorMessage());
    }

    @Test
    public void testGetMessageWithOnlyErrorMessage() {
        ProtectorException exception = new ProtectorException("Only message");
        exception.setErrorCode(0); // Explicitly set to 0
        
        assertEquals("Only message", exception.getMessage());
    }

    @Test
    public void testIsException() {
        ProtectorException exception = new ProtectorException("Test");
        
        assertTrue(exception instanceof Exception);
    }

    @Test
    public void testExceptionWithNullErrorList() {
        ProtectorException exception = new ProtectorException(100, "Error", null);
        
        assertNull(exception.getErrorList());
    }
}
