package com.protegrity.ap.java.auth;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for NoneAuthProvider (PTY-151133).
 */
public class NoneAuthProviderTest {

    @Test
    public void testInitializeDoesNotThrow() throws AuthenticationException {
        NoneAuthProvider provider = new NoneAuthProvider();
        provider.initialize();
    }

    @Test
    public void testAuthenticateRequestNoHeadersAdded() throws AuthenticationException {
        NoneAuthProvider provider = new NoneAuthProvider();
        provider.initialize();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, String> result = provider.authenticateRequest(
            "POST", "http://api/v1/protect", headers, new byte[0]);

        assertEquals(1, result.size());
        assertEquals("application/json", result.get("Content-Type"));
    }

    @Test
    public void testGetAuthMode() throws AuthenticationException {
        NoneAuthProvider provider = new NoneAuthProvider();
        provider.initialize();
        assertEquals("none", provider.getAuthMode());
    }
}
