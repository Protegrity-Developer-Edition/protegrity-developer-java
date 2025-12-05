package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;

public class SessionTimeoutExceptionTest {

    @Test
    public void testExceptionCreation() {
        SessionTimeoutException exception = new SessionTimeoutException("Session expired");
        
        assertNotNull(exception);
        assertTrue(exception instanceof ProtectorException);
    }

    @Test
    public void testExceptionMessage() {
        SessionTimeoutException exception = new SessionTimeoutException("Timeout occurred");
        
        assertEquals("Timeout occurred", exception.getErrorMessage());
    }

    @Test
    public void testExceptionWithEmptyMessage() {
        SessionTimeoutException exception = new SessionTimeoutException("");
        
        assertEquals("", exception.getErrorMessage());
    }

    @Test
    public void testExceptionInheritance() {
        SessionTimeoutException exception = new SessionTimeoutException("Test");
        
        assertTrue(exception instanceof ProtectorException);
        assertTrue(exception instanceof Exception);
    }

    @Test
    public void testExceptionWithNullMessage() {
        SessionTimeoutException exception = new SessionTimeoutException(null);
        
        assertNull(exception.getErrorMessage());
    }
}
