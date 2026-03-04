package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.request.WebhookRegistrationRequest;
import com.shopsphere.inventory.dto.response.WebhookRegistrationResponse;
import com.shopsphere.inventory.model.WebhookSubscription;
import com.shopsphere.inventory.repository.WebhookSubscriptionRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public WebhookRegistrationResponse registerWebhook(WebhookRegistrationRequest request) {
        WebhookSubscription sub = WebhookSubscription.builder()
                .url(request.getUrl())
                .eventType(request.getEventType())
                .active(true)
                .build();
        WebhookSubscription saved = Objects.requireNonNull(webhookSubscriptionRepository.save(sub), "subscription");
        return WebhookRegistrationResponse.fromEntity(saved);
    }

    public void dispatchEvent(String eventType, Map<String, Object> payload) {
        List<WebhookSubscription> subscriptions = webhookSubscriptionRepository.findByActiveTrueAndEventType(eventType);
        Map<String, Object> safePayload = Objects.requireNonNull(payload, "payload");
        subscriptions.forEach(subscription -> deliverWithRetry(subscription, safePayload));
    }

    private void deliverWithRetry(WebhookSubscription subscription, Map<String, Object> payload) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Objects.requireNonNull(payload, "payload"));

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        Objects.requireNonNull(subscription, "subscription").getUrl(),
                        HttpMethod.POST,
                        request,
                        String.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    return;
                }
            } catch (Exception ex) {
                log.warn("Webhook delivery failed (attempt {}) to {}: {}", attempt, subscription.getUrl(), ex.getMessage());
            }
        }
    }
}
