package com.shopsphere.payment.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Stripe Configuration
 *
 * Configures Stripe API key and SDK settings.
 */
@Configuration
public class StripeConfiguration {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
}
