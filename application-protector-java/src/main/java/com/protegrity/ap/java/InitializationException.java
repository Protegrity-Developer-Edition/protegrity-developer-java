package com.protegrity.ap.java;

/**
 * Exception thrown when initialization of Protector components fails.
 * 
 * <p>This exception is typically thrown when required environment variables
 * are missing or authentication with Protegrity services fails during startup.
 * 
 * @since 1.0.1
 */
public class InitializationException extends RuntimeException {

    /**
     * Constructs a new InitializationException with the specified detail message.
     * 
     * <p>The message will be prefixed with "Initialization failed: ".
     * 
     * @param message the detail message describing the initialization failure
     */
    public InitializationException(String message) {
        super("Initialization failed: " + message);
    }
}