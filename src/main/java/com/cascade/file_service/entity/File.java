package com.cascade.file_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "files",
        indexes = {
                @Index(
                        name = "idx_files_owner_id",
                        columnList = "owner_id"
                ),
                @Index(
                        name = "idx_files_owner_created_at",
                        columnList = "owner_id, created_at"
                ),
                @Index(
                        name = "idx_files_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    private User owner;

    @Column(
            name = "original_filename",
            nullable = false,
            length = 255
    )
    private String originalFilename;

    @Column(
            name = "object_key",
            nullable = false,
            unique = true,
            length = 1024
    )
    private String objectKey;

    @Column(name = "content_type", length = 255)
    private String contentType;

    @Column(
            name = "size_bytes",
            nullable = false
    )
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private FileStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        OffsetDateTime now = OffsetDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = FileStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}