package com.auth.config;

import com.auth.dto.JwtAuthResponse;
import com.auth.enums.AuthProvider;
import com.auth.exceptions.NotFoundException;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final String FRONTEND_URL = "http://localhost:5173";


    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("Gotten user {} from context----------", oAuth2User);
        String email = oAuth2User.getAttribute("email");
        if(email==null){
            throw new NotFoundException("Email not found for oauth user");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("User with email not found for email:"+email));

        JwtAuthResponse jwtResponse = createJwtResponse(user);

        // Redirect with tokens in URL
//        TODO:MORE SECURE FOR PROD
        String redirectUrl = String.format("%s/oauth2/redirect?access_token=%s&refresh_token=%s",
                FRONTEND_URL,
                jwtResponse.getAccessToken(),
                jwtResponse.getRefreshToken());

        log.info("Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

//    private User createNewUser(OAuth2User oAuth2User) {
//        User user = new User();
//        user.setEmail(oAuth2User.getAttribute("email"));
//        user.setPublicId(UUID.randomUUID().toString());
//        user.setActivated(true);
//        user.setAttributes(oAuth2User.getAttributes());
//
//        log.info("Checking provider ----------------{}",oAuth2User.getAttributes());
//        // Checking for Google OAuth
//        if ("accounts.google.com".equals(oAuth2User.getAttribute("iss"))) {
//            user.setProvider(AuthProvider.GOOGLE);
//            user.setProviderId(oAuth2User.getAttribute("sub"));
//        } else {
//            user.setProvider(AuthProvider.LOCAL);
//        }
//
//        return userRepository.save(user);
//    }

    private JwtAuthResponse createJwtResponse(User user) {
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(jwtService.generateAccessToken(user));
        jwtAuthResponse.setRefreshToken(jwtService.generateRefreshToken(user));
        return jwtAuthResponse;
    }
}