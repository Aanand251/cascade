package com.cascade.file_service.service;

import com.cascade.file_service.dto.FileDownloadResponse;
import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;
import com.cascade.file_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileDownloadService {

    private final FileRepository fileRepository;
    private final S3StorageService s3StorageService;

    public FileDownloadResponse generateDownloadUrl(Long fileId, Long ownerId) {

        File file = fileRepository.findByIdAndOwnerId(fileId, ownerId)
                .filter(existingFile -> existingFile.getStatus() == FileStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        String downloadUrl = s3StorageService.generateDownloadUrl(
                file.getObjectKey(),
                file.getOriginalFilename()
        );

        return new FileDownloadResponse(
                downloadUrl,
                s3StorageService.getDownloadUrlExpirationSeconds()
        );
    }
}