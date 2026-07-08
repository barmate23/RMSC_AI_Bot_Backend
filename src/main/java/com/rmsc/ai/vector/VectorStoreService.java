package com.rmsc.ai.vector;

import com.rmsc.ai.dto.SearchRequest;
import com.rmsc.ai.dto.SearchResult;
import com.rmsc.ai.embedding.EmbeddingService;
import com.rmsc.ai.entity.EventHistory;
import com.rmsc.ai.repository.EventEmbeddingRepository;
import com.rmsc.ai.repository.EventHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Performs semantic similarity search against the pgvector store.
 */
@Service
public class VectorStoreService {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreService.class);

    private final EmbeddingService         embeddingService;
    private final EventEmbeddingRepository embeddingRepository;
    private final EventHistoryRepository   eventHistoryRepository;

    public VectorStoreService(EmbeddingService embeddingService,
                              EventEmbeddingRepository embeddingRepository,
                              EventHistoryRepository eventHistoryRepository) {
        this.embeddingService = embeddingService;
        this.embeddingRepository = embeddingRepository;
        this.eventHistoryRepository = eventHistoryRepository;
    }

    @Transactional(readOnly = true)
    public List<SearchResult> search(SearchRequest request) {

        log.info("Semantic search initiated. Query: '{}', topK: {}, minSimilarity: {}",
                request.getQuery(), request.getTopK(), request.getMinSimilarity());

        long embedStart = Instant.now().toEpochMilli();
        float[] queryVector = embeddingService.embedQuery(request.getQuery());
        log.info("Query embedding completed in {}ms", Instant.now().toEpochMilli() - embedStart);

        String vectorLiteral = toVectorLiteral(queryVector);

        long searchStart = Instant.now().toEpochMilli();
        List<Object[]> rawRows = embeddingRepository.findTopKSimilar(
                vectorLiteral,
                request.getTopK(),
                request.getMinSimilarity());
        log.info("Vector search returned {} results in {}ms",
                rawRows.size(), Instant.now().toEpochMilli() - searchStart);

        List<SearchResult> results = rawRows.stream()
                .map(this::mapRowToSearchResult)
                .toList();

        log.info("Semantic search complete. Returned {} results for query: '{}'",
                results.size(), request.getQuery());
        return results;
    }

    private String toVectorLiteral(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private SearchResult mapRowToSearchResult(Object[] row) {
        Long   eventId       = ((Number) row[1]).longValue();
        String embeddingText = (String) row[2];
        double similarity    = ((Number) row[5]).doubleValue();

        EventHistory event = eventHistoryRepository.findById(eventId).orElse(null);

        SearchResult.SearchResultBuilder builder = SearchResult.builder()
                .eventId(eventId)
                .similarity(Math.round(similarity * 10000.0) / 10000.0)
                .embeddingText(embeddingText);

        if (event != null) {
            builder.eventType(event.getEventType())
                   .moduleName(event.getModuleName())
                   .referenceType(event.getReferenceType())
                   .referenceId(event.getReferenceId())
                   .status(event.getStatus())
                   .eventTime(event.getEventTime());
        }

        return builder.build();
    }
}
