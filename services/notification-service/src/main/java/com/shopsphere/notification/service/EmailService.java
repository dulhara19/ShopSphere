package com.shopsphere.notification.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.shopsphere.notification.config.RabbitMQConfig;
import com.shopsphere.notification.model.EmailLog;
import com.shopsphere.notification.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final SendGrid sendGrid;
    private final RabbitTemplate rabbitTemplate;
    private final EmailLogRepository emailLogRepository;

    @Value("${sendgrid.from-email:noreply@shopsphere.com}")
    private String fromEmail;

    @Value("${sendgrid.from-name:ShopSphere}")
    private String fromName;

    @Value("${sendgrid.enabled:false}")
    private boolean sendGridEnabled;

    /**
     * Queue an email for async processing (Epic 1.1.3)
     */
    public EmailLog queueEmail(String to, String subject, String htmlBody, String templateId,
            Map<String, Object> data) {
        EmailLog emailLog = new EmailLog();
        emailLog.setTo(to);
        emailLog.setSubject(subject);
        emailLog.setTemplateId(templateId);
        emailLog.setTemplateData(data);
        emailLog.setStatus("QUEUED");
        emailLog = emailLogRepository.save(emailLog);

        // Push to RabbitMQ for async processing
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, emailLog);
        log.info("📧 Email queued for: {} | Subject: {}", to, subject);
        return emailLog;
    }

    /**
     * Process email from queue (Epic 1.1.3)
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void processEmailQueue(EmailLog emailLog) {
        log.info("📨 Processing queued email ID: {}", emailLog.getId());
        sendEmail(emailLog.getId());
    }

    /**
     * Send email via SendGrid (Epic 1.1.1 + 1.1.2)
     */
    public void sendEmail(String emailLogId) {
        EmailLog emailLog = emailLogRepository.findById(emailLogId).orElse(null);
        if (emailLog == null)
            return;

        emailLog.setAttempts(emailLog.getAttempts() + 1);

        try {
            if (sendGridEnabled) {
                Email from = new Email(fromEmail, fromName);
                Email toEmail = new Email(emailLog.getTo());
                Content content = new Content("text/html",
                        emailLog.getTemplateData() != null
                                ? emailLog.getTemplateData().getOrDefault("htmlBody", "").toString()
                                : "");
                Mail mail = new Mail(from, emailLog.getSubject(), toEmail, content);

                Request request = new Request();
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());

                Response response = sendGrid.api(request);
                if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                    emailLog.setStatus("SENT");
                    emailLog.setSentAt(LocalDateTime.now());
                    log.info("✅ Email sent to: {} | Status: {}", emailLog.getTo(), response.getStatusCode());
                } else {
                    throw new IOException("SendGrid returned status: " + response.getStatusCode());
                }
            } else {
                // Mock mode — simulate successful send
                emailLog.setStatus("SENT");
                emailLog.setSentAt(LocalDateTime.now());
                log.info("📧 [MOCK EMAIL] To: {}, Subject: {}", emailLog.getTo(), emailLog.getSubject());
            }
        } catch (IOException e) {
            log.error("❌ Email send failed for: {} | Attempt: {}/{}", emailLog.getTo(), emailLog.getAttempts(),
                    emailLog.getMaxAttempts(), e);
            emailLog.setErrorMessage(e.getMessage());

            if (emailLog.getAttempts() >= emailLog.getMaxAttempts()) {
                emailLog.setStatus("FAILED");
                log.warn("💀 Email moved to DLQ after {} attempts: {}", emailLog.getMaxAttempts(), emailLog.getTo());
            } else {
                emailLog.setStatus("QUEUED"); // Will be retried
            }
        }

        emailLogRepository.save(emailLog);
    }

    /**
     * Retry failed emails (Epic 1.1.5) — runs every 60 seconds
     */
    @Scheduled(fixedDelay = 60000)
    public void retryFailedEmails() {
        List<EmailLog> retryable = emailLogRepository.findByStatusAndAttemptsLessThan("QUEUED", 3);
        for (EmailLog emailLog : retryable) {
            log.info("🔄 Retrying email ID: {}", emailLog.getId());
            sendEmail(emailLog.getId());
        }
    }

    /**
     * Get email status (Epic 1.1.4)
     */
    public EmailLog getEmailStatus(String emailLogId) {
        return emailLogRepository.findById(emailLogId).orElse(null);
    }

    /**
     * Simple send — for backward compatibility
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        queueEmail(to, subject, body, null, Map.of("htmlBody", body));
    }
}