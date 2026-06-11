package com.protegrity.ap.java.auth;

/**
 * Thrown when authentication initialization or request signing fails.
 *
 * @since 1.1.0
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
