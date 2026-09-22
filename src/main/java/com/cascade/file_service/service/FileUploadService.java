package com.cascade.file_service.service;

import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;
import com.cascade.file_service.entity.User;
import com.cascade.file_service.repository.FileRepository;
import com.cascade.file_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

    @Value("${app.file.max-size-bytes}")
    private long maxFileSize;

    @Value("${app.file.allowed-content-types}")
    private String allowedContentTypes;

    @Transactional
    public File upload(Long userId, MultipartFile multipartFile) {

        validateFile(multipartFile);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        String originalFilename = sanitizeFilename(
                multipartFile.getOriginalFilename()
        );

        String contentType = multipartFile.getContentType();

        String objectKey =
                "users/" + userId +
                        "/files/" + UUID.randomUUID();

        try (InputStream inputStream = multipartFile.getInputStream()) {

            s3StorageService.upload(
                    objectKey,
                    inputStream,
                    multipartFile.getSize(),
                    contentType
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read uploaded file",
                    e
            );
        }

        File file = File.builder()
                .owner(user)
                .originalFilename(originalFilename)
                .objectKey(objectKey)
                .contentType(contentType)
                .sizeBytes(multipartFile.getSize())
                .status(FileStatus.ACTIVE)
                .build();

        return fileRepository.save(file);
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    "File exceeds maximum allowed size"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !getAllowedContentTypes().contains(contentType)) {

            throw new IllegalArgumentException(
                    "File type is not allowed"
            );
        }
    }

    private Set<String> getAllowedContentTypes() {
        return Arrays.stream(allowedContentTypes.split(","))
                .map(String::trim)
                .filter(type -> !type.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    private String sanitizeFilename(String filename) {

        if (filename == null || filename.isBlank()) {
            return "unnamed";
        }

        String sanitized = filename.replace("\\", "/");

        sanitized = sanitized.substring(
                sanitized.lastIndexOf('/') + 1
        );

        sanitized = sanitized
                .replaceAll("[\\r\\n]", "_")
                .trim();

        if (sanitized.isBlank()) {
            return "unnamed";
        }

        return sanitized.length() > 255
                ? sanitized.substring(0, 255)
                : sanitized;
    }
}