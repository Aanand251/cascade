package com.cascade.file_service.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}