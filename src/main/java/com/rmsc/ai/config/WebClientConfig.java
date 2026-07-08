package com.rmsc.ai.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Application-wide web infrastructure configuration.
 */
@Configuration
public class WebClientConfig {

    private final OpenRouterProperties openRouterProperties;

    // Standard constructor instead of Lombok RequiredArgsConstructor
    public WebClientConfig(OpenRouterProperties openRouterProperties) {
        this.openRouterProperties = openRouterProperties;
    }

    @Bean("openRouterWebClient")
    public WebClient openRouterWebClient() {

        HttpClient reactorNettyHttpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, openRouterProperties.getConnectTimeoutMs())
                .responseTimeout(Duration.ofMillis(openRouterProperties.getReadTimeoutMs()))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(
                                openRouterProperties.getReadTimeoutMs(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(
                                openRouterProperties.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(openRouterProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " + openRouterProperties.getApiKey())
                .clientConnector(new ReactorClientHttpConnector(reactorNettyHttpClient))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            return Mono.just(clientResponse);
        });
    }
}
