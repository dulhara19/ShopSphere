package com.shopsphere.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    // Kafka configuration is handled through application.yml
    // Topic creation and other configurations can be added here if needed
}
