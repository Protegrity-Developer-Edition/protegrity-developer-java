package com.protegrity.ap.java;

public  class AuthTokenResponse {
    private int statusCode;
    private String jwtToken;
    private String apiKey;
    private String errorMessage;

    public AuthTokenResponse(int statusCode, String jwtToken, String apiKey, String errorMessage) {
        this.statusCode = statusCode;
        this.jwtToken = jwtToken;
        this.apiKey = apiKey;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
} 