package com.auth.SECURITY.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}