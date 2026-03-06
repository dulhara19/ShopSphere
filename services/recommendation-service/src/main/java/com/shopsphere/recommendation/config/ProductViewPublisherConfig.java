package com.shopsphere.recommendation.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductViewPublisherConfig {
    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;

    public ProductViewPublisherConfig(RabbitTemplate rabbitTemplate, DirectExchange productViewExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = productViewExchange;
    }

    public void publishProductView(String productId, String payload) {
        String routingKey = productId != null ? productId : "";
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, payload != null ? payload : productId);
    }
}
