package com.auth.dto;

import lombok.Data;

@Data
public class ValidateTokenRequest {
    private String accessToken;
}
