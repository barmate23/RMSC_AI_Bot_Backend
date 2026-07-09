package com.rmsc.ai.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing the vector embedding of an ERP event.
 */
@Entity
@Table(name = "event_embedding")
public class EventEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private EventHistory eventHistory;

    @Column(name = "embedding_text", nullable = false, columnDefinition = "TEXT")
    private String embeddingText;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.OTHER)
    @Column(name = "embedding", nullable = false, columnDefinition = "vector(1536)")
    private float[] embedding;

    @Column(name = "embedding_model", nullable = false, length = 200)
    private String embeddingModel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public EventEmbedding() {
    }

    public EventEmbedding(Long id, EventHistory eventHistory, String embeddingText, float[] embedding,
                          String embeddingModel, LocalDateTime createdAt) {
        this.id = id;
        this.eventHistory = eventHistory;
        this.embeddingText = embeddingText;
        this.embedding = embedding;
        this.embeddingModel = embeddingModel;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EventHistory getEventHistory() { return eventHistory; }
    public void setEventHistory(EventHistory eventHistory) { this.eventHistory = eventHistory; }

    public String getEmbeddingText() { return embeddingText; }
    public void setEmbeddingText(String embeddingText) { this.embeddingText = embeddingText; }

    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }

    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEmbedding that = (EventEmbedding) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EventEmbedding{" +
                "id=" + id +
                ", embeddingText='" + embeddingText + '\'' +
                ", embeddingModel='" + embeddingModel + '\'' +
                '}';
    }

    public static EventEmbeddingBuilder builder() {
        return new EventEmbeddingBuilder();
    }

    public static class EventEmbeddingBuilder {
        private Long id;
        private EventHistory eventHistory;
        private String embeddingText;
        private float[] embedding;
        private String embeddingModel;
        private LocalDateTime createdAt;

        public EventEmbeddingBuilder id(Long id) { this.id = id; return this; }
        public EventEmbeddingBuilder eventHistory(EventHistory eventHistory) { this.eventHistory = eventHistory; return this; }
        public EventEmbeddingBuilder embeddingText(String embeddingText) { this.embeddingText = embeddingText; return this; }
        public EventEmbeddingBuilder embedding(float[] embedding) { this.embedding = embedding; return this; }
        public EventEmbeddingBuilder embeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; return this; }
        public EventEmbeddingBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EventEmbedding build() {
            return new EventEmbedding(id, eventHistory, embeddingText, embedding, embeddingModel, createdAt);
        }
    }
}
