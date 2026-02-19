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

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils; // ඔයාගේ Test එකේ තියෙන නම
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

        // JWT ටෝකන් එක ජෙනරේට් කරනවා
        // සටහන: generateAccessToken method එකේ parameters ඔයාගේ JwtUtils එකේ විදිහට බලන්න
        String token = jwtUtils.generateAccessToken(email, user.getId().toString(), user.getRoles());

        // 🚀 මෙන්න මෙතනයි වැදගත්:
        // ඔයාගේ Frontend එක React (Port 3000) නම් මේ URL එක දෙන්න
        String targetUrl = "http://localhost:3000/login-success?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
