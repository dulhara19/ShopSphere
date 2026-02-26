package com.shopsphere.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Exchanges ──
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String DLX_EXCHANGE = "email.dlx.exchange";

    // ── Queues ──
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String EMAIL_DLQ = "email.dlq";
    public static final String ORDER_EVENT_QUEUE = "notification.order.queue";
    public static final String PAYMENT_EVENT_QUEUE = "notification.payment.queue";
    public static final String USER_EVENT_QUEUE = "notification.user.queue";
    public static final String INVENTORY_EVENT_QUEUE = "notification.inventory.queue";
    public static final String REVIEW_EVENT_QUEUE = "notification.review.queue";

    // ── Routing Keys ──
    public static final String EMAIL_ROUTING_KEY = "email.send";
    public static final String ORDER_ROUTING_KEY = "order.*";
    public static final String PAYMENT_ROUTING_KEY = "payment.*";
    public static final String USER_ROUTING_KEY = "user.*";
    public static final String INVENTORY_ROUTING_KEY = "inventory.*";
    public static final String REVIEW_ROUTING_KEY = "review.*";

    // ── Email Queue with DLQ ──
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "email.dead")
                .build();
    }

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(emailDlq()).to(dlxExchange()).with("email.dead");
    }

    // ── Event Queues ──
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue orderEventQueue() {
        return QueueBuilder.durable(ORDER_EVENT_QUEUE).build();
    }

    @Bean
    public Queue paymentEventQueue() {
        return QueueBuilder.durable(PAYMENT_EVENT_QUEUE).build();
    }

    @Bean
    public Queue userEventQueue() {
        return QueueBuilder.durable(USER_EVENT_QUEUE).build();
    }

    @Bean
    public Queue inventoryEventQueue() {
        return QueueBuilder.durable(INVENTORY_EVENT_QUEUE).build();
    }

    @Bean
    public Queue reviewEventQueue() {
        return QueueBuilder.durable(REVIEW_EVENT_QUEUE).build();
    }

    @Bean
    public Binding orderEventBinding() {
        return BindingBuilder.bind(orderEventQueue()).to(notificationExchange()).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding paymentEventBinding() {
        return BindingBuilder.bind(paymentEventQueue()).to(notificationExchange()).with(PAYMENT_ROUTING_KEY);
    }

    @Bean
    public Binding userEventBinding() {
        return BindingBuilder.bind(userEventQueue()).to(notificationExchange()).with(USER_ROUTING_KEY);
    }

    @Bean
    public Binding inventoryEventBinding() {
        return BindingBuilder.bind(inventoryEventQueue()).to(notificationExchange()).with(INVENTORY_ROUTING_KEY);
    }

    @Bean
    public Binding reviewEventBinding() {
        return BindingBuilder.bind(reviewEventQueue()).to(notificationExchange()).with(REVIEW_ROUTING_KEY);
    }

    // ── JSON Message Converter ──
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
