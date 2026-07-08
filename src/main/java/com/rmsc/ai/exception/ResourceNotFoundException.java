package com.rmsc.ai.exception;

/**
 * Thrown when a requested resource cannot be found in the database.
 * Maps to HTTP 404 in the global exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
