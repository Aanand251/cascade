package com.cascade.file_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileLifecycleService {

    private final FileDeletionStateService deletionStateService;
    private final S3StorageService s3StorageService;

    public void deleteFile(Long fileId, Long ownerId) {
        String objectKey =
                deletionStateService.beginDeletion(fileId, ownerId);

        s3StorageService.delete(objectKey);

        deletionStateService.completeDeletion(fileId, ownerId);
    }
}