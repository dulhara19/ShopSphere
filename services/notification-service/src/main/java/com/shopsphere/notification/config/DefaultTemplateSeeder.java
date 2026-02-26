package com.shopsphere.notification.config;

import com.shopsphere.notification.model.EmailTemplate;
import com.shopsphere.notification.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Epic 1.2.5 — Seeds default email templates on startup if not already present
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DefaultTemplateSeeder implements CommandLineRunner {

    private final EmailTemplateRepository templateRepository;

    @Override
    public void run(String... args) {
        if (templateRepository.count() > 0) {
            log.info("📄 Templates already exist, skipping seed.");
            return;
        }

        log.info("🌱 Seeding default email templates...");

        List<EmailTemplate> templates = List.of(
                createTemplate("welcome", "Welcome to ShopSphere, {{customerName}}!",
                        "<h1>Welcome, {{customerName}}!</h1><p>Thank you for joining ShopSphere. Start exploring amazing products today!</p>",
                        List.of("customerName")),

                createTemplate("email-verification", "Verify Your Email Address",
                        "<h1>Email Verification</h1><p>Hi {{customerName}}, please click the link below to verify your email:</p><a href='{{verificationLink}}'>Verify Email</a>",
                        List.of("customerName", "verificationLink")),

                createTemplate("password-reset", "Reset Your Password",
                        "<h1>Password Reset</h1><p>Hi {{customerName}}, click below to reset your password:</p><a href='{{resetLink}}'>Reset Password</a><p>This link expires in 24 hours.</p>",
                        List.of("customerName", "resetLink")),

                createTemplate("order-confirmation", "Order #{{orderNumber}} Confirmed",
                        "<h1>Order Confirmed!</h1><p>Hi {{customerName}}, your order <strong>#{{orderNumber}}</strong> has been placed successfully.</p><p>Total: ${{totalAmount}}</p>{{#if estimatedDelivery}}<p>Estimated Delivery: {{estimatedDelivery}}</p>{{/if}}",
                        List.of("customerName", "orderNumber", "totalAmount", "estimatedDelivery")),

                createTemplate("order-shipped", "Order #{{orderNumber}} Shipped!",
                        "<h1>Your Order is on the Way!</h1><p>Hi {{customerName}}, your order <strong>#{{orderNumber}}</strong> has been shipped.</p>{{#if trackingNumber}}<p>Tracking: {{trackingNumber}}</p>{{/if}}",
                        List.of("customerName", "orderNumber", "trackingNumber")),

                createTemplate("order-delivered", "Order #{{orderNumber}} Delivered",
                        "<h1>Order Delivered!</h1><p>Hi {{customerName}}, your order <strong>#{{orderNumber}}</strong> has been delivered.</p><p>We'd love to hear your feedback! <a href='{{reviewLink}}'>Leave a Review</a></p>",
                        List.of("customerName", "orderNumber", "reviewLink")),

                createTemplate("payment-receipt", "Payment Receipt for Order #{{orderNumber}}",
                        "<h1>Payment Received</h1><p>Hi {{customerName}}, we've received your payment of <strong>${{amount}}</strong> for order #{{orderNumber}}.</p>",
                        List.of("customerName", "orderNumber", "amount")),

                createTemplate("payment-failed", "Payment Failed for Order #{{orderNumber}}",
                        "<h1>Payment Failed</h1><p>Hi {{customerName}}, the payment for order <strong>#{{orderNumber}}</strong> was unsuccessful.</p><p>Please <a href='{{retryLink}}'>update your payment method</a> to complete your purchase.</p>",
                        List.of("customerName", "orderNumber", "retryLink")),

                createTemplate("low-stock-alert", "Low Stock Alert: {{productName}}",
                        "<h1>Low Stock Alert</h1><p>Hi {{sellerName}}, your product <strong>{{productName}}</strong> is running low on stock.</p><p>Current stock: <strong>{{currentStock}}</strong></p><p><a href='{{inventoryLink}}'>Manage Inventory</a></p>",
                        List.of("sellerName", "productName", "currentStock", "inventoryLink")),

                createTemplate("review-reminder", "New Review on {{productName}}",
                        "<h1>New Review!</h1><p>Hi {{sellerName}}, your product <strong>{{productName}}</strong> received a {{rating}}-star review.</p><p>\"{{reviewText}}\"</p>",
                        List.of("sellerName", "productName", "rating", "reviewText")));

        templateRepository.saveAll(templates);
        log.info("✅ Seeded {} default email templates", templates.size());
    }

    private EmailTemplate createTemplate(String name, String subject, String htmlContent, List<String> variables) {
        EmailTemplate t = new EmailTemplate();
        t.setName(name);
        t.setSubject(subject);
        t.setHtmlContent(htmlContent);
        t.setPlainTextContent(htmlContent.replaceAll("<[^>]*>", "")); // Strip HTML for plain text
        t.setVariables(variables);
        return t;
    }
}
