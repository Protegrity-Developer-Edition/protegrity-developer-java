package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;

public class AuthTokenResponseTest {

    @Test
    public void testSuccessfulResponse() {
        AuthTokenResponse response = new AuthTokenResponse(200, "jwt-token-123", "api-key-456", null);
        
        assertEquals(200, response.getStatusCode());
        assertEquals("jwt-token-123", response.getJwtToken());
        assertEquals("api-key-456", response.getApiKey());
        assertNull(response.getErrorMessage());
    }

    @Test
    public void testErrorResponse() {
        AuthTokenResponse response = new AuthTokenResponse(401, null, null, "Authentication failed");
        
        assertEquals(401, response.getStatusCode());
        assertNull(response.getJwtToken());
        assertNull(response.getApiKey());
        assertEquals("Authentication failed", response.getErrorMessage());
    }

    @Test
    public void testResponseWithAllFields() {
        AuthTokenResponse response = new AuthTokenResponse(500, "token", "key", "Server error");
        
        assertEquals(500, response.getStatusCode());
        assertEquals("token", response.getJwtToken());
        assertEquals("key", response.getApiKey());
        assertEquals("Server error", response.getErrorMessage());
    }

    @Test
    public void testResponseWithEmptyStrings() {
        AuthTokenResponse response = new AuthTokenResponse(200, "", "", "");
        
        assertEquals(200, response.getStatusCode());
        assertEquals("", response.getJwtToken());
        assertEquals("", response.getApiKey());
        assertEquals("", response.getErrorMessage());
    }
}
