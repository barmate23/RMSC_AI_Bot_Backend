package com.rmsc.ai.mapper;

import com.rmsc.ai.dto.EventRequest;
import com.rmsc.ai.dto.EventResponse;
import com.rmsc.ai.entity.EventEmbedding;
import com.rmsc.ai.entity.EventHistory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stateless mapper between {@link EventHistory} entity and its DTOs.
 *
 * <p>Kept as a plain Spring {@code @Component} (no MapStruct) for clarity
 * and to avoid hidden bytecode generation issues in a learning context.
 * All mapping logic is explicit and testable.
 */
@Component
public class EventMapper {

    /**
     * Maps an inbound {@link EventRequest} to a new {@link EventHistory} entity.
     * The {@code id}, {@code createdAt}, and {@code embedding} fields are
     * left unset — they are managed by JPA and the embedding pipeline.
     *
     * @param request the API request DTO
     * @return a new, unsaved entity ready for persistence
     */
    public EventHistory toEntity(EventRequest request) {
        return EventHistory.builder()
                .eventType(request.getEventType())
                .moduleName(request.getModuleName())
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .description(request.getDescription())
                .status(request.getStatus())
                .organizationId(request.getOrganizationId())
                .userId(request.getUserId())
                .eventTime(request.getEventTime())
                .metadata(request.getMetadata())
                .createdBy(request.getCreatedBy())
                .build();
    }

    /**
     * Maps a persisted {@link EventHistory} entity to an outbound {@link EventResponse}.
     *
     * <p>The {@code embedded} flag and {@code embeddingText} are derived
     * from the lazily-loaded {@link EventEmbedding} association.
     *
     * @param entity the persisted entity (with embedding association loaded or null)
     * @return populated response DTO
     */
    public EventResponse toResponse(EventHistory entity) {
        EventEmbedding emb = entity.getEmbedding();
        return EventResponse.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .moduleName(entity.getModuleName())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .organizationId(entity.getOrganizationId())
                .userId(entity.getUserId())
                .eventTime(entity.getEventTime())
                .metadata(entity.getMetadata())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .embedded(emb != null)
                .embeddingText(emb != null ? emb.getEmbeddingText() : null)
                .build();
    }

    /**
     * Convenience overload that maps a list of entities.
     *
     * @param entities list of persisted entities
     * @return list of response DTOs
     */
    public List<EventResponse> toResponseList(List<EventHistory> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
