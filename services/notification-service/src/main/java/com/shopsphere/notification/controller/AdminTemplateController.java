package com.shopsphere.notification.controller;

import com.shopsphere.notification.model.EmailTemplate;
import com.shopsphere.notification.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
public class AdminTemplateController {
    private final EmailTemplateRepository repository;

    @PostMapping
    public EmailTemplate create(@RequestBody EmailTemplate template) {
        return repository.save(template);
    }

    @GetMapping
    public List<EmailTemplate> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public EmailTemplate getById(@PathVariable String id) {
        return repository.findById(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public EmailTemplate update(@PathVariable String id, @RequestBody EmailTemplate updated) {
        updated.setId(id);
        return repository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }
}