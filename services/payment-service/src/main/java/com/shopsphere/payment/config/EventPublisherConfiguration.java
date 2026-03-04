package com.shopsphere.payment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Event Publisher Configuration
 *
 * Configures event publishing machinery for domain events.
 */
@Configuration
public class EventPublisherConfiguration implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
