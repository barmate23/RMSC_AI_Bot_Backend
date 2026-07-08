package com.rmsc.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Inbound DTO for a semantic vector search request.
 */
public class SearchRequest {

    @NotBlank(message = "query must not be blank")
    private String query;

    @Min(value = 1, message = "topK must be at least 1")
    @Max(value = 50, message = "topK must not exceed 50")
    private int topK = 10;

    private double minSimilarity = 0.0;

    public SearchRequest() {
    }

    public SearchRequest(String query, int topK, double minSimilarity) {
        this.query = query;
        this.topK = topK;
        this.minSimilarity = minSimilarity;
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }

    public double getMinSimilarity() { return minSimilarity; }
    public void setMinSimilarity(double minSimilarity) { this.minSimilarity = minSimilarity; }

    public static SearchRequestBuilder builder() {
        return new SearchRequestBuilder();
    }

    public static class SearchRequestBuilder {
        private String query;
        private int topK = 10;
        private double minSimilarity = 0.0;

        public SearchRequestBuilder query(String query) { this.query = query; return this; }
        public SearchRequestBuilder topK(int topK) { this.topK = topK; return this; }
        public SearchRequestBuilder minSimilarity(double minSimilarity) { this.minSimilarity = minSimilarity; return this; }

        public SearchRequest build() {
            return new SearchRequest(query, topK, minSimilarity);
        }
    }
}
