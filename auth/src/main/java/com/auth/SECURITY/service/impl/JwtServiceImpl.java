package com.auth.SECURITY.service.impl;


import com.auth.SECURITY.model.User;
import com.auth.SECURITY.service.JwtService;
import com.auth.SECURITY.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final TokenBlacklistService tokenBlacklistService;
    //    lazy inject tokenService to avoid circular ref
    public JwtServiceImpl(@Lazy TokenBlacklistService tokenBlacklistService){
        this.tokenBlacklistService=tokenBlacklistService;
    }
    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 12; // 12 hours

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, ACCESS_TOKEN_EXPIRATION);
    }

    @Override
    public String generateToken(UserDetails userDetails, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        if (userDetails instanceof User) {
            claims.put("userId", ((User) userDetails).getId());
            claims.put("publicId", ((User) userDetails).getPublicId());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            log.info("checking validity for token {}", token);
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                log.info("Token is blacklisted");
                return false;
            }

            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            log.info("Token validation result: {}", isValid);
            return isValid;
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
            return false;
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    @Override
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        try {
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                log.info("Refresh token is blacklisted");
                return false;
            }
            final String username = extractUsername(token);
            Claims claims = extractAllClaims(token);
            return (username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token) &&
                    claims.get("tokenType", String.class).equals("refresh"));
        } catch (Exception e) {
            log.error("Error validating refresh token", e);
            return false;
        }
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        if(userDetails instanceof User){
            claims.put("publicId", (((User) userDetails).getPublicId()));
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    @Override
    public String extractPublicId(String token){return  extractClaim(token, claims -> claims.get("publicId", String.class));}

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}