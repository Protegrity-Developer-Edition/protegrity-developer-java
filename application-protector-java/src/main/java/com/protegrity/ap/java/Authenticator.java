package com.protegrity.ap.java;

public class Authenticator {

    private String apiKey;
    private String jwtToken;

    public Authenticator() {
        try {
            authenticate();
        } catch (Exception e) {
            throw new InitializationException(e.getMessage());
        }
    }

    private void authenticate() {
        String email = System.getenv("DEV_EDITION_EMAIL");
        String password = System.getenv("DEV_EDITION_PASSWORD");
        String apiKey = System.getenv("DEV_EDITION_API_KEY");
        
        if (email == null || password == null) {
            throw new InitializationException("Authentication failed: Both DEV_EDITION_EMAIL and DEV_EDITION_PASSWORD must be provided.");
        }

        if (apiKey == null) {
            throw new InitializationException("Authentication failed: DEV_EDITION_API_KEY must be provided.");
        }

        // Simulate API call to get JWT token
        AuthTokenResponse response = AuthTokenProvider.getJwtToken(email, password, apiKey);

        if (response.getStatusCode() != 200) {
            throw new InitializationException(response.getErrorMessage());
        }

        this.apiKey = apiKey;
        this.jwtToken = response.getJwtToken();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}