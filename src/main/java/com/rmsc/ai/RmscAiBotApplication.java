package com.rmsc.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * RMSC AI Bot – Application Entry Point
 *
 * <p>This is the root of the ERP AI Assistant backend. Spring Boot
 * auto-scans all beans inside {@code com.rmsc.ai} and its sub-packages.
 *
 * <p>{@code @ConfigurationPropertiesScan} guarantees that all
 * {@code @ConfigurationProperties} classes (e.g. {@code OpenRouterProperties})
 * are registered without explicit {@code @EnableConfigurationProperties}.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class RmscAiBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(RmscAiBotApplication.class, args);
    }
}
