package com.cascade.file_service.dto;

import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;

import java.time.OffsetDateTime;

public record FileResponse(
        Long id,
        String originalFilename,
        String contentType,
        Long sizeBytes,
        FileStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static FileResponse from(File file) {
        return new FileResponse(
                file.getId(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSizeBytes(),
                file.getStatus(),
                file.getCreatedAt(),
                file.getUpdatedAt()
        );
    }
}