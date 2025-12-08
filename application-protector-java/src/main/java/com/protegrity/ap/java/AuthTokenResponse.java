package com.protegrity.ap.java;

/**
 * Represents the response from an authentication token request.
 * 
 * <p>This class encapsulates the result of a JWT token request, including
 * the HTTP status code, JWT token, API key, and any error messages.
 * 
 * @since 1.0.0
 */
public  class AuthTokenResponse {
    private int statusCode;
    private String jwtToken;
    private String apiKey;
    private String errorMessage;

    /**
     * Constructs a new AuthTokenResponse.
     * 
     * @param statusCode the HTTP status code from the authentication request
     * @param jwtToken the JWT token if authentication was successful, null otherwise
     * @param apiKey the API key used for the request
     * @param errorMessage error message if authentication failed, null otherwise
     */
    public AuthTokenResponse(int statusCode, String jwtToken, String apiKey, String errorMessage) {
        this.statusCode = statusCode;
        this.jwtToken = jwtToken;
        this.apiKey = apiKey;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the HTTP status code from the authentication request.
     * 
     * @return the HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the JWT token obtained from authentication.
     * 
     * @return the JWT token, or null if authentication failed
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Returns the API key used for the authentication request.
     * 
     * @return the API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Returns the error message if authentication failed.
     * 
     * @return the error message, or null if authentication was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }
} 