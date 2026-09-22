package com.cascade.file_service.repository;

import com.cascade.file_service.entity.File;
import com.cascade.file_service.entity.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByIdAndOwnerId(Long id, Long ownerId);

    List<File> findAllByOwnerIdAndStatusOrderByCreatedAtDesc(
            Long ownerId,
            FileStatus status
    );

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}