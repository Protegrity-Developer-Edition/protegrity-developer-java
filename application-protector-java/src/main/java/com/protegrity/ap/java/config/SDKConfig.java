package com.protegrity.ap.java.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SDK configuration loader with resolution order: env vars > config file > defaults.
 *
 * @since 1.1.0
 */
public class SDKConfig {

    private static final Logger logger = LoggerFactory.getLogger(SDKConfig.class);

    private static final Map<String, String> DEFAULTS = Map.of(
        "version", "1",
        "request_timeout", "30",
        "max_retries", "3"
    );

    // Keys that hold secrets-at-rest. Dropped from fileConfig if the file is
    // group/world readable on POSIX systems. Mirrors `~/.pgpass` behavior.
    private static final List<String> SECRET_FILE_KEYS = List.of("static_token", "client_secret");

    private final Map<String, String> config;

    private SDKConfig(Map<String, String> config) {
        this.config = config;
    }

    // Public factory exposed for unit tests so callers can supply a fixed
    // config map without env vars or a config file. Not for production use.
    public static SDKConfig forTesting(Map<String, String> config) {
        return new SDKConfig(new HashMap<>(config));
    }

    /**
     * Load configuration with resolution order: env > file > defaults.
     *
     * @return loaded SDKConfig instance
     */
    public static SDKConfig load() {
        Map<String, String> fileConfig = loadConfigFile();
        Map<String, String> resolved = new HashMap<>();

        resolved.put("protect_host", resolve("PTY_CP_HOST", "protect_host", fileConfig, null));
        resolved.put("auth_mode", resolve("PTY_AUTH_MODE", "auth_mode", fileConfig, null));
        resolved.put("version", resolve("PTY_API_VERSION", "version", fileConfig, DEFAULTS.get("version")));
        resolved.put("request_timeout", resolve("PTY_REQUEST_TIMEOUT", "request_timeout", fileConfig, DEFAULTS.get("request_timeout")));
        resolved.put("max_retries", resolve("PTY_MAX_RETRIES", "max_retries", fileConfig, DEFAULTS.get("max_retries")));
        resolved.put("token_endpoint", resolve("PTY_TOKEN_ENDPOINT", "token_endpoint", fileConfig, null));
        resolved.put("client_id", resolve("PTY_CLIENT_ID", "client_id", fileConfig, null));
        resolved.put("client_secret", resolve("PTY_CLIENT_SECRET", "client_secret", fileConfig, null));
        resolved.put("static_token", resolve("PTY_STATIC_TOKEN", "static_token", fileConfig, null));
        resolved.put("client_cert", resolve("PTY_CLIENT_CERT", "client_cert", fileConfig, null));
        resolved.put("client_key", resolve("PTY_CLIENT_KEY", "client_key", fileConfig, null));
        resolved.put("ca_cert", resolve("PTY_CA_CERT", "ca_cert", fileConfig, null));

        return new SDKConfig(resolved);
    }

    public String get(String key) {
        return config.get(key);
    }

    public String get(String key, String defaultValue) {
        String val = config.get(key);
        return val != null ? val : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        String val = config.get(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get the base URL for API requests.
     * Uses PTY_CP_HOST if set, otherwise falls back to legacy DEV_EDITION_HOST.
     */
    public String getBaseUrl(String operationType) {
        String host = config.get("protect_host");
        String version = config.get("version");

        if (host != null && !host.isEmpty()) {
            // TE mode: host includes full URL with base path (e.g., https://host/pty)
            String baseUrl = host.endsWith("/") ? host.substring(0, host.length() - 1) : host;
            return baseUrl + "/v" + version + "/" + operationType;
        }

        // Legacy DE mode
        String runtimeHost = System.getenv().getOrDefault("DEV_EDITION_HOST",
            "api.developer-edition.protegrity.com");
        String runtimeVersion = System.getenv().getOrDefault("DEV_EDITION_VERSION", "1");
        return "https://" + runtimeHost + "/v" + runtimeVersion + "/" + operationType;
    }

    /**
     * Check if running in Developer Edition mode (DEV_EDITION_* vars present).
     */
    public boolean isDeveloperEdition() {
        return System.getenv("DEV_EDITION_EMAIL") != null
            || System.getenv("DEV_EDITION_API_KEY") != null;
    }

    private static String resolve(String envVar, String fileKey,
                                  Map<String, String> fileConfig, String defaultValue) {
        String envVal = System.getenv(envVar);
        if (envVal != null && !envVal.isEmpty()) return envVal;
        String fileVal = fileConfig.get(fileKey);
        if (fileVal != null && !fileVal.isEmpty()) return fileVal;
        return defaultValue;
    }

    private static Map<String, String> loadConfigFile() {
        String configPath = System.getenv("PTY_CONFIG_FILE");
        if (configPath == null || configPath.isEmpty()) {
            configPath = System.getProperty("user.home") + "/.protegrity/config.yaml";
        }
        return loadConfigFileFromPath(configPath);
    }

    // Package-private for testing — lets tests pass an explicit path without
    // mutating process env vars.
    static Map<String, String> loadConfigFileFromPath(String configPath) {
        Map<String, String> result = new HashMap<>();
        Path path = Paths.get(configPath);
        if (!Files.isRegularFile(path)) {
            return result;
        }

        try {
            // Simple YAML-like parsing (key: value per line)
            // Avoids adding a YAML library dependency
            for (String line : Files.readAllLines(path)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int colon = line.indexOf(':');
                if (colon > 0) {
                    String key = line.substring(0, colon).trim();
                    String value = line.substring(colon + 1).trim();
                    // Remove quotes if present
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (value.startsWith("'") && value.endsWith("'")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    result.put(key, value);
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to read config file {}: {}", configPath, e.getMessage());
        }

        // Drop secret keys if the file is group/world readable (POSIX only).
        // Windows ACLs aren't checked — out of scope for this guard.
        if (!isFileSecure(path)) {
            for (String key : SECRET_FILE_KEYS) {
                if (result.containsKey(key)) {
                    logger.warn("Refusing to read '{}' from {}: file is group/world readable. "
                        + "Run: chmod 600 {}", key, configPath, configPath);
                    result.remove(key);
                }
            }
        }

        return result;
    }

    /**
     * True if {@code path} is readable only by the owner (POSIX permissions).
     * On non-POSIX filesystems (Windows) returns true — we don't try to map ACLs.
     */
    private static boolean isFileSecure(Path path) {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
            return !perms.contains(PosixFilePermission.GROUP_READ)
                && !perms.contains(PosixFilePermission.GROUP_WRITE)
                && !perms.contains(PosixFilePermission.GROUP_EXECUTE)
                && !perms.contains(PosixFilePermission.OTHERS_READ)
                && !perms.contains(PosixFilePermission.OTHERS_WRITE)
                && !perms.contains(PosixFilePermission.OTHERS_EXECUTE);
        } catch (UnsupportedOperationException e) {
            // Non-POSIX FS (Windows) — trust the ACL, skip the check.
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
