package com.rmsc.ai.embedding;

import com.rmsc.ai.entity.EventEmbedding;
import com.rmsc.ai.entity.EventHistory;
import com.rmsc.ai.exception.EmbeddingException;
import com.rmsc.ai.repository.EventEmbeddingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Orchestrates the full embedding pipeline for a single ERP event.
 */
@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);

    private final EmbeddingModel           embeddingModel;
    private final EventSentenceGenerator   sentenceGenerator;
    private final EventEmbeddingRepository embeddingRepository;

    @Value("${spring.ai.openai.embedding.options.model:text-embedding-3-small}")
    private String embeddingModelName;

    public EmbeddingService(EmbeddingModel embeddingModel,
                            EventSentenceGenerator sentenceGenerator,
                            EventEmbeddingRepository embeddingRepository) {
        this.embeddingModel = embeddingModel;
        this.sentenceGenerator = sentenceGenerator;
        this.embeddingRepository = embeddingRepository;
    }

    @Transactional
    public EventEmbedding embedEvent(EventHistory event) {

        if (embeddingRepository.existsByEventHistoryId(event.getId())) {
            log.info("Embedding already exists for event [id={}], skipping.", event.getId());
            return embeddingRepository.findByEventHistoryId(event.getId()).orElseThrow();
        }

        String sentence = sentenceGenerator.generate(event);
        log.info("Generated embedding sentence for event [id={}]: {}", event.getId(), sentence);

        long startTime = Instant.now().toEpochMilli();
        float[] vector = callEmbeddingModel(sentence, event.getId());
        long elapsed = Instant.now().toEpochMilli() - startTime;

        log.info("Embedding generated for event [id={}] in {}ms. Dimensions: {}",
                event.getId(), elapsed, vector.length);

        EventEmbedding embedding = EventEmbedding.builder()
                .eventHistory(event)
                .embeddingText(sentence)
                .embedding(vector)
                .embeddingModel(embeddingModelName)
                .build();

        return embeddingRepository.save(embedding);
    }

    @Transactional
    public int embedAll(List<EventHistory> events) {
        int created = 0;
        for (EventHistory event : events) {
            try {
                embedEvent(event);
                created++;
            } catch (Exception ex) {
                log.error("Failed to embed event [id={}]: {}", event.getId(), ex.getMessage(), ex);
            }
        }
        log.info("Batch embedding complete. Created: {}, Total: {}", created, events.size());
        return created;
    }

    public float[] embedQuery(String text) {
        log.debug("Embedding query text: '{}'", text);
        long start = Instant.now().toEpochMilli();
        float[] vector = callEmbeddingModel(text, null);
        log.debug("Query embedding generated in {}ms", Instant.now().toEpochMilli() - start);
        return vector;
    }

    private float[] callEmbeddingModel(String text, Long eventId) {
        try {
            log.debug("Calling EmbeddingModel. EventId: {}, TextLength: {}", eventId, text.length());
            EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));

            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                throw new EmbeddingException(
                        "EmbeddingModel returned empty response for event id=" + eventId);
            }

            float[] vector = response.getResults().getFirst().getOutput();

            if (vector == null || vector.length == 0) {
                throw new EmbeddingException(
                        "EmbeddingModel returned null/empty vector for event id=" + eventId);
            }

            return vector;

        } catch (EmbeddingException e) {
            throw e;
        } catch (Exception e) {
            String msg = String.format(
                    "EmbeddingModel API call failed for event id=%s: %s", eventId, e.getMessage());
            log.error(msg, e);
            throw new EmbeddingException(msg, e);
        }
    }
}
