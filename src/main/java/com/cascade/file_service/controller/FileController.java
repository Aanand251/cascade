package com.cascade.file_service.controller;

import com.cascade.file_service.dto.FileDownloadResponse;
import com.cascade.file_service.dto.FileResponse;
import com.cascade.file_service.entity.File;
import com.cascade.file_service.security.AuthenticatedUser;
import com.cascade.file_service.service.FileDownloadService;
import com.cascade.file_service.service.FileLifecycleService;
import com.cascade.file_service.service.FileService;
import com.cascade.file_service.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;
    private final FileDownloadService fileDownloadService;
    private final FileService fileService;
    private final FileLifecycleService fileLifecycleService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileResponse> upload(
            Authentication authentication,
            @RequestPart("file") MultipartFile file
    ) {

        Long userId = AuthenticatedUser.getId(authentication);

        File savedFile = fileUploadService.upload(
                userId,
                file
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(FileResponse.from(savedFile));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<FileDownloadResponse> download(
            Authentication authentication,
            @PathVariable Long fileId
    ) {

        Long userId = AuthenticatedUser.getId(authentication);

        FileDownloadResponse response =
                fileDownloadService.generateDownloadUrl(
                        fileId,
                        userId
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<FileResponse>> getMyFiles(
            Authentication authentication
    ) {
        Long userId = AuthenticatedUser.getId(authentication);

        List<FileResponse> files = fileService
                .getActiveFilesForOwner(userId)
                .stream()
                .map(FileResponse::from)
                .toList();

        return ResponseEntity.ok(files);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponse> getFile(
            Authentication authentication,
            @PathVariable Long fileId
    ) {
        Long userId = AuthenticatedUser.getId(authentication);

        FileResponse response =
                fileService.getFileResponseForOwner(fileId, userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            Authentication authentication,
            @PathVariable Long fileId
    ) {
        Long userId = AuthenticatedUser.getId(authentication);

        fileLifecycleService.deleteFile(fileId, userId);

        return ResponseEntity.noContent().build();
    }
}