package com.cascade.file_service.service;

import com.cascade.file_service.dto.FileDownloadResponse;
import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;
import com.cascade.file_service.exception.ResourceNotFoundException;
import com.cascade.file_service.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDownloadServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private FileDownloadService fileDownloadService;


    @Test
    void shouldGenerateDownloadUrlForActiveFile() {

        // Arrange
        Long fileId = 1L;
        Long ownerId = 10L;

        File file = File.builder()
                .id(fileId)
                .id(ownerId)
                .objectKey("files/test-file.pdf")
                .originalFilename("test-file.pdf")
                .status(FileStatus.ACTIVE)
                .build();

        when(fileRepository.findByIdAndOwnerId(fileId, ownerId))
                .thenReturn(Optional.of(file));

        when(s3StorageService.generateDownloadUrl(
                "files/test-file.pdf",
                "test-file.pdf"
        )).thenReturn("https://example.com/download-url");

        when(s3StorageService.getDownloadUrlExpirationSeconds())
                .thenReturn(300L);


        // Act
        FileDownloadResponse response =
                fileDownloadService.generateDownloadUrl(
                        fileId,
                        ownerId
                );


        // Assert
        assertNotNull(response);

        assertEquals(
                "https://example.com/download-url",
                response.downloadUrl()
        );

        assertEquals(
                300L,
                response.expiresIn()
        );

        verify(fileRepository)
                .findByIdAndOwnerId(fileId, ownerId);

        verify(s3StorageService)
                .generateDownloadUrl(
                        "files/test-file.pdf",
                        "test-file.pdf"
                );

        verify(s3StorageService)
                .getDownloadUrlExpirationSeconds();
    }


    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() {

        // Arrange
        Long fileId = 1L;
        Long ownerId = 10L;

        when(fileRepository.findByIdAndOwnerId(fileId, ownerId))
                .thenReturn(Optional.empty());


        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> fileDownloadService.generateDownloadUrl(
                        fileId,
                        ownerId
                )
        );


        // S3 must never be called
        verifyNoInteractions(s3StorageService);
    }


    @Test
    void shouldThrowExceptionWhenFileIsNotActive() {

        // Arrange
        Long fileId = 1L;
        Long ownerId = 10L;

        File deletedFile = File.builder()
                .id(fileId)
                .id(ownerId)
                .objectKey("files/test-file.pdf")
                .originalFilename("test-file.pdf")
                .status(FileStatus.DELETED)
                .build();

        when(fileRepository.findByIdAndOwnerId(fileId, ownerId))
                .thenReturn(Optional.of(deletedFile));


        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> fileDownloadService.generateDownloadUrl(
                        fileId,
                        ownerId
                )
        );


        // Critical security check:
        // No download URL should be generated for inactive files.
        verifyNoInteractions(s3StorageService);
    }
}