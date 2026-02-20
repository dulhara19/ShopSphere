package com.shopsphere.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing Configuration
 *
 * This configuration enables automatic auditing of entity lifecycle events,
 * specifically tracking creation and modification timestamps.
 *
 * - @CreatedDate: Automatically sets the creation timestamp
 * - @LastModifiedDate: Automatically updates the modification timestamp
 * - @CreatedBy: Can track who created an entity (if AuditorAware is provided)
 * - @LastModifiedBy: Can track who modified an entity (if AuditorAware is provided)
 */
@Configuration
@EnableJpaAuditing
public class PersistenceConfig {
    // Configuration is minimal - @EnableJpaAuditing activates JPA auditing globally
}

