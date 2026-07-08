package com.rmsc.ai.exception;

/**
 * Thrown when the OpenRouter LLM call fails or returns an invalid response.
 * Maps to HTTP 502 in the global exception handler.
 */
public class LlmException extends RuntimeException {

    public LlmException(String message) {
        super(message);
    }

    public LlmException(String message, Throwable cause) {
        super(message, cause);
    }
}
