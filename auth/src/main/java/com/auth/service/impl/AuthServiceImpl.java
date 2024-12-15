package com.auth.service.impl;


import com.auth.dto.JwtAuthResponse;
import com.auth.dto.SignInRequest;
import com.auth.dto.SignUpRequest;
import com.auth.enums.Role;
import com.auth.exceptions.UnauthorizedException;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.service.AuthService;
import com.auth.service.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> initiateSignUp(SignUpRequest signUpRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use.");
        }

        // Generate verification token
        String verificationToken = generateVerificationToken();

        // Create unverified
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPublicId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.ROLE_TENANT);
        user.setVerificationToken(verificationToken);
        user.setActivated(false);

        userRepository.save(user);
        sendVerificationEmail(user.getEmail(), verificationToken);
        return ResponseEntity.status(HttpStatus.CREATED).body("Verification email sent.");
    }

    public ResponseEntity<?> verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid verification token.");
        }

        // Enable user and clear token
        user.setActivated(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully.");
    }
    private void sendVerificationEmail(String email, String token) {
        try {
            emailService.sendVerificationEmail(email, token);
        } catch (MessagingException e) {
            // retry mechanism or
            // storing failed verification emails for manual review
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }


    public JwtAuthResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
        var user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email address"));
        if(!user.isActivated()){
            throw new UnauthorizedException("User is not activated!");
        }
        return createJwtAuthResponse(user);
    }

    public JwtAuthResponse refreshToken(String refreshToken) {
        try {
            String userEmail = jwtService.extractUsername(refreshToken);
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (jwtService.isRefreshTokenValid(refreshToken, user)) {
                return createJwtAuthResponse(user);
            } else {
                throw new IllegalArgumentException("Invalid refresh token");
            }
        } catch (Exception e) {
            log.error("Error refreshing token: ", e);
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    private JwtAuthResponse createJwtAuthResponse(User user) {
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);
        return jwtAuthResponse;
    }

    public boolean isTokenValid(String token) {
        try {
            String userEmail = jwtService.extractUsername(token);
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email address!"));
            return jwtService.isTokenValid(token, user);
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            return false;
        }
    }
}