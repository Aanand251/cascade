CREATE TABLE files (
                       id BIGSERIAL PRIMARY KEY,

                       owner_id BIGINT NOT NULL,

                       original_filename VARCHAR(255) NOT NULL,

                       object_key VARCHAR(1024) NOT NULL UNIQUE,

                       content_type VARCHAR(255),

                       size_bytes BIGINT NOT NULL,

                       status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_files_owner
                           FOREIGN KEY (owner_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE,

                       CONSTRAINT chk_files_size_non_negative
                           CHECK (size_bytes >= 0),

                       CONSTRAINT chk_files_status
                           CHECK (status IN ('PENDING', 'ACTIVE', 'DELETED'))
);

CREATE INDEX idx_files_owner_id
    ON files(owner_id);

CREATE INDEX idx_files_owner_created_at
    ON files(owner_id, created_at DESC);

CREATE INDEX idx_files_status
    ON files(status);