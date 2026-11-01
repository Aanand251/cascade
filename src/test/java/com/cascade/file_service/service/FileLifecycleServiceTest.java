package com.cascade.file_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileLifecycleServiceTest {

    @Mock
    private FileDeletionStateService deletionStateService;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private FileLifecycleService fileLifecycleService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldDeleteFileSuccessfully() {

        // Arrange
        Long fileId = 1L;
        Long ownerId = 10L;
        String objectKey = "files/test-file.pdf";

        when(
                deletionStateService.beginDeletion(
                        fileId,
                        ownerId
                )
        ).thenReturn(objectKey);

        // Act
        fileLifecycleService.deleteFile(
                fileId,
                ownerId
        );

        // Assert
        verify(deletionStateService)
                .beginDeletion(fileId, ownerId);

        verify(s3StorageService)
                .delete(objectKey);

        verify(deletionStateService)
                .completeDeletion(fileId, ownerId);
    }

    @Test
    void shouldNotCompleteDeletionWhenS3DeletionFails() {

        // Arrange
        Long fileId = 1L;
        Long ownerId = 10L;
        String objectKey = "files/test-file.pdf";

        when(
                deletionStateService.beginDeletion(
                        fileId,
                        ownerId
                )
        ).thenReturn(objectKey);

        doThrow(new RuntimeException("S3 deletion failed"))
                .when(s3StorageService)
                .delete(objectKey);

        // Act + Assert
        assertThrows(
                RuntimeException.class,
                () -> fileLifecycleService.deleteFile(fileId, ownerId)
        );

        // Verify deletion started
        verify(deletionStateService)
                .beginDeletion(fileId, ownerId);

        // Verify S3 deletion was attempted
        verify(s3StorageService)
                .delete(objectKey);

        // Critical: deletion must NOT be completed
        verify(deletionStateService, never())
                .completeDeletion(fileId, ownerId);
    }
}