package com.rmsc.ai.prompt;

import com.rmsc.ai.dto.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Constructs the RAG prompt sent to the LLM.
 */
@Component
public class RagPromptBuilder {

    private static final Logger log = LoggerFactory.getLogger(RagPromptBuilder.class);

    private static final String PROMPT_TEMPLATE = """
            You are an ERP AI Assistant with deep knowledge of production planning,
            material management, procurement, and inventory operations.

            Answer the user's question using ONLY the provided ERP event history context below. 
            If the context does not contain any relevant events or information to help answer the query, clearly state:
            "This information cannot be determined from the available ERP event history."

            Always answer the question as fully as possible using whatever events are present in the context. Do not refuse to answer if the context contains relevant but incomplete information; instead, summarize only the events that are available.

            Do not guess, invent, or use knowledge outside the provided context.
            Be concise, factual, and professional.

            ---
            ERP EVENT HISTORY CONTEXT:
            {context}
            ---

            QUESTION:
            {question}

            ANSWER:
            """;

    public Prompt buildRagPrompt(String question, List<SearchResult> contextEvents) {
        String contextBlock = buildContextBlock(contextEvents);

        log.info("Building RAG prompt. Question length: {}, Context events: {}, Context length: {}",
                question.length(), contextEvents.size(), contextBlock.length());

        PromptTemplate template = new PromptTemplate(PROMPT_TEMPLATE);
        Prompt prompt = template.create(Map.of(
                "context",  contextBlock,
                "question", question
        ));

        log.debug("Final prompt size: {} chars", prompt.toString().length());
        return prompt;
    }

    private String buildContextBlock(List<SearchResult> events) {
        if (events == null || events.isEmpty()) {
            return "No relevant ERP events found in the knowledge base.";
        }

        AtomicInteger index = new AtomicInteger(1);
        StringBuilder sb = new StringBuilder();

        for (SearchResult event : events) {
            sb.append(String.format("""
                    [Event %d] (Relevance: %.2f%%)
                    Statement: %s
                    Module: %s | Event Type: %s
                    Reference: %s %s | Status: %s
                    Actor: %s | Time: %s
                    """,
                    index.getAndIncrement(),
                    event.getSimilarity() * 100,
                    event.getEmbeddingText(),
                    safeStr(event.getModuleName()),
                    safeStr(event.getEventType()),
                    safeStr(event.getReferenceType()),
                    safeStr(event.getReferenceId()),
                    safeStr(event.getStatus()),
                    safeStr(event.getCreatedBy()),
                    event.getEventTime() != null ? event.getEventTime().toString() : "N/A"
            ));
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    private String safeStr(String value) {
        return (value != null && !value.isBlank()) ? value : "N/A";
    }
}
