package com.protegrity.ap.java.auth;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auth provider for Bearer Token (OAuth2 client_credentials or static token).
 *
 * <p>Supports two modes:
 * <ul>
 *   <li>Static token: PTY_STATIC_TOKEN env var</li>
 *   <li>OAuth2 token endpoint: PTY_TOKEN_ENDPOINT, PTY_CLIENT_ID, PTY_CLIENT_SECRET</li>
 * </ul>
 *
 * @since 1.1.0
 */
public class BearerTokenAuthProvider implements AuthProvider {

    private static final Logger logger = LoggerFactory.getLogger(BearerTokenAuthProvider.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private String accessToken;
    private String tokenEndpoint;
    private String clientId;
    private String clientSecret;

    @Override
    public void initialize() throws AuthenticationException {
        String staticToken = System.getenv("PTY_STATIC_TOKEN");
        if (staticToken != null && !staticToken.isEmpty()) {
            this.accessToken = staticToken;
            return;
        }

        this.tokenEndpoint = System.getenv("PTY_TOKEN_ENDPOINT");
        this.clientId = System.getenv("PTY_CLIENT_ID");
        this.clientSecret = System.getenv("PTY_CLIENT_SECRET");

        if (tokenEndpoint == null || tokenEndpoint.isEmpty()) {
            throw new AuthenticationException(
                "Bearer token mode requires PTY_STATIC_TOKEN or PTY_TOKEN_ENDPOINT.");
        }
        if (clientId == null || clientSecret == null) {
            throw new AuthenticationException(
                "Bearer token mode with endpoint requires PTY_CLIENT_ID and PTY_CLIENT_SECRET.");
        }

        fetchToken();
    }

    private void fetchToken() {
        try {
            URL url = new URL(tokenEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "grant_type=client_credentials&client_id=" + clientId
                + "&client_secret=" + clientSecret;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new AuthenticationException(
                    "Token endpoint returned HTTP " + status);
            }

            JsonNode json = mapper.readTree(conn.getInputStream());
            this.accessToken = json.get("access_token").asText();
            logger.debug("Bearer token obtained from endpoint");
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Failed to fetch bearer token: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> authenticateRequest(String method, String url,
                                                   Map<String, String> headers, byte[] body) {
        headers.put("Authorization", "Bearer " + accessToken);
        return headers;
    }

    @Override
    public String getAuthMode() {
        return "bearer_token";
    }
}
