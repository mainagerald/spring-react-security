package com.auth.controller;



import com.auth.dto.*;
import com.auth.model.User;
import com.auth.service.AuthService;
import com.auth.service.UserService;
import com.auth.service.impl.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        log.info("hit endpoint signup");
        if (!isValidEmail(signUpRequest.getEmail()) ||
                !isValidPassword(signUpRequest.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }

        return authService.initiateSignUp(signUpRequest);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    // Email and password validation methods
    private boolean isValidEmail(String email) {
        return email != null &&
                email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 8 &&
                password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signIn(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok(authService.signIn(signInRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken().replace("\"", "");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody ValidateTokenRequest request){
        log.info("received token {} for validation", request.getAccessToken().toString());
        return ResponseEntity.ok(authService.isTokenValid(request.getAccessToken()));
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<String> handleOAuthCallback(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = (User) userService.loadUserByUsername(email);

        return ResponseEntity.ok("OAuth2 user authenticated: " + user.getEmail());
    }

}
