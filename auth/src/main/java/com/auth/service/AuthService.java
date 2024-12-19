package com.auth.service;


import com.auth.dto.JwtAuthResponse;
import com.auth.dto.SignInRequest;
import com.auth.dto.SignUpRequest;
import com.auth.model.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> initiateSignUp(SignUpRequest signUpRequest);
    ResponseEntity<?> verifyEmail(String token);
    JwtAuthResponse signIn(SignInRequest signInRequest);
    JwtAuthResponse refreshToken(String refreshToken);
    boolean isTokenValid(String token);
    ResponseEntity<?> logout(String accessToken, String refreshToken);
}
