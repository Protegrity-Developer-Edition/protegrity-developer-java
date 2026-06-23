package com.protegrity.ap.java.auth;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for BearerTokenAuthProvider (PTY-151133).
 */
public class BearerTokenAuthProviderTest {

    @Test
    public void testStaticTokenMode() throws AuthenticationException {
        // Set env via system property workaround: use reflection or test with actual env
        // For unit test, we test the class behavior given an initialized state
        BearerTokenAuthProvider provider = new BearerTokenAuthProvider();
        // Without env vars, initialize should fail
        try {
            provider.initialize();
            fail("Should throw without env vars");
        } catch (AuthenticationException e) {
            assertTrue(e.getMessage().contains("PTY_STATIC_TOKEN")
                || e.getMessage().contains("PTY_TOKEN_ENDPOINT"));
        }
    }

    @Test
    public void testGetAuthMode() {
        BearerTokenAuthProvider provider = new BearerTokenAuthProvider();
        assertEquals("bearer_token", provider.getAuthMode());
    }
}
