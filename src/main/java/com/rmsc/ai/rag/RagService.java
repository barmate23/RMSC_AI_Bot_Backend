package com.rmsc.ai.rag;

import com.rmsc.ai.config.OpenRouterProperties;
import com.rmsc.ai.dto.ChatRequest;
import com.rmsc.ai.dto.ChatResponse;
import com.rmsc.ai.dto.SearchRequest;
import com.rmsc.ai.dto.SearchResult;
import com.rmsc.ai.exception.LlmException;
import com.rmsc.ai.prompt.RagPromptBuilder;
import com.rmsc.ai.vector.VectorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Orchestrates the complete RAG (Retrieval-Augmented Generation) pipeline.
 */
@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    private final VectorStoreService    vectorStoreService;
    private final RagPromptBuilder      promptBuilder;
    private final ChatModel             chatModel;
    private final OpenRouterProperties  openRouterProperties;

    public RagService(VectorStoreService vectorStoreService,
                      RagPromptBuilder promptBuilder,
                      ChatModel chatModel,
                      OpenRouterProperties openRouterProperties) {
        this.vectorStoreService = vectorStoreService;
        this.promptBuilder = promptBuilder;
        this.chatModel = chatModel;
        this.openRouterProperties = openRouterProperties;
    }

    public ChatResponse chat(ChatRequest request) {

        long pipelineStart = Instant.now().toEpochMilli();
        log.info("RAG pipeline started for question: '{}'", request.getQuestion());

        List<SearchResult> contextEvents;
        Prompt prompt;

        if (isConversationalQuery(request.getQuestion())) {
            log.info("Question classified as a Conversational Query. Bypassing database/vector search.");
            contextEvents = List.of();
            prompt = promptBuilder.buildConversationalPrompt(request.getQuestion());
        } else {
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(request.getQuestion())
                    .topK(request.getTopK())
                    .minSimilarity(request.getMinSimilarity())
                    .build();

            contextEvents = vectorStoreService.search(searchRequest);
            log.info("Retrieved {} context events for RAG prompt.", contextEvents.size());
            prompt = promptBuilder.buildRagPrompt(request.getQuestion(), contextEvents);
        }

        long llmStart = Instant.now().toEpochMilli();
        log.info("Sending prompt to LLM [model={}]", openRouterProperties.getChatModel());

        org.springframework.ai.chat.model.ChatResponse aiResponse = callLlm(prompt, request.getQuestion());

        long llmLatency = Instant.now().toEpochMilli() - llmStart;
        log.info("LLM responded in {}ms", llmLatency);

        String answer = extractAnswer(aiResponse);

        Integer promptTokens = null;
        Integer completionTokens = null;

        if (aiResponse.getMetadata() != null && aiResponse.getMetadata().getUsage() != null) {
            promptTokens     = aiResponse.getMetadata().getUsage().getPromptTokens() != null
                             ? aiResponse.getMetadata().getUsage().getPromptTokens().intValue() : null;
            completionTokens = aiResponse.getMetadata().getUsage().getCompletionTokens() != null
                             ? aiResponse.getMetadata().getUsage().getCompletionTokens().intValue() : null;
        }

        long totalLatency = Instant.now().toEpochMilli() - pipelineStart;

        log.info("RAG pipeline complete. TotalLatency: {}ms, PromptTokens: {}, CompletionTokens: {}",
                totalLatency, promptTokens, completionTokens);

        return ChatResponse.builder()
                .question(request.getQuestion())
                .answer(answer)
                .contextEvents(contextEvents)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalLatencyMs(totalLatency)
                .modelUsed(openRouterProperties.getChatModel())
                .build();
    }

    private org.springframework.ai.chat.model.ChatResponse callLlm(Prompt prompt, String question) {
        try {
            return chatModel.call(prompt);
        } catch (Exception e) {
            String msg = "LLM call failed for question: '" + question + "'. Cause: " + e.getMessage();
            log.error(msg, e);
            throw new LlmException(msg, e);
        }
    }

    private String extractAnswer(org.springframework.ai.chat.model.ChatResponse response) {
        try {
            return response.getResult().getOutput().getText();
        } catch (Exception e) {
            log.warn("Could not extract answer text from LLM response: {}", e.getMessage());
            return "Unable to extract answer from LLM response.";
        }
    }

    private boolean isConversationalQuery(String question) {
        if (question == null) return false;
        String clean = question.trim().toLowerCase();
        
        // Remove trailing punctuation for comparison
        clean = clean.replaceAll("[^a-zA-Z0-9\\s]", "");
        
        // Match common greetings
        if (clean.matches("^(hello|hi|hey|greetings|hola|hey there|good morning|good afternoon|good evening|sup|yo|hi there)$")) {
            return true;
        }
        
        // Check for common non-search questions
        if (clean.contains("who are you") || 
            clean.contains("what is your name") || 
            clean.contains("how are you") || 
            clean.contains("tell me about yourself") ||
            clean.matches("^(thanks|thank you|thank you so much|great|awesome|ok|okay)$")) {
            return true;
        }
        
        return false;
    }
}
