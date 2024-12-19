package com.auth.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    Claims extractAllClaims(String token);
    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    String generateToken(UserDetails userDetails, long expiration);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
    Long extractUserId(String token);
    String extractPublicId(String token);
    boolean isRefreshTokenValid(String token, UserDetails userDetails);
}