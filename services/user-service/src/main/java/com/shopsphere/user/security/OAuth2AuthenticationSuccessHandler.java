package com.shopsphere.user.security;

import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

        // 1. Roles ටික Enum එකේ සිට String List එකකට හරවා ගැනීම (JwtUtils වලට ගැලපෙන ලෙස)
        List<String> roles = user.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        // 2. JWT Access Token එක ජෙනරේට් කිරීම
        String token = jwtUtils.generateAccessToken(email, user.getId().toString(), roles);

        // 3. Frontend එකට Redirect කිරීම
        // Frontend එක port 3000 දුවනවා නම් මේ URL එක නිවැරදියි
        String targetUrl = "http://localhost:3000/login-success?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
