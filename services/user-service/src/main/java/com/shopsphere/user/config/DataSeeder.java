package com.shopsphere.user.config;

import com.shopsphere.user.model.Role;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@shopsphere.com")) {
            User admin = User.builder()
                    .id(UUID.randomUUID())
                    .username("admin")
                    .email("admin@shopsphere.com")
                    .passwordHash(passwordEncoder.encode("Admin12345"))
                    .firstName("Admin")
                    .lastName("ShopSphere")
                    .isEnabled(true)
                    .isEmailVerified(true)
                    .isAccountLocked(false)
                    .failedLoginAttempts(0)
                    .roles(Set.of(Role.ADMIN))
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin@shopsphere.com");
        }

        if (!userRepository.existsByEmail("customer@example.com")) {
            User customer = User.builder()
                    .id(UUID.randomUUID())
                    .username("customer")
                    .email("customer@example.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .firstName("Demo")
                    .lastName("Customer")
                    .isEnabled(true)
                    .isEmailVerified(true)
                    .isAccountLocked(false)
                    .failedLoginAttempts(0)
                    .roles(Set.of(Role.CUSTOMER))
                    .build();
            userRepository.save(customer);
            log.info("Default demo customer created: customer@example.com");
        }
    }
}
