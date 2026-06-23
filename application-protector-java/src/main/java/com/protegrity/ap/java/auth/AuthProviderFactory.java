package com.protegrity.ap.java.auth;

import java.util.Map;

/**
 * Factory for creating the appropriate AuthProvider based on configuration.
 *
 * @since 1.1.0
 */
public class AuthProviderFactory {

    private static final Map<String, Class<? extends AuthProvider>> REGISTRY = Map.of(
        "cognito", CognitoAuthProvider.class,
        "aws_iam", AWSIAMAuthProvider.class,
        "bearer_token", BearerTokenAuthProvider.class,
        "none", NoneAuthProvider.class,
        "mtls", MTLSAuthProvider.class
    );

    /**
     * Create and initialize an auth provider based on the specified mode.
     *
     * @param authMode the authentication mode string
     * @return initialized AuthProvider instance
     * @throws AuthenticationException if the mode is unknown or initialization fails
     */
    public static AuthProvider create(String authMode) throws AuthenticationException {
        Class<? extends AuthProvider> providerClass = REGISTRY.get(authMode);
        if (providerClass == null) {
            throw new AuthenticationException("Unknown auth mode: " + authMode
                + ". Supported modes: " + REGISTRY.keySet());
        }

        try {
            AuthProvider provider = providerClass.getDeclaredConstructor().newInstance();
            provider.initialize();
            return provider;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException(
                "Failed to create auth provider for mode '" + authMode + "': " + e.getMessage(), e);
        }
    }

    /**
     * Auto-detect the auth mode from environment variables.
     *
     * <p>Detection order:
     * <ol>
     *   <li>PTY_AUTH_MODE env var (explicit)</li>
     *   <li>DEV_EDITION_* vars present → cognito</li>
     *   <li>PTY_CP_HOST + AWS creds → aws_iam</li>
     *   <li>PTY_CP_HOST alone → error</li>
     * </ol>
     *
     * @return the detected auth mode string
     * @throws AuthenticationException if mode cannot be determined
     */
    public static String detectAuthMode() throws AuthenticationException {
        String explicit = System.getenv("PTY_AUTH_MODE");
        boolean hasDeVars = System.getenv("DEV_EDITION_EMAIL") != null
            && System.getenv("DEV_EDITION_PASSWORD") != null
            && System.getenv("DEV_EDITION_API_KEY") != null;
        boolean hasAwsCreds = System.getenv("AWS_ACCESS_KEY_ID") != null
            || System.getenv("AWS_PROFILE") != null;
        String host = System.getenv("PTY_CP_HOST");
        boolean hasTeHost = host != null && !host.isEmpty();

        // Conflict: explicit aws_iam but no PTY_CP_HOST
        if ("aws_iam".equals(explicit) && !hasTeHost) {
            String msg = "PTY_AUTH_MODE=aws_iam but PTY_CP_HOST is not set. "
                + "The SDK cannot use SigV4 without a Team Edition endpoint. ";
            if (hasDeVars) {
                msg += "To use Developer Edition, unset: PTY_AUTH_MODE, AWS_ACCESS_KEY_ID, "
                    + "AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN, AWS_PROFILE";
            } else {
                msg += "Set PTY_CP_HOST to your Team Edition endpoint.";
            }
            throw new AuthenticationException(msg);
        }

        // Warning: explicit aws_iam but DE vars also present (leftover from previous session)
        if ("aws_iam".equals(explicit) && hasTeHost && hasDeVars) {
            System.err.println("WARNING: PTY_AUTH_MODE=aws_iam but Developer Edition variables are also set "
                + "(DEV_EDITION_*). These will be ignored. "
                + "To silence this warning, unset: DEV_EDITION_EMAIL, "
                + "DEV_EDITION_PASSWORD, DEV_EDITION_API_KEY.");
        }

        // Warning: explicit cognito but TE vars also present (leftover from previous session)
        if ("cognito".equals(explicit) && hasAwsCreds && hasTeHost) {
            System.err.println("WARNING: PTY_AUTH_MODE=cognito but Team Edition variables are also set "
                + "(PTY_CP_HOST, AWS credentials). These will be ignored. "
                + "To silence this warning, unset: PTY_CP_HOST, AWS_ACCESS_KEY_ID, "
                + "AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN, AWS_PROFILE.");
        }

        // Conflict: both DE and TE credentials present without explicit mode
        if (explicit == null && hasDeVars && hasAwsCreds && hasTeHost) {
            throw new AuthenticationException(
                "Conflicting credentials: both DEV_EDITION_* and AWS/PTY_CP_HOST variables are set. "
                + "To use Team Edition, unset: DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD, DEV_EDITION_API_KEY. "
                + "To use Developer Edition, unset: PTY_CP_HOST, AWS_ACCESS_KEY_ID, "
                + "AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN, AWS_PROFILE.");
        }

        if (explicit != null && !explicit.isEmpty()) {
            return explicit;
        }

        // Legacy DE vars present → cognito
        if (hasDeVars) {
            return "cognito";
        }

        // TE vars present → try to infer
        if (hasTeHost) {
            if (hasAwsCreds) {
                return "aws_iam";
            }
            if (System.getenv("PTY_STATIC_TOKEN") != null || System.getenv("PTY_TOKEN_ENDPOINT") != null) {
                return "bearer_token";
            }
            if (System.getenv("PTY_CLIENT_CERT") != null) {
                return "mtls";
            }
            throw new AuthenticationException(
                "PTY_CP_HOST is set but no auth credentials found. "
                + "Set PTY_AUTH_MODE explicitly or provide appropriate credentials.");
        }

        throw new AuthenticationException(
            "Cannot determine auth mode. Set PTY_AUTH_MODE or DEV_EDITION_* environment variables.");
    }
}
