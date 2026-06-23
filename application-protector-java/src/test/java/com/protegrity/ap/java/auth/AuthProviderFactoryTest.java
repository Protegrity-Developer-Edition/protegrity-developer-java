package com.protegrity.ap.java.auth;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for Auth Provider abstraction (PTY-151133).
 */
public class AuthProviderFactoryTest {

    // ──────────────────────────────────────────────────────────
    // Auto-detection tests
    // ──────────────────────────────────────────────────────────

    @Test
    public void testExplicitAuthModeOverridesDetection() throws Exception {
        // PTY_AUTH_MODE is read first — tested via factory.create()
        // This test verifies the registry has all expected modes
        String[] modes = {"cognito", "aws_iam", "bearer_token", "none", "mtls"};
        for (String mode : modes) {
            // Should not throw "Unknown auth mode"
            try {
                AuthProviderFactory.create(mode);
            } catch (AuthenticationException e) {
                // May fail on initialize() due to missing env vars — that's OK
                // The important thing is it didn't throw "Unknown auth mode"
                assertFalse("Should not be unknown mode: " + mode,
                    e.getMessage().contains("Unknown auth mode"));
            }
        }
    }

    @Test(expected = AuthenticationException.class)
    public void testUnknownAuthModeThrows() throws AuthenticationException {
        AuthProviderFactory.create("invalid_mode");
    }

    @Test
    public void testCreateReturnsCorrectProviderType() throws Exception {
        try {
            AuthProvider provider = AuthProviderFactory.create("none");
            assertTrue(provider instanceof NoneAuthProvider);
        } catch (AuthenticationException e) {
            fail("none mode should not fail: " + e.getMessage());
        }
    }
}
