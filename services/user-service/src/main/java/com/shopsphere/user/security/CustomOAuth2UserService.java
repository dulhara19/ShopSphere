package com.shopsphere.user.security;

import com.shopsphere.user.model.Role;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Email not found in OAuth2 user attributes");
        }

        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String picture = oauth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            User newUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .username(email)
                .firstName(firstName)
                .lastName(lastName)
                .profilePictureUrl(picture)
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .roles(Set.of(Role.CUSTOMER))
                .isEnabled(true)
                .isEmailVerified(true)
                .isAccountLocked(false)
                .failedLoginAttempts(0)
                .lastLoginAt(LocalDateTime.now())
                .build();

            userRepository.save(newUser);
            log.info("Created new OAuth2 user: {}", email);
        } else {
            boolean updated = false;
            if (firstName != null && !firstName.equals(user.getFirstName())) {
                user.setFirstName(firstName);
                updated = true;
            }
            if (lastName != null && !lastName.equals(user.getLastName())) {
                user.setLastName(lastName);
                updated = true;
            }
            if (picture != null && !picture.equals(user.getProfilePictureUrl())) {
                user.setProfilePictureUrl(picture);
                updated = true;
            }
            user.setLastLoginAt(LocalDateTime.now());
            user.setIsEmailVerified(true);
            updated = true;

            if (updated) {
                userRepository.save(user);
                log.info("Updated OAuth2 user profile: {}", email);
            }
        }

        return oauth2User;
    }
}
