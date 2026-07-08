package com.rmsc.ai.vector;

import com.rmsc.ai.dto.SearchRequest;
import com.rmsc.ai.dto.SearchResult;
import com.rmsc.ai.embedding.EmbeddingService;
import com.rmsc.ai.entity.EventHistory;
import com.rmsc.ai.repository.EventEmbeddingRepository;
import com.rmsc.ai.repository.EventHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link VectorStoreService}.
 *
 * <p>The pgvector native query is mocked; tests verify that the service
 * correctly maps raw database rows to typed {@link SearchResult} DTOs,
 * applies similarity scores, and enriches with event metadata.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VectorStoreService")
class VectorStoreServiceTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private EventEmbeddingRepository embeddingRepository;

    @Mock
    private EventHistoryRepository eventHistoryRepository;

    @InjectMocks
    private VectorStoreService vectorStoreService;

    private SearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        searchRequest = SearchRequest.builder()
                .query("What happened to production plan PP1001?")
                .topK(5)
                .minSimilarity(0.5)
                .build();
    }

    @Test
    @DisplayName("should return ordered search results with similarity scores")
    void shouldReturnSearchResultsWithSimilarityScores() {
        // Given
        float[] queryVector = {0.1f, 0.2f, 0.3f};
        given(embeddingService.embedQuery(anyString())).willReturn(queryVector);

        EventHistory event = EventHistory.builder()
                .id(1L)
                .eventType("PRODUCTION_PLAN_SYNCED")
                .moduleName("PRODUCTION")
                .referenceType("PRODUCTION_PLAN")
                .referenceId("PP1001")
                .status("SUCCESS")
                .eventTime(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();

        Object[] row = {
                1L,                    // ee.id
                1L,                    // ee.event_id
                "Production Plan PP1001 synchronized successfully by Planning System.", // embedding_text
                "text-embedding-3-small",  // embedding_model
                LocalDateTime.now(),       // created_at
                0.943                      // similarity
        };

        given(embeddingRepository.findTopKSimilar(anyString(), eq(5), eq(0.5))).willReturn(List.of(new Object[][]{row}));
        given(eventHistoryRepository.findById(1L)).willReturn(Optional.of(event));

        // When
        List<SearchResult> results = vectorStoreService.search(searchRequest);

        // Then
        assertThat(results).hasSize(1);
        SearchResult result = results.getFirst();
        assertThat(result.getEventId()).isEqualTo(1L);
        assertThat(result.getSimilarity()).isEqualTo(0.943);
        assertThat(result.getEmbeddingText()).contains("PP1001");
        assertThat(result.getEventType()).isEqualTo("PRODUCTION_PLAN_SYNCED");
        assertThat(result.getModuleName()).isEqualTo("PRODUCTION");
    }

    @Test
    @DisplayName("should return empty list when no similar events found")
    void shouldReturnEmptyListWhenNoResults() {
        // Given
        given(embeddingService.embedQuery(anyString())).willReturn(new float[]{0.1f});
        given(embeddingRepository.findTopKSimilar(anyString(), anyInt(), anyDouble()))
                .willReturn(List.of());

        // When
        List<SearchResult> results = vectorStoreService.search(searchRequest);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should gracefully handle missing event in event_history")
    void shouldHandleMissingEventGracefully() {
        // Given – event_id in embedding has no matching event_history row
        float[] queryVector = {0.1f};
        given(embeddingService.embedQuery(anyString())).willReturn(queryVector);

        Object[] row = {
                99L, 999L, "Some embedding text", "model-name", LocalDateTime.now(), 0.8
        };
        given(embeddingRepository.findTopKSimilar(anyString(), anyInt(), anyDouble()))
                .willReturn(List.of(new Object[][]{row}));
        given(eventHistoryRepository.findById(999L)).willReturn(Optional.empty());

        // When
        List<SearchResult> results = vectorStoreService.search(searchRequest);

        // Then – result is returned even without event metadata
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getEventId()).isEqualTo(999L);
        assertThat(results.getFirst().getEventType()).isNull();
    }
}
