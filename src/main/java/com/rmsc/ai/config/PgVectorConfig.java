package com.rmsc.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * pgvector type registration.
 */
@Configuration
public class PgVectorConfig {

    private static final Logger log = LoggerFactory.getLogger(PgVectorConfig.class);

    @Value("${spring.ai.vectorstore.pgvector.dimensions:768}")
    private int dimensions;

    private final JdbcTemplate jdbcTemplate;

    public PgVectorConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initializePgVector() {
        log.info("pgvector configuration initialized. Vector dimensions: {}", dimensions);
        try {
            String version = jdbcTemplate.queryForObject(
                    "SELECT extversion FROM pg_extension WHERE extname='vector'",
                    String.class);
            log.info("pgvector extension version: {}", version);
        } catch (Exception e) {
            log.warn("Could not retrieve pgvector extension version (extension may not be installed): {}",
                    e.getMessage());
        }
    }
}
