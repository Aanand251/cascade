package com.cascade.file_service.dto;

public record FileDownloadResponse(
        String downloadUrl,
        long expiresIn
) {
}