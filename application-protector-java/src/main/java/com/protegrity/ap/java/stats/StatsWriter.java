package com.protegrity.ap.java.stats;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistent JSON file storage for usage statistics with file locking.
 *
 * <p>Reads existing stats, merges new session data, and writes back atomically.
 * Uses {@link FileLock} for multi-process safety.
 *
 * <p>Degrades gracefully — never throws exceptions to callers.
 *
 * @since 1.1.0
 */
public class StatsWriter {

    private static final Logger logger = LoggerFactory.getLogger(StatsWriter.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String SCHEMA_VERSION = "1.0";

    /**
     * Flush session stats to disk with file locking.
     *
     * @param sessionData map from UsageCollector.getSessionData()
     */
    @SuppressWarnings("unchecked")
    public static void flush(Map<String, Object> sessionData) {
        if (sessionData == null) return;

        Map<String, ?> dataElements = (Map<String, ?>) sessionData.get("data_elements");
        Map<String, ?> policyUsers = (Map<String, ?>) sessionData.get("policy_users");
        if ((dataElements == null || dataElements.isEmpty())
            && (policyUsers == null || policyUsers.isEmpty())) {
            return; // Nothing to write
        }

        try {
            Path path = getStatsPath();
            Files.createDirectories(path.getParent());

            try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                 FileChannel channel = raf.getChannel();
                 FileLock lock = channel.lock()) {

                // Read existing
                ObjectNode existing = readExisting(raf);
                if (existing == null) {
                    existing = createEmpty();
                }

                // Merge
                merge(existing, sessionData);

                // Write back
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(existing);
                raf.seek(0);
                raf.setLength(0);
                raf.write(json.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            logger.warn("Failed to write usage stats: {}", e.getMessage());
        }
    }

    private static Path getStatsPath() {
        String custom = System.getenv("PTY_STATS_FILE");
        if (custom != null && !custom.isEmpty()) {
            return Paths.get(custom);
        }
        return Paths.get(System.getProperty("user.home"), ".protegrity", "usage_stats.json");
    }

    private static ObjectNode readExisting(RandomAccessFile raf) throws IOException {
        if (raf.length() == 0) return null;
        byte[] bytes = new byte[(int) raf.length()];
        raf.readFully(bytes);
        String content = new String(bytes, StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) return null;

        try {
            JsonNode node = mapper.readTree(content);
            if (node.isObject() && SCHEMA_VERSION.equals(node.path("schema_version").asText())) {
                return (ObjectNode) node;
            }
        } catch (Exception e) {
            logger.warn("Invalid stats file, resetting: {}", e.getMessage());
        }
        return null;
    }

    private static ObjectNode createEmpty() {
        ObjectNode node = mapper.createObjectNode();
        String now = Instant.now().toString();
        node.put("schema_version", SCHEMA_VERSION);
        node.put("collected_since", now);
        node.put("last_updated", now);
        node.set("data_elements", mapper.createObjectNode());
        node.set("policy_users", mapper.createObjectNode());
        return node;
    }

    @SuppressWarnings("unchecked")
    private static void merge(ObjectNode existing, Map<String, Object> sessionData) {
        existing.put("last_updated", Instant.now().toString());

        // Merge data elements
        ObjectNode deNode = (ObjectNode) existing.path("data_elements");
        if (deNode.isMissingNode()) {
            deNode = mapper.createObjectNode();
            existing.set("data_elements", deNode);
        }

        Map<String, Map<String, Object>> dataElements =
            (Map<String, Map<String, Object>>) sessionData.get("data_elements");
        if (dataElements != null) {
            for (Map.Entry<String, Map<String, Object>> entry : dataElements.entrySet()) {
                String deName = entry.getKey();
                Map<String, Object> stats = entry.getValue();

                ObjectNode deStats;
                if (deNode.has(deName)) {
                    deStats = (ObjectNode) deNode.get(deName);
                } else {
                    deStats = mapper.createObjectNode();
                    deStats.put("protect_count", 0);
                    deStats.put("unprotect_count", 0);
                    deStats.put("reprotect_source_count", 0);
                    deStats.put("reprotect_target_count", 0);
                    deStats.put("first_used", (String) stats.get("first_used"));
                    deStats.put("last_used", (String) stats.get("last_used"));
                    deNode.set(deName, deStats);
                }

                deStats.put("protect_count",
                    deStats.path("protect_count").asInt() + ((Number) stats.get("protect_count")).intValue());
                deStats.put("unprotect_count",
                    deStats.path("unprotect_count").asInt() + ((Number) stats.get("unprotect_count")).intValue());
                deStats.put("reprotect_source_count",
                    deStats.path("reprotect_source_count").asInt() + ((Number) stats.get("reprotect_source_count")).intValue());
                deStats.put("reprotect_target_count",
                    deStats.path("reprotect_target_count").asInt() + ((Number) stats.get("reprotect_target_count")).intValue());

                String lastUsed = (String) stats.get("last_used");
                if (lastUsed != null && lastUsed.compareTo(deStats.path("last_used").asText("")) > 0) {
                    deStats.put("last_used", lastUsed);
                }
                String firstUsed = (String) stats.get("first_used");
                if (firstUsed != null && firstUsed.compareTo(deStats.path("first_used").asText("9999-12-31")) < 0) {
                    deStats.put("first_used", firstUsed);
                }
            }
        }

        // Merge policy users
        ObjectNode usersNode = (ObjectNode) existing.path("policy_users");
        if (usersNode.isMissingNode()) {
            usersNode = mapper.createObjectNode();
            existing.set("policy_users", usersNode);
        }

        Map<String, Map<String, Object>> policyUsers =
            (Map<String, Map<String, Object>>) sessionData.get("policy_users");
        if (policyUsers != null) {
            String today = LocalDate.now().toString();
            for (Map.Entry<String, Map<String, Object>> entry : policyUsers.entrySet()) {
                String user = entry.getKey();
                Map<String, Object> userStats = entry.getValue();

                ObjectNode userNode;
                if (usersNode.has(user)) {
                    userNode = (ObjectNode) usersNode.get(user);
                } else {
                    userNode = mapper.createObjectNode();
                    userNode.put("session_count", 0);
                    userNode.put("first_used", today);
                    userNode.put("last_used", today);
                    usersNode.set(user, userNode);
                }

                userNode.put("session_count",
                    userNode.path("session_count").asInt() + ((Number) userStats.get("session_count")).intValue());
                userNode.put("last_used", today);
            }
        }
    }
}
