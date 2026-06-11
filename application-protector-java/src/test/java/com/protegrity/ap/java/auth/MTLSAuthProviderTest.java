package com.protegrity.ap.java.auth;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for MTLSAuthProvider (PTY-151133).
 */
public class MTLSAuthProviderTest {

    @Test
    public void testMissingCertThrows() {
        MTLSAuthProvider provider = new MTLSAuthProvider();
        try {
            provider.initialize();
            fail("Should throw without cert/key configured");
        } catch (AuthenticationException e) {
            assertTrue(e.getMessage().contains("PTY_CLIENT_CERT")
                || e.getMessage().contains("client_cert"));
        }
    }

    @Test
    public void testGetAuthMode() {
        MTLSAuthProvider provider = new MTLSAuthProvider();
        assertEquals("mtls", provider.getAuthMode());
    }
}
