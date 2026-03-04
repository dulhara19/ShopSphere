package com.shopsphere.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * User Service - Main Spring Boot Application Entry Point
 *
 * Responsibilities:
 * - User Authentication & Authorization
 * - User Profile Management
 * - Role-Based Access Control (RBAC)
 * - Inter-service Communication
 * * Phase 4.3: Event Publishing Integrated
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
