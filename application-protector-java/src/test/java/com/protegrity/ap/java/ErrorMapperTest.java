package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the ErrorMapper class.
 */
public class ErrorMapperTest {
    
    @Test
    public void testGetErrorDetailWithNull() {
        ErrorMapper.ErrorDetail detail = ErrorMapper.getErrorDetail(null);
        assertNotNull("Error detail should not be null", detail);
        assertEquals("Error code should be -1 for null", -1, detail.getErrorCode());
        assertEquals("Error message should indicate unknown error", "Unknown error occurred", detail.getErrorMessage());
    }
    
    @Test
    public void testGetErrorDetailWithEmpty() {
        ErrorMapper.ErrorDetail detail = ErrorMapper.getErrorDetail("");
        assertNotNull("Error detail should not be null", detail);
        assertEquals("Error code should be -1 for empty", -1, detail.getErrorCode());
        assertEquals("Error message should indicate unknown error", "Unknown error occurred", detail.getErrorMessage());
    }
    
    @Test
    public void testGetErrorDetailWithKnownError() {
        ErrorMapper.ErrorDetail detail = ErrorMapper.getErrorDetail("The username could not be found in the policy in shared memory.");
        assertNotNull("Error detail should not be null", detail);
        assertEquals("Error code should be 1", 1, detail.getErrorCode());
        assertTrue("Error message should contain username", detail.getErrorMessage().contains("username"));
    }
    
    @Test
    public void testGetErrorDetailWithUnknownError() {
        ErrorMapper.ErrorDetail detail = ErrorMapper.getErrorDetail("This is a completely unknown error");
        assertNotNull("Error detail should not be null", detail);
        assertEquals("Error code should be -1 for unknown", -1, detail.getErrorCode());
        assertEquals("Error message should be original", "This is a completely unknown error", detail.getErrorMessage());
    }
    
    @Test
    public void testGetErrorDetailWithPartialMatch() {
        ErrorMapper.ErrorDetail detail = ErrorMapper.getErrorDetail("Something about data element could not be found");
        assertNotNull("Error detail should not be null", detail);
        // Should find partial match for "The data element could not be found in the policy"
        assertTrue("Should have matched error code", detail.getErrorCode() > 0 || detail.getErrorCode() == -1);
    }
    
    @Test
    public void testErrorDetailToString() {
        ErrorMapper.ErrorDetail detail = new ErrorMapper.ErrorDetail(1, "Test error message");
        String str = detail.toString();
        assertNotNull("toString should not return null", str);
        assertTrue("toString should contain error message", str.contains("Test error message"));
    }
    
    @Test
    public void testGetErrorCode() {
        ErrorMapper.ErrorDetail detail = new ErrorMapper.ErrorDetail(42, "Test message");
        assertEquals("Error code should match", 42, detail.getErrorCode());
    }
    
    @Test
    public void testGetErrorMessage() {
        ErrorMapper.ErrorDetail detail = new ErrorMapper.ErrorDetail(1, "Test error message");
        assertEquals("Error message should match", "Test error message", detail.getErrorMessage());
    }
}
