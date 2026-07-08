package com.rmsc.ai.controller;

import com.rmsc.ai.dto.ChatRequest;
import com.rmsc.ai.dto.ChatResponse;
import com.rmsc.ai.rag.RagService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the complete RAG chat pipeline.
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final RagService ragService;

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("POST /chat – Question: '{}'", request.getQuestion());
        ChatResponse response = ragService.chat(request);
        return ResponseEntity.ok(response);
    }
}
