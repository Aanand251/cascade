package com.cascade.file_service.service;

import com.cascade.file_service.dto.FileResponse;
import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;
import com.cascade.file_service.exception.ResourceNotFoundException;
import com.cascade.file_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;

    public List<File> getActiveFilesForOwner(Long ownerId) {
        return fileRepository
                .findAllByOwnerIdAndStatusOrderByCreatedAtDesc(
                        ownerId,
                        FileStatus.ACTIVE
                );
    }

    public boolean existsForOwner(Long fileId, Long ownerId) {
        return fileRepository.existsByIdAndOwnerId(fileId, ownerId);
    }

    public File getActiveFileForOwner(Long fileId, Long ownerId) {
        return fileRepository
                .findByIdAndOwnerId(fileId, ownerId)
                .filter(file -> file.getStatus() == FileStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));
    }

    public FileResponse getFileResponseForOwner(Long fileId, Long ownerId) {
        File file = getActiveFileForOwner(fileId, ownerId);
        return FileResponse.from(file);
    }
}