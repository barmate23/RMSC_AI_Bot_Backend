package com.rmsc.ai.embedding;

import com.rmsc.ai.entity.EventEmbedding;
import com.rmsc.ai.entity.EventHistory;
import com.rmsc.ai.exception.EmbeddingException;
import com.rmsc.ai.repository.EventEmbeddingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EmbeddingService}.
 *
 * <p>Spring AI components (EmbeddingModel) are mocked to isolate
 * the business logic of idempotency, batch processing, and error handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmbeddingService")
class EmbeddingServiceTest {

    @Mock
    private EmbeddingModel embeddingModel;

    @Mock
    private EventSentenceGenerator sentenceGenerator;

    @Mock
    private EventEmbeddingRepository embeddingRepository;

    @InjectMocks
    private EmbeddingService embeddingService;

    private EventHistory testEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(embeddingService, "embeddingModelName", "text-embedding-3-small");

        testEvent = EventHistory.builder()
                .id(42L)
                .eventType("PRODUCTION_PLAN_SYNCED")
                .moduleName("PRODUCTION")
                .referenceId("PP1001")
                .status("SUCCESS")
                .eventTime(LocalDateTime.now())
                .build();
    }

    // =========================================================================
    // embedEvent – Happy Path
    // =========================================================================

    @Test
    @DisplayName("embedEvent should generate, call model, and persist when no embedding exists")
    void shouldEmbedEventWhenNoExistingEmbedding() {
        // Given
        String expectedSentence = "Production Plan PP1001 synchronized successfully by Planning System.";
        float[] expectedVector = {0.1f, 0.2f, 0.3f};

        given(embeddingRepository.existsByEventHistoryId(42L)).willReturn(false);
        given(sentenceGenerator.generate(testEvent)).willReturn(expectedSentence);
        given(embeddingModel.embedForResponse(anyList())).willReturn(mockEmbeddingResponse(expectedVector));
        given(embeddingRepository.save(any(EventEmbedding.class))).willAnswer(inv -> inv.getArgument(0));

        // When
        EventEmbedding result = embeddingService.embedEvent(testEvent);

        // Then
        assertThat(result.getEmbeddingText()).isEqualTo(expectedSentence);
        assertThat(result.getEmbeddingModel()).isEqualTo("text-embedding-3-small");
        assertThat(result.getEmbedding()).isEqualTo(expectedVector);

        verify(embeddingModel, times(1)).embedForResponse(anyList());
        verify(embeddingRepository, times(1)).save(any(EventEmbedding.class));
    }

    // =========================================================================
    // embedEvent – Idempotency
    // =========================================================================

    @Test
    @DisplayName("embedEvent should skip API call when embedding already exists")
    void shouldSkipEmbeddingWhenAlreadyExists() {
        // Given
        EventEmbedding existing = EventEmbedding.builder()
                .id(1L)
                .eventHistory(testEvent)
                .embeddingText("Existing sentence.")
                .embedding(new float[]{0.5f, 0.6f})
                .embeddingModel("text-embedding-3-small")
                .build();

        given(embeddingRepository.existsByEventHistoryId(42L)).willReturn(true);
        given(embeddingRepository.findByEventHistoryId(42L)).willReturn(Optional.of(existing));

        // When
        EventEmbedding result = embeddingService.embedEvent(testEvent);

        // Then
        assertThat(result).isEqualTo(existing);
        verify(embeddingModel, never()).embedForResponse(anyList());
        verify(embeddingRepository, never()).save(any());
    }

    // =========================================================================
    // embedEvent – Failure Handling
    // =========================================================================

    @Test
    @DisplayName("embedEvent should throw EmbeddingException when model returns empty response")
    void shouldThrowWhenModelReturnsEmptyResponse() {
        // Given
        given(embeddingRepository.existsByEventHistoryId(42L)).willReturn(false);
        given(sentenceGenerator.generate(testEvent)).willReturn("A sentence");

        EmbeddingResponse emptyResponse = mock(EmbeddingResponse.class);
        given(emptyResponse.getResults()).willReturn(List.of());
        given(embeddingModel.embedForResponse(anyList())).willReturn(emptyResponse);

        // When + Then
        assertThatThrownBy(() -> embeddingService.embedEvent(testEvent))
                .isInstanceOf(EmbeddingException.class)
                .hasMessageContaining("empty response");
    }

    // =========================================================================
    // embedAll – Batch processing
    // =========================================================================

    @Test
    @DisplayName("embedAll should process multiple events and count successes")
    void shouldEmbedAllEventsInBatch() {
        // Given
        EventHistory event2 = EventHistory.builder().id(43L)
                .eventType("ASN_APPROVED").moduleName("ASN")
                .referenceId("ASN450").status("SUCCESS")
                .eventTime(LocalDateTime.now()).build();

        float[] vector = {0.1f, 0.2f};
        given(embeddingRepository.existsByEventHistoryId(anyLong())).willReturn(false);
        given(sentenceGenerator.generate(any())).willReturn("A sentence");
        given(embeddingModel.embedForResponse(anyList())).willReturn(mockEmbeddingResponse(vector));
        given(embeddingRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // When
        int count = embeddingService.embedAll(List.of(testEvent, event2));

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("embedAll should continue processing if one event fails")
    void shouldContinueBatchOnIndividualFailure() {
        // Given – second event will fail the embedding
        EventHistory event2 = EventHistory.builder().id(43L)
                .eventType("ASN_APPROVED").moduleName("ASN")
                .referenceId("ASN450").status("SUCCESS")
                .eventTime(LocalDateTime.now()).build();

        float[] vector = {0.1f};
        given(embeddingRepository.existsByEventHistoryId(42L)).willReturn(false);
        given(embeddingRepository.existsByEventHistoryId(43L)).willReturn(false);
        given(sentenceGenerator.generate(testEvent)).willReturn("Sentence 1");
        given(sentenceGenerator.generate(event2)).willReturn("Sentence 2");

        // First succeeds, second throws
        given(embeddingModel.embedForResponse(List.of("Sentence 1")))
                .willReturn(mockEmbeddingResponse(vector));
        given(embeddingModel.embedForResponse(List.of("Sentence 2")))
                .willThrow(new RuntimeException("Upstream API timeout"));
        given(embeddingRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // When
        int count = embeddingService.embedAll(List.of(testEvent, event2));

        // Then – only 1 succeeded
        assertThat(count).isEqualTo(1);
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private EmbeddingResponse mockEmbeddingResponse(float[] vector) {
        Embedding embedding = mock(Embedding.class);
        given(embedding.getOutput()).willReturn(vector);

        EmbeddingResponse response = mock(EmbeddingResponse.class);
        given(response.getResults()).willReturn(List.of(embedding));
        return response;
    }
}
