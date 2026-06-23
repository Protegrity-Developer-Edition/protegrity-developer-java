package com.protegrity.ap.java.auth;

import java.util.Map;

/**
 * Base interface for authentication providers.
 *
 * <p>Each provider knows how to authenticate outgoing HTTP requests for a specific
 * infrastructure auth mechanism (Cognito, AWS IAM SigV4, Bearer token, mTLS, none).
 *
 * @since 1.1.0
 */
public interface AuthProvider {

    /**
     * Initialize the provider (e.g., login, fetch tokens).
     * Called once during Protector initialization.
     *
     * @throws AuthenticationException if initialization fails
     */
    void initialize() throws AuthenticationException;

    /**
     * Apply authentication to an outgoing HTTP request.
     *
     * @param method  HTTP method (e.g., "POST")
     * @param url     full request URL
     * @param headers mutable map of HTTP headers to modify
     * @param body    request body as bytes
     * @return the (possibly modified) headers map
     */
    Map<String, String> authenticateRequest(String method, String url,
                                            Map<String, String> headers, byte[] body);

    /**
     * Return the auth mode identifier for this provider.
     *
     * @return mode string (e.g., "cognito", "aws_iam", "bearer_token", "none", "mtls")
     */
    String getAuthMode();
}
