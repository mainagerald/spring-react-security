package com.auth.SECURITY.service;

public interface TokenBlacklistService {
    boolean isTokenBlacklisted(String token);
    void blacklistToken(String token);
}
