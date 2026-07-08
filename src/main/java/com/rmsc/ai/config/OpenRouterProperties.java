package com.rmsc.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe binding for all {@code openrouter.*} properties.
 */
@ConfigurationProperties(prefix = "openrouter")
public class OpenRouterProperties {

    private String apiKey;
    private String baseUrl;
    private String chatModel;
    private String embeddingModel;
    private double temperature = 0.3;
    private int maxTokens = 2048;
    private int connectTimeoutMs = 10_000;
    private int readTimeoutMs = 60_000;

    public OpenRouterProperties() {
    }

    // Getters and Setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getChatModel() { return chatModel; }
    public void setChatModel(String chatModel) { this.chatModel = chatModel; }

    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }

    public int getReadTimeoutMs() { return readTimeoutMs; }
    public void setReadTimeoutMs(int readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; }
}
