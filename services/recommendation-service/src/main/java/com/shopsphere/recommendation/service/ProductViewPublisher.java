package com.shopsphere.recommendation.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ProductViewPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ProductViewPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendProductViewEvent(String productId) {
        // 'product_view_queue' must match your RabbitMQ queue name
        rabbitTemplate.convertAndSend("product_view_queue", productId);
        System.out.println("Sent product view event: " + productId);
    }
    public void publish(String userId, String productId) {
        String routingKey = "PRODUCT_VIEW";
        String exchange = "product-exchange";

        String payload = String.format("{\"userId\":\"%s\",\"productId\":\"%s\"}", userId, productId);
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }

    @RestController
    @RequestMapping("/products")
    public class ProductController {

        private final ProductViewPublisher productViewPublisher;

        public ProductController(ProductViewPublisher productViewPublisher) {
            this.productViewPublisher = productViewPublisher;
        }

        @GetMapping("/{id}")
        public String viewProduct(@PathVariable String id) {
            // Send event to RabbitMQ
            productViewPublisher.sendProductViewEvent(id);

            // Return product details
            return "Viewing product " + id;
        }
    }
}
