package com.protegrity.ap.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.junit.Test;

import com.protegrity.ap.java.config.SDKConfig;

/**
 * Unit tests for the timeout/retry resilience layer wired into
 * {@link CoreproviderAdapter#sendRequest}. Tests target the package-private
 * helpers so we don't need a live HTTP server.
 */
public class CoreproviderAdapterResilienceTest {

    private static SDKConfig configWith(String timeout, String retries) {
        Map<String, String> m = new HashMap<>();
        if (timeout != null) m.put("request_timeout", timeout);
        if (retries != null) m.put("max_retries", retries);
        return SDKConfig.forTesting(m);
    }

    // ── resolveTimeoutSeconds ──────────────────────────────────────────────

    @Test
    public void timeoutDefaultsTo30WhenMissing() {
        assertEquals(30, CoreproviderAdapter.resolveTimeoutSeconds(configWith(null, null)));
    }

    @Test
    public void timeoutHonorsExplicitValue() {
        assertEquals(5, CoreproviderAdapter.resolveTimeoutSeconds(configWith("5", null)));
    }

    @Test
    public void timeoutClampsZeroToOne() {
        // 0 would make every request fail instantly; clamp.
        assertEquals(1, CoreproviderAdapter.resolveTimeoutSeconds(configWith("0", null)));
    }

    @Test
    public void timeoutClampsNegativeToOne() {
        assertEquals(1, CoreproviderAdapter.resolveTimeoutSeconds(configWith("-5", null)));
    }

    @Test
    public void timeoutFallsBackToDefaultOnBadValue() {
        assertEquals(30, CoreproviderAdapter.resolveTimeoutSeconds(configWith("abc", null)));
    }

    // ── resolveMaxRetries ──────────────────────────────────────────────────

    @Test
    public void retriesDefaultTo3WhenMissing() {
        assertEquals(3, CoreproviderAdapter.resolveMaxRetries(configWith(null, null)));
    }

    @Test
    public void retriesHonorsExplicitValue() {
        assertEquals(7, CoreproviderAdapter.resolveMaxRetries(configWith(null, "7")));
    }

    @Test
    public void retriesZeroDisablesRetries() {
        // PTY_MAX_RETRIES=0 is the documented disable switch.
        assertEquals(0, CoreproviderAdapter.resolveMaxRetries(configWith(null, "0")));
    }

    @Test
    public void retriesClampsNegativeToZero() {
        assertEquals(0, CoreproviderAdapter.resolveMaxRetries(configWith(null, "-2")));
    }

    @Test
    public void retriesFallsBackToDefaultOnBadValue() {
        assertEquals(3, CoreproviderAdapter.resolveMaxRetries(configWith(null, "not-a-number")));
    }

    // ── buildRequestConfig ─────────────────────────────────────────────────

    @Test
    public void requestConfigAppliesTimeoutToAllThreeAxes() {
        RequestConfig rc = CoreproviderAdapter.buildRequestConfig(5_000);
        assertEquals(5_000, rc.getConnectTimeout());
        assertEquals(5_000, rc.getSocketTimeout());
        assertEquals(5_000, rc.getConnectionRequestTimeout());
    }

    // ── buildRetryHandler ──────────────────────────────────────────────────

    @Test
    public void retryHandlerHasRequestedMaxRetries() {
        HttpRequestRetryHandler h = CoreproviderAdapter.buildRetryHandler(4);
        assertNotNull(h);
        assertTrue(h instanceof DefaultHttpRequestRetryHandler);
        assertEquals(4, ((DefaultHttpRequestRetryHandler) h).getRetryCount());
    }

    @Test
    public void retryHandlerDoesNotRetryAfterRequestSent() {
        // Critical: protect/unprotect are POSTs with payloads. Retrying after
        // bytes are on the wire could cause duplicate operations.
        DefaultHttpRequestRetryHandler h =
                (DefaultHttpRequestRetryHandler) CoreproviderAdapter.buildRetryHandler(3);
        assertEquals(false, h.isRequestSentRetryEnabled());
    }

    @Test
    public void retryHandlerZeroNeverRetries() {
        DefaultHttpRequestRetryHandler h =
                (DefaultHttpRequestRetryHandler) CoreproviderAdapter.buildRetryHandler(0);
        assertEquals(0, h.getRetryCount());
    }
}
