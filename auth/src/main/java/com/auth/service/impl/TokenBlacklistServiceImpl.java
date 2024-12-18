package com.auth.service.impl;

import com.auth.service.TokenBlacklistService;
import com.hazelcast.core.HazelcastInstance;
import com.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final HazelcastInstance hazelcastInstance;
    private final JwtService jwtService;

    public void blacklistToken(String token) {
        ConcurrentMap<String, Boolean> tokenBlacklist =
                hazelcastInstance.getMap("tokenBlacklistCache");

        try {
            // TODO: (OPTIONAL) Extract token expiration to manage cache efficiently
            Claims claims = jwtService.extractAllClaims(token);
            long expirationTime = claims.getExpiration().getTime();
            // Calculate remaining time to live
            long currentTime = System.currentTimeMillis();
            long timeToLive = Math.max(0, expirationTime - currentTime);

            tokenBlacklist.put(token, true);
            log.info("Token blacklisted successfully");
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        ConcurrentMap<String, Boolean> tokenBlacklist =
                hazelcastInstance.getMap("tokenBlacklistCache");
        return tokenBlacklist.containsKey(token);
    }
}
