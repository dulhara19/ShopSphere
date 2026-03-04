package com.shopsphere.payment.controller;

import com.shopsphere.payment.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.Event;

/**
 * Webhook Controller
 *
 * Handles Stripe webhook events.
 * Implements Epic 1.1 Story 1.1.3 and Epic 1.4 Story 1.4.4 requirements.
 */
@RestController
@RequestMapping("/api/webhooks")
@Tag(name = "Webhooks", description = "Webhook event endpoints")
@Slf4j
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    /**
     * Receive Stripe webhook
     * Story 1.1.3: Webhook endpoint setup
     * POST /api/webhooks/stripe
     */
    @PostMapping("/stripe")
    @Operation(summary = "Receive Stripe webhook", description = "Endpoint for receiving Stripe webhook events")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sig) {
        log.info("Received Stripe webhook");

        try {
            // Verify webhook signature
            Event event = webhookService.verifyWebhookSignature(payload, sig);
            log.info("Webhook verified, type: {}", event.getType());

            // Process the event
            webhookService.processWebhookEvent(event);

            return ResponseEntity.ok("{\"status\":\"success\"}");

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"status\":\"failure\"}");
        }
    }
}
