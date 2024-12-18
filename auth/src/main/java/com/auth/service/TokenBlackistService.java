package com.auth.service;

public interface TokenBlackistService {
    boolean isTokenBlacklisted(String token);
}
