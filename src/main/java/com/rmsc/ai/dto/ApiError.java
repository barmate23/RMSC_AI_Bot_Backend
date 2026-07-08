package com.rmsc.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard API error envelope returned for all error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    private Map<String, String> details;

    public ApiError() {
    }

    public ApiError(int status, String error, String message, String path, LocalDateTime timestamp, Map<String, String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.details = details;
    }

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Map<String, String> getDetails() { return details; }
    public void setDetails(Map<String, String> details) { this.details = details; }

    public static ApiErrorBuilder builder() {
        return new ApiErrorBuilder();
    }

    public static class ApiErrorBuilder {
        private int status;
        private String error;
        private String message;
        private String path;
        private LocalDateTime timestamp = LocalDateTime.now();
        private Map<String, String> details;

        public ApiErrorBuilder status(int status) { this.status = status; return this; }
        public ApiErrorBuilder error(String error) { this.error = error; return this; }
        public ApiErrorBuilder message(String message) { this.message = message; return this; }
        public ApiErrorBuilder path(String path) { this.path = path; return this; }
        public ApiErrorBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public ApiErrorBuilder details(Map<String, String> details) { this.details = details; return this; }

        public ApiError build() {
            return new ApiError(status, error, message, path, timestamp, details);
        }
    }
}
