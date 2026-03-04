package com.shopsphere.recommendation.controller;

import com.shopsphere.recommendation.service.VisualSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations/visual-search")
public class VisualSearchController {

    private final VisualSearchService visualSearchService;

    public VisualSearchController(VisualSearchService visualSearchService) {
        this.visualSearchService = visualSearchService;
    }

    @PostMapping
    public ResponseEntity<List<String>> searchByImage(@RequestParam MultipartFile image) throws IOException {
        List<String> results = visualSearchService.searchByImage(image);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/url")
    public ResponseEntity<List<String>> searchByUrl(@RequestBody String imageUrl) throws IOException {
        List<String> results = visualSearchService.searchByImageUrl(imageUrl);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/index")
    public ResponseEntity<Void> indexProductImage(@RequestParam String productId,
                                                  @RequestParam String imageUrl,
                                                  @RequestParam(required = false) MultipartFile file) throws IOException {
        visualSearchService.indexImage(productId, imageUrl, file);
        return ResponseEntity.ok().build();
    }
}
