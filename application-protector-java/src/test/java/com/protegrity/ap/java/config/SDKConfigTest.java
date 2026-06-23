package com.protegrity.ap.java.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Migration regression tests for {@link SDKConfig}. Focused on the parts of
 * config plumbing that the DE→TE migration relies on: typed getters, default
 * fallback, base URL construction for the TE path, and trailing-slash handling
 * on {@code protect_host}.
 *
 * <p>Env-var resolution is exercised indirectly through the live
 * {@link SDKConfig#load()} path in other tests; here we use the test-only
 * {@link SDKConfig#forTesting(Map)} factory to avoid leaking process env.
 */
public class SDKConfigTest {

    private static SDKConfig cfg(Map<String, String> m) {
        return SDKConfig.forTesting(m);
    }

    // ── getInt / get with defaults ─────────────────────────────────────────

    @Test
    public void getIntReturnsDefaultWhenKeyMissing() {
        assertEquals(42, cfg(new HashMap<>()).getInt("request_timeout", 42));
    }

    @Test
    public void getIntParsesNumericValue() {
        Map<String, String> m = new HashMap<>();
        m.put("request_timeout", "15");
        assertEquals(15, cfg(m).getInt("request_timeout", 30));
    }

    @Test
    public void getIntFallsBackOnNonNumericValue() {
        Map<String, String> m = new HashMap<>();
        m.put("max_retries", "not-a-number");
        // Must not throw — silently fall back so a bad config file doesn't
        // crash the SDK during init.
        assertEquals(3, cfg(m).getInt("max_retries", 3));
    }

    @Test
    public void getStringReturnsNullForUnknownKey() {
        assertNull(cfg(new HashMap<>()).get("nope"));
    }

    @Test
    public void getStringWithDefaultFallsBack() {
        assertEquals("fallback", cfg(new HashMap<>()).get("nope", "fallback"));
    }

    // ── getBaseUrl: Team Edition (protect_host configured) ─────────────────

    @Test
    public void baseUrlUsesProtectHostAndVersionInTeMode() {
        Map<String, String> m = new HashMap<>();
        m.put("protect_host", "https://cp.example.com/pty");
        m.put("version", "1");
        assertEquals("https://cp.example.com/pty/v1/protect",
                cfg(m).getBaseUrl("protect"));
    }

    @Test
    public void baseUrlStripsTrailingSlashFromProtectHost() {
        // A trailing slash on the host is a common config mistake; we should
        // not produce //v1/ in the URL.
        Map<String, String> m = new HashMap<>();
        m.put("protect_host", "https://cp.example.com/pty/");
        m.put("version", "1");
        assertEquals("https://cp.example.com/pty/v1/unprotect",
                cfg(m).getBaseUrl("unprotect"));
    }

    @Test
    public void baseUrlHonorsCustomApiVersion() {
        Map<String, String> m = new HashMap<>();
        m.put("protect_host", "https://cp.example.com/pty");
        m.put("version", "2");
        assertTrue(cfg(m).getBaseUrl("protect").endsWith("/v2/protect"));
    }

    @Test
    public void baseUrlBuildsAllThreeOperations() {
        Map<String, String> m = new HashMap<>();
        m.put("protect_host", "https://cp.example.com/pty");
        m.put("version", "1");
        SDKConfig c = cfg(m);
        assertEquals("https://cp.example.com/pty/v1/protect", c.getBaseUrl("protect"));
        assertEquals("https://cp.example.com/pty/v1/unprotect", c.getBaseUrl("unprotect"));
        assertEquals("https://cp.example.com/pty/v1/reprotect", c.getBaseUrl("reprotect"));
    }

    // ── forTesting factory hygiene ─────────────────────────────────────────

    @Test
    public void forTestingProducesIndependentInstance() {
        Map<String, String> m = new HashMap<>();
        m.put("protect_host", "https://a");
        SDKConfig c = cfg(m);
        // Mutating the source map must not leak into the SDKConfig — otherwise
        // tests interfere with each other.
        m.put("protect_host", "https://b");
        m.put("version", "9");
        assertEquals("https://a", c.get("protect_host"));
    }

    @Test
    public void liveLoadProducesNonNullConfig() {
        // Smoke test: SDKConfig.load() should never throw, even in a bare
        // environment with no PTY_* vars and no config file.
        SDKConfig live = SDKConfig.load();
        assertNotNull(live);
        // Defaults are always present:
        assertEquals("1", live.get("version"));
        assertEquals("30", live.get("request_timeout"));
        assertEquals("3", live.get("max_retries"));
    }

    // ── File-based secrets: permission guard ──────────────────────────────

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private Path writeCfg(String body, String mode) throws IOException {
        Path p = tmp.newFile("config.yaml").toPath();
        Files.writeString(p, body);
        Files.setPosixFilePermissions(p, PosixFilePermissions.fromString(mode));
        return p;
    }

    private static boolean isPosixFs() {
        return Path.of(System.getProperty("user.home"))
                .getFileSystem().supportedFileAttributeViews().contains("posix");
    }

    @Test
    public void securePerms_secretsAreKept() throws IOException {
        assumeFalse("POSIX-only test", !isPosixFs());
        Path p = writeCfg("static_token: tok-xyz\nclient_secret: cs-xyz\n", "rw-------");
        Map<String, String> out = SDKConfig.loadConfigFileFromPath(p.toString());
        assertEquals("tok-xyz", out.get("static_token"));
        assertEquals("cs-xyz", out.get("client_secret"));
    }

    @Test
    public void loosePerms_secretsAreDroppedNonSecretsKept() throws IOException {
        assumeFalse("POSIX-only test", !isPosixFs());
        Path p = writeCfg(
            "protect_host: https://ok\nstatic_token: tok-xyz\nclient_secret: cs-xyz\n",
            "rw-r--r--"
        );
        Map<String, String> out = SDKConfig.loadConfigFileFromPath(p.toString());
        assertEquals("https://ok", out.get("protect_host"));
        assertFalse("static_token must be dropped", out.containsKey("static_token"));
        assertFalse("client_secret must be dropped", out.containsKey("client_secret"));
    }

    @Test
    public void loosePerms_noSecrets_noChange() throws IOException {
        assumeFalse("POSIX-only test", !isPosixFs());
        Path p = writeCfg("protect_host: https://ok\n", "rw-r--r--");
        Map<String, String> out = SDKConfig.loadConfigFileFromPath(p.toString());
        assertEquals("https://ok", out.get("protect_host"));
    }
}
