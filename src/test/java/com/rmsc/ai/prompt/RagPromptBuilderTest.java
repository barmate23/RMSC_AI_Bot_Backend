package com.rmsc.ai.prompt;

import com.rmsc.ai.dto.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RagPromptBuilder}.
 *
 * <p>Verifies that the prompt includes the question, context events,
 * similarity scores, and the correct system instructions.
 */
@DisplayName("RagPromptBuilder")
class RagPromptBuilderTest {

    private RagPromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new RagPromptBuilder();
    }

    @Test
    @DisplayName("should produce a Prompt containing the user question")
    void shouldContainUserQuestion() {
        String question = "What happened to Production Plan PP1001?";
        Prompt prompt = promptBuilder.buildRagPrompt(question, List.of());
        assertThat(prompt.toString()).contains(question);
    }

    @Test
    @DisplayName("should include event data in context block")
    void shouldIncludeEventContextInPrompt() {
        SearchResult event = SearchResult.builder()
                .eventId(1L)
                .similarity(0.943)
                .embeddingText("Production Plan PP1001 synchronized successfully by Planning System.")
                .eventType("PRODUCTION_PLAN_SYNCED")
                .moduleName("PRODUCTION")
                .referenceType("PRODUCTION_PLAN")
                .referenceId("PP1001")
                .status("SUCCESS")
                .eventTime(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();

        Prompt prompt = promptBuilder.buildRagPrompt("What is the status of PP1001?", List.of(event));
        String promptText = prompt.toString();

        assertThat(promptText).contains("PP1001");
        assertThat(promptText).contains("PRODUCTION_PLAN_SYNCED");
        assertThat(promptText).contains("synchronized successfully");
        assertThat(promptText).contains("94.30%"); // 0.943 * 100 formatted
    }

    @Test
    @DisplayName("should include system instruction about answering only from context")
    void shouldIncludeSystemInstruction() {
        Prompt prompt = promptBuilder.buildRagPrompt("tell me something", List.of());
        assertThat(prompt.toString()).contains("Answer ONLY from the provided ERP event history context");
    }

    @Test
    @DisplayName("should handle empty context gracefully")
    void shouldHandleEmptyContextGracefully() {
        Prompt prompt = promptBuilder.buildRagPrompt("Any question?", Collections.emptyList());
        assertThat(prompt.toString()).contains("No relevant ERP events found");
    }

    @Test
    @DisplayName("should number multiple events sequentially")
    void shouldNumberMultipleEventsSequentially() {
        SearchResult e1 = buildSearchResult(1L, "Event one sentence.", 0.9);
        SearchResult e2 = buildSearchResult(2L, "Event two sentence.", 0.85);
        SearchResult e3 = buildSearchResult(3L, "Event three sentence.", 0.75);

        Prompt prompt = promptBuilder.buildRagPrompt("Question?", List.of(e1, e2, e3));
        String text = prompt.toString();

        assertThat(text).contains("[Event 1]");
        assertThat(text).contains("[Event 2]");
        assertThat(text).contains("[Event 3]");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private SearchResult buildSearchResult(Long id, String text, double similarity) {
        return SearchResult.builder()
                .eventId(id)
                .similarity(similarity)
                .embeddingText(text)
                .eventType("TEST_EVENT")
                .moduleName("TEST")
                .referenceId("REF" + id)
                .status("SUCCESS")
                .eventTime(LocalDateTime.now())
                .build();
    }
}
