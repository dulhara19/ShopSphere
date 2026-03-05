package com.shopsphere.recommendation.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.DirectExchange;

@Configuration
public class RabbitMQConfig {

    // Name of the queue
    public static final String PRODUCT_VIEW_QUEUE = "product_view_queue";

    // Create the queue bean
    @Bean
    public Queue productViewQueue() {
        return new Queue(PRODUCT_VIEW_QUEUE, true); // durable queue
    }

    // Create RabbitTemplate bean
    @Bean
    public RabbitTemplate rabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public DirectExchange productViewExchange() {
        return new DirectExchange("product-exchange");
    }

}
