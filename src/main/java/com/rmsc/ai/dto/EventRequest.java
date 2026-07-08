package com.rmsc.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Inbound DTO for creating a new ERP event.
 */
public class EventRequest {

    @NotBlank(message = "eventType is mandatory")
    @Size(max = 100, message = "eventType must not exceed 100 characters")
    private String eventType;

    @NotBlank(message = "moduleName is mandatory")
    @Size(max = 100, message = "moduleName must not exceed 100 characters")
    private String moduleName;

    @Size(max = 100)
    private String referenceType;

    @Size(max = 255)
    private String referenceId;

    private String description;

    @Size(max = 50)
    private String status;

    private Long organizationId;

    private Long userId;

    @NotNull(message = "eventTime is mandatory")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;

    private Map<String, Object> metadata;

    @Size(max = 100)
    private String createdBy;

    public EventRequest() {
    }

    public EventRequest(String eventType, String moduleName, String referenceType, String referenceId,
                        String description, String status, Long organizationId, Long userId,
                        LocalDateTime eventTime, Map<String, Object> metadata, String createdBy) {
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
    }

    // Getters and Setters
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

    public static EventRequestBuilder builder() {
        return new EventRequestBuilder();
    }

    public static class EventRequestBuilder {
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

        public EventRequestBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public EventRequestBuilder moduleName(String moduleName) { this.moduleName = moduleName; return this; }
        public EventRequestBuilder referenceType(String referenceType) { this.referenceType = referenceType; return this; }
        public EventRequestBuilder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public EventRequestBuilder description(String description) { this.description = description; return this; }
        public EventRequestBuilder status(String status) { this.status = status; return this; }
        public EventRequestBuilder organizationId(Long organizationId) { this.organizationId = organizationId; return this; }
        public EventRequestBuilder userId(Long userId) { this.userId = userId; return this; }
        public EventRequestBuilder eventTime(LocalDateTime eventTime) { this.eventTime = eventTime; return this; }
        public EventRequestBuilder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
        public EventRequestBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }

        public EventRequest build() {
            return new EventRequest(eventType, moduleName, referenceType, referenceId, description, status,
                    organizationId, userId, eventTime, metadata, createdBy);
        }
    }
}
