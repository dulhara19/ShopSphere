package com.shopsphere.notification.controller;

import com.shopsphere.notification.dto.EmailRequest;
import com.shopsphere.notification.model.EmailLog;
import com.shopsphere.notification.service.EmailService;
import com.shopsphere.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications/email")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;
    private final TemplateService templateService;

    // ── Epic 1.1.2 — Send transactional email ──
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> sendEmail(@RequestBody EmailRequest request) {
        String subject;
        String htmlBody;

        if (request.getTemplateId() != null) {
            subject = templateService.renderSubject(request.getTemplateId(), request.getData());
            htmlBody = templateService.renderTemplate(request.getTemplateId(), request.getData());
        } else {
            subject = request.getData() != null ? request.getData().getOrDefault("subject", "No Subject").toString()
                    : "No Subject";
            htmlBody = request.getData() != null ? request.getData().getOrDefault("body", "").toString() : "";
        }

        EmailLog log = emailService.queueEmail(request.getTo(), subject, htmlBody, request.getTemplateId(),
                request.getData());
        return Map.of("emailId", log.getId(), "status", log.getStatus());
    }

    // ── Epic 1.1.4 — Get email delivery status done or fail ──
    @GetMapping("/{id}/status")
    public EmailLog getStatus(@PathVariable String id) {
        EmailLog log = emailService.getEmailStatus(id);
        if (log == null)
            throw new RuntimeException("Email log not found: " + id);
        return log;
    }
}
