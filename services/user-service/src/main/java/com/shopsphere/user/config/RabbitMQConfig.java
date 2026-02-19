package com.shopsphere.user.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for ShopSphere User Service.
 */
@Configuration
public class RabbitMQConfig {

    // Exchange name
    public static final String USER_EXCHANGE = "user.exchange";

    // Queue names
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_UPDATED_QUEUE = "user.updated.queue";
    public static final String USER_DELETED_QUEUE = "user.deleted.queue";
    public static final String USER_PASSWORD_RESET_QUEUE = "user.password.reset.queue";

    // Routing keys
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_UPDATED_ROUTING_KEY = "user.updated";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";
    public static final String USER_PASSWORD_RESET_ROUTING_KEY = "user.password.reset";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE, true, false);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE, true, false, false);
    }

    @Bean
    public Queue userUpdatedQueue() {
        return new Queue(USER_UPDATED_QUEUE, true, false, false);
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(USER_DELETED_QUEUE, true, false, false);
    }

    /**
     * Define user.password.reset.queue for password reset events
     *
     * @return Queue instance
     */
    @Bean
    public Queue userPasswordResetQueue() {
        return new Queue(USER_PASSWORD_RESET_QUEUE, true, false, false);
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userCreatedQueue).to(userExchange).with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding userUpdatedBinding(Queue userUpdatedQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userUpdatedQueue).to(userExchange).with(USER_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding userDeletedBinding(Queue userDeletedQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userDeletedQueue).to(userExchange).with(USER_DELETED_ROUTING_KEY);
    }

    /**
     * Bind user.password.reset.queue to user.exchange with user.password.reset routing key
     *
     * @param userPasswordResetQueue user password reset queue
     * @param userExchange user exchange
     * @return Binding instance
     */
    @Bean
    public Binding userPasswordResetBinding(Queue userPasswordResetQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userPasswordResetQueue)
            .to(userExchange)
            .with(USER_PASSWORD_RESET_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Forces the creation of exchanges, queues, and bindings upon startup.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }
}
