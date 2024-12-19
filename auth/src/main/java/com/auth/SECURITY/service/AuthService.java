package com.auth.SECURITY.service;


import com.auth.SECURITY.dto.JwtAuthResponse;
import com.auth.SECURITY.dto.SignInRequest;
import com.auth.SECURITY.dto.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> initiateSignUp(SignUpRequest signUpRequest);
    ResponseEntity<?> verifyEmail(String token);
    JwtAuthResponse signIn(SignInRequest signInRequest);
    JwtAuthResponse refreshToken(String refreshToken);
    boolean isTokenValid(String token);
    ResponseEntity<?> logout(String accessToken, String refreshToken);
}
