package com.cascade.file_service.service;

import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;
import com.cascade.file_service.exception.ResourceNotFoundException;
import com.cascade.file_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileDeletionStateService {

    private final FileRepository fileRepository;

    @Transactional
    public String beginDeletion(Long fileId, Long ownerId) {
        File file = fileRepository.findByIdAndOwnerId(fileId, ownerId)
                .filter(existingFile ->
                        existingFile.getStatus() == FileStatus.ACTIVE
                                || existingFile.getStatus() == FileStatus.DELETING)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));

        if (file.getStatus() == FileStatus.ACTIVE) {
            file.setStatus(FileStatus.DELETING);
            fileRepository.saveAndFlush(file);
        }

        return file.getObjectKey();
    }

    @Transactional
    public void completeDeletion(Long fileId, Long ownerId) {
        File file = fileRepository.findByIdAndOwnerId(fileId, ownerId)
                .filter(existingFile ->
                        existingFile.getStatus() == FileStatus.DELETING)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));

        file.setStatus(FileStatus.DELETED);
        fileRepository.save(file);
    }
}