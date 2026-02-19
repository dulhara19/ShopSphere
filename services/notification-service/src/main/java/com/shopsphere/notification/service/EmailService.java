package com.shopsphere.notification.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {
    public void sendEmail(String to, String subject, String body) {
        // EPIC 1.1 Stub: Allows system to run without SendGrid keys for MVP
        log.info("📧 [MOCK EMAIL] To: {}, Subject: {}", to, subject);
    }
}