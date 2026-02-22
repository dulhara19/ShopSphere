package com.shopsphere.analytics.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queues
    @Bean
    public Queue analyticsEventsQueue() {
        return new Queue("analytics.events", true);
    }

    @Bean
    public Queue analyticsBatchEventsQueue() {
        return new Queue("analytics.batch-events", true);
    }

    // Exchanges
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange("events.topic", true, false);
    }

    // Bindings
    @Bean
    public Binding analyticsEventBinding(Queue analyticsEventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(analyticsEventsQueue)
            .to(eventsExchange)
            .with("events.#");
    }

    @Bean
    public Binding analyticsBatchEventBinding(Queue analyticsBatchEventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(analyticsBatchEventsQueue)
            .to(eventsExchange)
            .with("batch.#");
    }

    // Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
