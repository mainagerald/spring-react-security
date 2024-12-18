package com.auth.service;

public interface TokenBlacklistService {
    boolean isTokenBlacklisted(String token);
    void blacklistToken(String token);
}
