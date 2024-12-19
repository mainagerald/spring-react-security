package com.auth.SECURITY.service.impl;

import com.auth.SECURITY.service.TokenBlacklistService;
import com.hazelcast.core.HazelcastInstance;
import com.auth.SECURITY.service.JwtService;
import com.hazelcast.map.IMap;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final HazelcastInstance hazelcastInstance;
    private final JwtService jwtService;

    public void blacklistToken(String token) {
        IMap<String, Boolean> tokenBlacklist =
                hazelcastInstance.getMap("tokenBlacklistCache");
        try {
            Claims claims = jwtService.extractAllClaims(token);
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            long timeToLive = Math.max(0, expirationTime - currentTime);

            tokenBlacklist.put(token, true, timeToLive, TimeUnit.MILLISECONDS);
            log.info("Token blacklisted successfully");
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
        }
    }
    public boolean isTokenBlacklisted(String token) {
        log.info("checking if token {} is blacklisted", token);
        ConcurrentMap<String, Boolean> tokenBlacklist =
                hazelcastInstance.getMap("tokenBlacklistCache");
        return tokenBlacklist.containsKey(token);
    }
}