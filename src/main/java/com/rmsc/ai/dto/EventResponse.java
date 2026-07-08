package com.rmsc.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Outbound DTO representing a persisted ERP event.
 */
public class EventResponse {

    private Long id;
    private String eventType;
    private String moduleName;
    private String referenceType;
    private String referenceId;
    private String description;
    private String status;
    private Long organizationId;
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;

    private Map<String, Object> metadata;
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private boolean embedded;
    private String embeddingText;

    public EventResponse() {
    }

    public EventResponse(Long id, String eventType, String moduleName, String referenceType, String referenceId,
                         String description, String status, Long organizationId, Long userId,
                         LocalDateTime eventTime, Map<String, Object> metadata, String createdBy,
                         LocalDateTime createdAt, boolean embedded, String embeddingText) {
        this.id = id;
        this.eventType = eventType;
        this.moduleName = moduleName;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.description = description;
        this.status = status;
        this.organizationId = organizationId;
        this.userId = userId;
        this.eventTime = eventTime;
        this.metadata = metadata;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.embedded = embedded;
        this.embeddingText = embeddingText;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isEmbedded() { return embedded; }
    public void setEmbedded(boolean embedded) { this.embedded = embedded; }

    public String getEmbeddingText() { return embeddingText; }
    public void setEmbeddingText(String embeddingText) { this.embeddingText = embeddingText; }

    public static EventResponseBuilder builder() {
        return new EventResponseBuilder();
    }

    public static class EventResponseBuilder {
        private Long id;
        private String eventType;
        private String moduleName;
        private String referenceType;
        private String referenceId;
        private String description;
        private String status;
        private Long organizationId;
        private Long userId;
        private LocalDateTime eventTime;
        private Map<String, Object> metadata;
        private String createdBy;
        private LocalDateTime createdAt;
        private boolean embedded;
        private String embeddingText;

        public EventResponseBuilder id(Long id) { this.id = id; return this; }
        public EventResponseBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public EventResponseBuilder moduleName(String moduleName) { this.moduleName = moduleName; return this; }
        public EventResponseBuilder referenceType(String referenceType) { this.referenceType = referenceType; return this; }
        public EventResponseBuilder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public EventResponseBuilder description(String description) { this.description = description; return this; }
        public EventResponseBuilder status(String status) { this.status = status; return this; }
        public EventResponseBuilder organizationId(Long organizationId) { this.organizationId = organizationId; return this; }
        public EventResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public EventResponseBuilder eventTime(LocalDateTime eventTime) { this.eventTime = eventTime; return this; }
        public EventResponseBuilder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
        public EventResponseBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public EventResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public EventResponseBuilder embedded(boolean embedded) { this.embedded = embedded; return this; }
        public EventResponseBuilder embeddingText(String embeddingText) { this.embeddingText = embeddingText; return this; }

        public EventResponse build() {
            return new EventResponse(id, eventType, moduleName, referenceType, referenceId, description, status,
                    organizationId, userId, eventTime, metadata, createdBy, createdAt, embedded, embeddingText);
        }
    }
}
