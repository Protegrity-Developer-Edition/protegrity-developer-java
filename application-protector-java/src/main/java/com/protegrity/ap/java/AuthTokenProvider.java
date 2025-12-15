package com.protegrity.ap.java;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Provides JWT token retrieval functionality for Protegrity Developer Edition authentication.
 * 
 * <p>This class handles HTTP communication with the Protegrity authentication service
 * to obtain JWT tokens required for API operations.
 * 
 * @since 1.0.0
 */
public class AuthTokenProvider {
    
    /**
     * The hostname for Protegrity Developer Edition API.
     */
    public static final String DEV_EDITION_HOST = "api.developer-edition.protegrity.com";
    
    /**
     * Obtains a JWT token from the Protegrity authentication service.
     * 
     * @param email the user's email address
     * @param password the user's password
     * @param apiKey the API key for service access
     * @return an {@link AuthTokenResponse} containing the JWT token and status information
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public static AuthTokenResponse getJwtToken(String email, String password, String apiKey) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("Email, password, and API key must not be null or empty");
        }
      
        String baseUrl = "https://" + DEV_EDITION_HOST + "/auth/login";
        String payload = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("x-api-key", apiKey);
            httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                // Extract JWT token from JSON response
                String jwtToken = extractJwtToken(responseBody);

                return new AuthTokenResponse(statusCode, jwtToken, apiKey,
                        statusCode == 200 ? null : responseBody);
            }
        } catch (IOException e) {
            return new AuthTokenResponse(500, null, apiKey, "Connection error: " + e.getMessage());
        }
    }

    private static String extractJwtToken(String responseBody) {
        try {
            org.json.JSONObject json = new org.json.JSONObject(responseBody);
            return json.optString("jwt_token", null);
        } catch (Exception e) {
            return null;
        }
    }
}
