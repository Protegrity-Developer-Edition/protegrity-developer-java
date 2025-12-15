package com.protegrity.ap.java;

/**
 * Handles authentication with Protegrity Developer Edition services.
 * 
 * <p>This class manages the authentication process using environment variables
 * and obtains JWT tokens required for API operations. Authentication credentials
 * are read from the following environment variables:
 * <ul>
 *   <li>{@code DEV_EDITION_EMAIL} - User email address</li>
 *   <li>{@code DEV_EDITION_PASSWORD} - User password</li>
 *   <li>{@code DEV_EDITION_API_KEY} - API key for service access</li>
 * </ul>
 * 
 * @since 1.0.0
 */
public class Authenticator {

    private String apiKey;
    private String jwtToken;

    /**
     * Constructs a new Authenticator and performs authentication.
     * 
     * @throws InitializationException if authentication fails or required environment variables are missing
     */
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

    /**
     * Returns the API key used for authentication.
     * 
     * @return the API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Returns the JWT token obtained during authentication.
     * 
     * @return the JWT token
     */
    public String getJwtToken() {
        return jwtToken;
    }
}