package com.rmsc.ai.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * JPA entity representing a single ERP business event.
 */
@Entity
@Table(name = "event_history")
public class EventHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(name = "reference_type", length = 100)
    private String referenceType;

    @Column(name = "reference_id", length = 255)
    private String referenceId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Type(JsonBinaryType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "eventHistory", cascade = CascadeType.ALL,
              fetch = FetchType.LAZY, optional = true)
    private EventEmbedding embedding;

    public EventHistory() {
    }

    public EventHistory(Long id, String eventType, String moduleName, String referenceType, String referenceId,
                        String description, String status, Long organizationId, Long userId,
                        LocalDateTime eventTime, Map<String, Object> metadata, String createdBy,
                        LocalDateTime createdAt, EventEmbedding embedding) {
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
        this.embedding = embedding;
    }

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

    public EventEmbedding getEmbedding() { return embedding; }
    public void setEmbedding(EventEmbedding embedding) { this.embedding = embedding; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventHistory that = (EventHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EventHistory{" +
                "id=" + id +
                ", eventType='" + eventType + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static EventHistoryBuilder builder() {
        return new EventHistoryBuilder();
    }

    public static class EventHistoryBuilder {
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
        private EventEmbedding embedding;

        public EventHistoryBuilder id(Long id) { this.id = id; return this; }
        public EventHistoryBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public EventHistoryBuilder moduleName(String moduleName) { this.moduleName = moduleName; return this; }
        public EventHistoryBuilder referenceType(String referenceType) { this.referenceType = referenceType; return this; }
        public EventHistoryBuilder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public EventHistoryBuilder description(String description) { this.description = description; return this; }
        public EventHistoryBuilder status(String status) { this.status = status; return this; }
        public EventHistoryBuilder organizationId(Long organizationId) { this.organizationId = organizationId; return this; }
        public EventHistoryBuilder userId(Long userId) { this.userId = userId; return this; }
        public EventHistoryBuilder eventTime(LocalDateTime eventTime) { this.eventTime = eventTime; return this; }
        public EventHistoryBuilder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
        public EventHistoryBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public EventHistoryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public EventHistoryBuilder embedding(EventEmbedding embedding) { this.embedding = embedding; return this; }

        public EventHistory build() {
            return new EventHistory(id, eventType, moduleName, referenceType, referenceId, description, status,
                    organizationId, userId, eventTime, metadata, createdBy, createdAt, embedding);
        }
    }
}
