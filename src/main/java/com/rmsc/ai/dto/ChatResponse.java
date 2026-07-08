package com.rmsc.ai.dto;

import java.util.List;

/**
 * Outbound DTO for the RAG chat response.
 */
public class ChatResponse {

    private String question;
    private String answer;
    private List<SearchResult> contextEvents;
    private Integer promptTokens;
    private Integer completionTokens;
    private long totalLatencyMs;
    private String modelUsed;

    public ChatResponse() {
    }

    public ChatResponse(String question, String answer, List<SearchResult> contextEvents, Integer promptTokens,
                        Integer completionTokens, long totalLatencyMs, String modelUsed) {
        this.question = question;
        this.answer = answer;
        this.contextEvents = contextEvents;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalLatencyMs = totalLatencyMs;
        this.modelUsed = modelUsed;
    }

    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<SearchResult> getContextEvents() { return contextEvents; }
    public void setContextEvents(List<SearchResult> contextEvents) { this.contextEvents = contextEvents; }

    public Integer getPromptTokens() { return promptTokens; }
    public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }

    public Integer getCompletionTokens() { return completionTokens; }
    public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }

    public long getTotalLatencyMs() { return totalLatencyMs; }
    public void setTotalLatencyMs(long totalLatencyMs) { this.totalLatencyMs = totalLatencyMs; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public static ChatResponseBuilder builder() {
        return new ChatResponseBuilder();
    }

    public static class ChatResponseBuilder {
        private String question;
        private String answer;
        private List<SearchResult> contextEvents;
        private Integer promptTokens;
        private Integer completionTokens;
        private long totalLatencyMs;
        private String modelUsed;

        public ChatResponseBuilder question(String question) { this.question = question; return this; }
        public ChatResponseBuilder answer(String answer) { this.answer = answer; return this; }
        public ChatResponseBuilder contextEvents(List<SearchResult> contextEvents) { this.contextEvents = contextEvents; return this; }
        public ChatResponseBuilder promptTokens(Integer promptTokens) { this.promptTokens = promptTokens; return this; }
        public ChatResponseBuilder completionTokens(Integer completionTokens) { this.completionTokens = completionTokens; return this; }
        public ChatResponseBuilder totalLatencyMs(long totalLatencyMs) { this.totalLatencyMs = totalLatencyMs; return this; }
        public ChatResponseBuilder modelUsed(String modelUsed) { this.modelUsed = modelUsed; return this; }

        public ChatResponse build() {
            return new ChatResponse(question, answer, contextEvents, promptTokens, completionTokens, totalLatencyMs, modelUsed);
        }
    }
}
