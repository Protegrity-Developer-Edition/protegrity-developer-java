package com.protegrity.ap.java.auth;

import java.util.Map;

import com.protegrity.ap.java.AuthTokenProvider;
import com.protegrity.ap.java.AuthTokenResponse;

/**
 * Auth provider for Developer Edition (Cognito + API Key).
 *
 * <p>Reads DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD, DEV_EDITION_API_KEY from environment,
 * authenticates via Cognito login endpoint, and attaches JWT + API key to requests.
 *
 * @since 1.1.0
 */
public class CognitoAuthProvider implements AuthProvider {

    private String apiKey;
    private String jwtToken;

    @Override
    public void initialize() throws AuthenticationException {
        String email = System.getenv("DEV_EDITION_EMAIL");
        String password = System.getenv("DEV_EDITION_PASSWORD");
        String apiKey = System.getenv("DEV_EDITION_API_KEY");

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException(
                "Authentication failed: Both DEV_EDITION_EMAIL and DEV_EDITION_PASSWORD must be provided.");
        }

        if (apiKey == null || apiKey.isEmpty()) {
            throw new AuthenticationException(
                "Authentication failed: DEV_EDITION_API_KEY must be provided.");
        }

        AuthTokenResponse response = AuthTokenProvider.getJwtToken(email, password, apiKey);
        if (response.getStatusCode() != 200) {
            throw new AuthenticationException(
                "Cognito login failed: " + response.getErrorMessage());
        }

        this.apiKey = apiKey;
        this.jwtToken = response.getJwtToken();
    }

    @Override
    public Map<String, String> authenticateRequest(String method, String url,
                                                   Map<String, String> headers, byte[] body) {
        headers.put("x-api-key", apiKey);
        headers.put("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    @Override
    public String getAuthMode() {
        return "cognito";
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}
