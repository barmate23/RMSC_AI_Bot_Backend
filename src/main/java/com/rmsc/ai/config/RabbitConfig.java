package com.rmsc.ai.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String RMSC_AI_EVENTS_QUEUE = "rmsc.ai.events";

    @Bean
    public Queue rmscAiEventsQueue() {
        return new Queue(RMSC_AI_EVENTS_QUEUE, true);
    }
}
