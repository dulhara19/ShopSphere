package com.shopsphere.user.service;

import com.shopsphere.user.config.RabbitMQConfig;
import com.shopsphere.user.dto.UserInternalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * UserEventPublisher Service
 *
 * Responsibilities:
 * - Publish user events to RabbitMQ
 * - Send UserInternalDto messages to user.exchange with appropriate routing keys
 * - Handle event publishing errors gracefully
 *
 * Phase 4.3: Event Publishing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publish a user created event
     *
     * @param userDto UserInternalDto containing user details
     */
    public void publishUserCreatedEvent(UserInternalDto userDto) {
        try {
            log.info("Publishing user.created event for user: {}", userDto.getEmail());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_EXCHANGE,
                    RabbitMQConfig.USER_CREATED_ROUTING_KEY,
                    userDto
            );
            log.debug("User created event published successfully for user: {}", userDto.getEmail());
        } catch (Exception e) {
            log.error("Error publishing user.created event for user {}: {}", userDto.getEmail(), e.getMessage(), e);
            // In production, consider retry logic or dead-letter queue handling
        }
    }

    /**
     * Publish a user updated event
     *
     * @param userDto UserInternalDto containing updated user details
     */
    public void publishUserUpdatedEvent(UserInternalDto userDto) {
        try {
            log.info("Publishing user.updated event for user: {}", userDto.getEmail());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_EXCHANGE,
                    RabbitMQConfig.USER_UPDATED_ROUTING_KEY,
                    userDto
            );
            log.debug("User updated event published successfully for user: {}", userDto.getEmail());
        } catch (Exception e) {
            log.error("Error publishing user.updated event for user {}: {}", userDto.getEmail(), e.getMessage(), e);
        }
    }

    /**
     * Publish a user deleted event
     *
     * @param userDto UserInternalDto containing user details of deleted user
     */
    public void publishUserDeletedEvent(UserInternalDto userDto) {
        try {
            log.info("Publishing user.deleted event for user: {}", userDto.getEmail());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_EXCHANGE,
                    RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                    userDto
            );
            log.debug("User deleted event published successfully for user: {}", userDto.getEmail());
        } catch (Exception e) {
            log.error("Error publishing user.deleted event for user {}: {}", userDto.getEmail(), e.getMessage(), e);
        }
    }
}

