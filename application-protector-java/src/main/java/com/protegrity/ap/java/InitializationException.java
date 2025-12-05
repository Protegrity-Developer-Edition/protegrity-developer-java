package com.protegrity.ap.java;


public class InitializationException extends RuntimeException {

    public InitializationException(String message) {
        super("Initialization failed: " + message);
    }
}