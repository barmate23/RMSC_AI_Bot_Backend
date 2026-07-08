package com.rmsc.ai.controller;

import com.rmsc.ai.dto.SearchRequest;
import com.rmsc.ai.dto.SearchResult;
import com.rmsc.ai.vector.VectorStoreService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for semantic vector search.
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final VectorStoreService vectorStoreService;

    public SearchController(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> search(@Valid @RequestBody SearchRequest request) {
        log.info("POST /search – Query: '{}', topK: {}", request.getQuery(), request.getTopK());

        List<SearchResult> results = vectorStoreService.search(request);

        return ResponseEntity.ok(Map.of(
                "query",        request.getQuery(),
                "totalResults", results.size(),
                "results",      results
        ));
    }
}
