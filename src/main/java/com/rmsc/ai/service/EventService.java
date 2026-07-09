package com.rmsc.ai.service;

import com.rmsc.ai.dto.EventRequest;
import com.rmsc.ai.dto.EventResponse;
import com.rmsc.ai.embedding.EmbeddingService;
import com.rmsc.ai.entity.EventEmbedding;
import com.rmsc.ai.entity.EventHistory;
import com.rmsc.ai.exception.ResourceNotFoundException;
import com.rmsc.ai.mapper.EventMapper;
import com.rmsc.ai.repository.EventHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business service for ERP event management.
 */
@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventHistoryRepository eventHistoryRepository;
    private final EmbeddingService       embeddingService;
    private final EventMapper            eventMapper;

    public EventService(EventHistoryRepository eventHistoryRepository,
                        EmbeddingService embeddingService,
                        EventMapper eventMapper) {
        this.eventHistoryRepository = eventHistoryRepository;
        this.embeddingService = embeddingService;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        log.info("Creating new ERP event. Type: {}, Module: {}, Reference: {}",
                request.getEventType(), request.getModuleName(), request.getReferenceId());

        EventHistory entity = eventMapper.toEntity(request);
        EventHistory saved  = eventHistoryRepository.save(entity);

        log.info("ERP event created with id={}", saved.getId());

        try {
            // Automatically generate and persist embedding on create
            EventEmbedding embedding = embeddingService.embedEvent(saved);
            saved.setEmbedding(embedding);
            log.info("Successfully auto-embedded new event id={}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to automatically generate embedding for event id={}: {}",
                    saved.getId(), e.getMessage(), e);
        }

        return eventMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        EventHistory event = findEventOrThrow(id);
        return eventMapper.toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventMapper.toResponseList(eventHistoryRepository.findAll());
    }

    @Transactional
    public EventResponse embedEvent(Long id) {
        log.info("Embedding requested for event id={}", id);
        EventHistory event = findEventOrThrow(id);
        EventEmbedding embedding = embeddingService.embedEvent(event);
        event.setEmbedding(embedding);
        log.info("Embedding complete for event id={}. Text: '{}'", id, embedding.getEmbeddingText());
        return eventMapper.toResponse(event);
    }

    @Transactional
    public int embedAllPendingEvents() {
        List<EventHistory> pending = eventHistoryRepository.findAllWithoutEmbedding();
        log.info("Batch embedding: {} events pending.", pending.size());
        int created = embeddingService.embedAll(pending);
        log.info("Batch embedding complete. Created {} new embeddings.", created);
        return created;
    }

    private EventHistory findEventOrThrow(Long id) {
        return eventHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EventHistory not found with id=" + id));
    }
}
