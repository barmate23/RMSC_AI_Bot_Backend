package com.rmsc.ai.controller;

import com.rmsc.ai.dto.EventRequest;
import com.rmsc.ai.dto.EventResponse;
import com.rmsc.ai.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for ERP event management.
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("POST /events – Creating event type={}", request.getEventType());
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        log.info("GET /events/{}", id);
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        log.info("GET /events – Fetching all events");
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping("/{id}/embed")
    public ResponseEntity<EventResponse> embedEvent(@PathVariable Long id) {
        log.info("POST /events/{}/embed – Generating embedding", id);
        return ResponseEntity.ok(eventService.embedEvent(id));
    }

    @PostMapping("/embed/all")
    public ResponseEntity<Map<String, Object>> embedAllEvents() {
        log.info("POST /events/embed/all – Batch embedding started");
        int created = eventService.embedAllPendingEvents();
        return ResponseEntity.ok(Map.of(
                "embeddingsCreated", created,
                "message", created + " new embedding(s) generated successfully."
        ));
    }
}
