package com.rmsc.ai.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Inbound DTO for a complete RAG pipeline chat request.
 */
public class ChatRequest {

    @NotBlank(message = "question must not be blank")
    private String question;

    private int topK = 10;

    private double minSimilarity = 0.5;

    public ChatRequest() {
    }

    public ChatRequest(String question, int topK, double minSimilarity) {
        this.question = question;
        this.topK = topK;
        this.minSimilarity = minSimilarity;
    }

    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }

    public double getMinSimilarity() { return minSimilarity; }
    public void setMinSimilarity(double minSimilarity) { this.minSimilarity = minSimilarity; }

    public static ChatRequestBuilder builder() {
        return new ChatRequestBuilder();
    }

    public static class ChatRequestBuilder {
        private String question;
        private int topK = 10;
        private double minSimilarity = 0.5;

        public ChatRequestBuilder question(String question) { this.question = question; return this; }
        public ChatRequestBuilder topK(int topK) { this.topK = topK; return this; }
        public ChatRequestBuilder minSimilarity(double minSimilarity) { this.minSimilarity = minSimilarity; return this; }

        public ChatRequest build() {
            return new ChatRequest(question, topK, minSimilarity);
        }
    }
}
