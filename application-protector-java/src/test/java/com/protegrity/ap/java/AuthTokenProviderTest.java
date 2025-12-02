package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the AuthTokenProvider class.
 */
public class AuthTokenProviderTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetJwtTokenWithNullEmail() {
        AuthTokenProvider.getJwtToken(null, "password", "apiKey");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetJwtTokenWithEmptyEmail() {
        AuthTokenProvider.getJwtToken("", "password", "apiKey");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetJwtTokenWithNullPassword() {
        AuthTokenProvider.getJwtToken("email@test.com", null, "apiKey");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetJwtTokenWithEmptyPassword() {
        AuthTokenProvider.getJwtToken("email@test.com", "", "apiKey");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetJwtTokenWithNullApiKey() {
        AuthTokenProvider.getJwtToken("email@test.com", "password", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetJwtTokenWithEmptyApiKey() {
        AuthTokenProvider.getJwtToken("email@test.com", "password", "");
    }
    
    @Test
    public void testGetJwtTokenWithInvalidCredentials() {
        AuthTokenResponse response = AuthTokenProvider.getJwtToken("invalid@test.com", "wrongpassword", "invalidkey");
        assertNotNull("Response should not be null", response);
        assertNotEquals("Status code should not be 200 for invalid credentials", 200, response.getStatusCode());
    }
    
    @Test
    public void testAuthTokenResponseGetters() {
        AuthTokenResponse response = new AuthTokenResponse(200, "token123", "key456", null);
        assertEquals("Status code should match", 200, response.getStatusCode());
        assertEquals("JWT token should match", "token123", response.getJwtToken());
        assertEquals("API key should match", "key456", response.getApiKey());
        assertNull("Error message should be null", response.getErrorMessage());
    }
    
    @Test
    public void testAuthTokenResponseWithError() {
        AuthTokenResponse response = new AuthTokenResponse(401, null, "key456", "Unauthorized");
        assertEquals("Status code should match", 401, response.getStatusCode());
        assertNull("JWT token should be null", response.getJwtToken());
        assertEquals("Error message should match", "Unauthorized", response.getErrorMessage());
    }
}
