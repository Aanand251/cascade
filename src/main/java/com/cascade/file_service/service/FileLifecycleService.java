package com.cascade.file_service.service;

import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;
import com.cascade.file_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileLifecycleService {

    private final FileRepository fileRepository;
    private final S3StorageService s3StorageService;

    @Transactional
    public void deleteFile(Long fileId, Long ownerId) {

        File file = fileRepository
                .findByIdAndOwnerId(fileId, ownerId)
                .filter(existingFile -> existingFile.getStatus() == FileStatus.ACTIVE)
                .orElseThrow(() ->
                        new IllegalArgumentException("File not found"));

        s3StorageService.delete(file.getObjectKey());

        file.setStatus(FileStatus.DELETED);
        fileRepository.save(file);
    }
}