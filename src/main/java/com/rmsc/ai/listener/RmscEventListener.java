package com.rmsc.ai.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmsc.ai.config.RabbitConfig;
import com.rmsc.ai.dto.EventRequest;
import com.rmsc.ai.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class RmscEventListener {

    private static final Logger log = LoggerFactory.getLogger(RmscEventListener.class);

    private final EventService eventService;
    private final ObjectMapper objectMapper;

    public RmscEventListener(EventService eventService, ObjectMapper objectMapper) {
        this.eventService = eventService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.RMSC_AI_EVENTS_QUEUE)
    public void consumeRmscEvent(String jsonMessage) {
        log.info("Received RMSC event from queue rawMessage={}", jsonMessage);
        try {
            // Deserialize message as raw map to handle type formatting flexibility (e.g. eventTime string)
            Map<String, Object> payload = objectMapper.readValue(jsonMessage, Map.class);

            String eventType = (String) payload.get("eventType");
            String moduleName = (String) payload.getOrDefault("moduleName", "PPE");
            String referenceType = (String) payload.get("referenceType");
            String referenceId = (String) payload.get("referenceId");
            String description = (String) payload.get("description");
            String status = (String) payload.get("status");
            
            Long organizationId = null;
            if (payload.get("organizationId") != null) {
                organizationId = Long.valueOf(payload.get("organizationId").toString());
            }

            Long userId = null;
            if (payload.get("userId") != null) {
                userId = Long.valueOf(payload.get("userId").toString());
            }

            String eventTimeStr = (String) payload.get("eventTime");
            LocalDateTime eventTime = LocalDateTime.now();
            if (eventTimeStr != null && !eventTimeStr.isBlank()) {
                try {
                    eventTime = LocalDateTime.parse(eventTimeStr);
                } catch (Exception ex) {
                    try {
                        eventTime = LocalDateTime.parse(eventTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                    } catch (Exception ex2) {
                        log.warn("Unable to parse eventTime string: '{}'. Defaulting to now.", eventTimeStr);
                    }
                }
            }

            Map<String, Object> metadata = (Map<String, Object>) payload.get("metadata");
            String createdBy = (String) payload.get("createdBy");
            String eventReferenceId = (String) payload.get("eventReferenceId");

            // Construct EventRequest DTO
            EventRequest request = EventRequest.builder()
                    .eventType(eventType)
                    .moduleName(moduleName)
                    .referenceType(referenceType)
                    .referenceId(referenceId)
                    .description(description)
                    .status(status)
                    .organizationId(organizationId)
                    .userId(userId)
                    .eventTime(eventTime)
                    .metadata(metadata)
                    .createdBy(createdBy)
                    .eventReferenceId(eventReferenceId)
                    .build();

            // Save and automatically trigger embedding calculation via EventService
            eventService.createEvent(request);
            log.info("Successfully executed RMSC RAG Bot event ingestion pipeline for eventType={}", eventType);

        } catch (Exception e) {
            log.error("Failed to process consumed RMSC event detail from queue", e);
        }
    }
}
