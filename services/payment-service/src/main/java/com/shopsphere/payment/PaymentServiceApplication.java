package com.shopsphere.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Payment Service Application
 *
 * Main entry point for the Payment Service microservice.
 * Responsible for payment processing and transactions with Stripe integration.
 *
 * Port: 3005
 */
@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
