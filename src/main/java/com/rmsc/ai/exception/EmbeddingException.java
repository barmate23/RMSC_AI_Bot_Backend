package com.rmsc.ai.exception;

/**
 * Thrown when the Spring AI EmbeddingModel call fails,
 * returns an empty result, or produces an invalid vector.
 * Maps to HTTP 502 in the global exception handler.
 */
public class EmbeddingException extends RuntimeException {

    public EmbeddingException(String message) {
        super(message);
    }

    public EmbeddingException(String message, Throwable cause) {
        super(message, cause);
    }
}
