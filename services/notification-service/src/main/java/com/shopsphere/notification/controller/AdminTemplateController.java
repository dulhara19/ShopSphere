package com.shopsphere.notification.controller;

import com.shopsphere.notification.model.EmailTemplate;
import com.shopsphere.notification.repository.EmailTemplateRepository;
import com.shopsphere.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
public class AdminTemplateController {

    private final EmailTemplateRepository repository;
    private final TemplateService templateService;

    // ── Epic 1.2.2 — Create template ──
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmailTemplate create(@RequestBody EmailTemplate template) {
        return repository.save(template);
    }

    // ── Epic 1.2.1 — List all templates ──
    @GetMapping
    public List<EmailTemplate> getAll() {
        return repository.findAll();
    }

    // ── Epic 1.2.1 — Get template by ID ──
    @GetMapping("/{id}")
    public EmailTemplate getById(@PathVariable String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Template not found: " + id));
    }

    // ── Epic 1.2.2 — Update template (increments version) ──
    @PutMapping("/{id}")
    public EmailTemplate update(@PathVariable String id, @RequestBody EmailTemplate updated) {
        EmailTemplate existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        updated.setId(id);
        updated.setVersion(existing.getVersion() + 1);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(LocalDateTime.now());
        return repository.save(updated);
    }

    // ── Epic 1.2.2 — Delete template ──
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }

    // ── Epic 1.2.4 — Preview template with sample data ──
    @PostMapping("/{id}/preview")
    public Map<String, String> preview(@PathVariable String id, @RequestBody Map<String, Object> sampleData) {
        EmailTemplate template = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        String rendered = templateService.previewTemplate(template.getName(), sampleData);
        return Map.of("html", rendered);
    }
}