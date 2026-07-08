package com.rmsc.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Outbound DTO for a single semantic search result.
 */
public class SearchResult {

    private Long eventId;
    private double similarity;
    private String embeddingText;
    private String eventType;
    private String moduleName;
    private String referenceType;
    private String referenceId;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;

    public SearchResult() {
    }

    public SearchResult(Long eventId, double similarity, String embeddingText, String eventType, String moduleName,
                        String referenceType, String referenceId, String status, LocalDateTime eventTime) {
        this.eventId = eventId;
        this.similarity = similarity;
        this.embeddingText = embeddingText;
        this.eventType = eventType;
        this.moduleName = moduleName;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.status = status;
        this.eventTime = eventTime;
    }

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public double getSimilarity() { return similarity; }
    public void setSimilarity(double similarity) { this.similarity = similarity; }

    public String getEmbeddingText() { return embeddingText; }
    public void setEmbeddingText(String embeddingText) { this.embeddingText = embeddingText; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public static SearchResultBuilder builder() {
        return new SearchResultBuilder();
    }

    public static class SearchResultBuilder {
        private Long eventId;
        private double similarity;
        private String embeddingText;
        private String eventType;
        private String moduleName;
        private String referenceType;
        private String referenceId;
        private String status;
        private LocalDateTime eventTime;

        public SearchResultBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public SearchResultBuilder similarity(double similarity) { this.similarity = similarity; return this; }
        public SearchResultBuilder embeddingText(String embeddingText) { this.embeddingText = embeddingText; return this; }
        public SearchResultBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public SearchResultBuilder moduleName(String moduleName) { this.moduleName = moduleName; return this; }
        public SearchResultBuilder referenceType(String referenceType) { this.referenceType = referenceType; return this; }
        public SearchResultBuilder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public SearchResultBuilder status(String status) { this.status = status; return this; }
        public SearchResultBuilder eventTime(LocalDateTime eventTime) { this.eventTime = eventTime; return this; }

        public SearchResult build() {
            return new SearchResult(eventId, similarity, embeddingText, eventType, moduleName, referenceType,
                    referenceId, status, eventTime);
        }
    }
}
