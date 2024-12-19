package com.auth.SECURITY.dto;

import lombok.Data;

@Data
public class ValidateTokenRequest {
    private String accessToken;
}
