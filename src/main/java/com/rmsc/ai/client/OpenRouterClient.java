package com.rmsc.ai.client;

import com.rmsc.ai.config.OpenRouterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Low-level reusable HTTP client for the OpenRouter API.
 */
@Component
public class OpenRouterClient {

    private static final Logger log = LoggerFactory.getLogger(OpenRouterClient.class);

    private final WebClient webClient;
    private final OpenRouterProperties properties;

    public OpenRouterClient(@Qualifier("openRouterWebClient") WebClient webClient,
                            OpenRouterProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public Map<String, Object> listModels() {
        log.info("Fetching available models from OpenRouter [baseUrl={}]", properties.getBaseUrl());
        long start = Instant.now().toEpochMilli();

        Map<String, Object> response = webClient.get()
                .uri("/models")
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("OpenRouter /models call failed. Status: {}, Body: {}",
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException(
                             "OpenRouter API error: " + ex.getMessage(), ex));
                })
                .block();

        log.info("Model list fetched in {}ms", Instant.now().toEpochMilli() - start);
        return response;
    }

    public Map<String, Object> chatCompletion(String model,
                                              List<Map<String, String>> messages,
                                              int maxTokens) {

        log.info("Direct chat completion. Model: {}, Messages: {}, MaxTokens: {}",
                model, messages.size(), maxTokens);
        long start = Instant.now().toEpochMilli();

        Map<String, Object> requestBody = Map.of(
                "model",      model,
                "messages",   messages,
                "max_tokens", maxTokens,
                "temperature", properties.getTemperature()
        );

        Map<String, Object> response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("OpenRouter chat completion failed. Status: {}, Body: {}",
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException(
                            "OpenRouter chat API error: " + ex.getMessage(), ex));
                })
                .block();

        log.info("Chat completion received in {}ms", Instant.now().toEpochMilli() - start);
        return response;
    }
}
