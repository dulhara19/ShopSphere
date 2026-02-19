package com.shopsphere.user;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Force-initializes RabbitMQ queues and exchanges on application startup.
 */
@Configuration
public class RabbitInitializer {

    @Bean
    public CommandLineRunner initRabbit(RabbitAdmin rabbitAdmin) {
        return args -> {
            System.out.println("🔥 RabbitMQ Queues initializing...");
            rabbitAdmin.initialize(); // මෙන්න මෙතනින් තමයි RabbitMQ එකට බල කරන්නේ
            System.out.println("✅ RabbitMQ Queues initialized successfully!");
        };
    }
}
