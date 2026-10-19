ALTER TABLE files
DROP CONSTRAINT chk_files_status;

ALTER TABLE files
    ADD CONSTRAINT chk_files_status
        CHECK (status IN ('PENDING', 'ACTIVE', 'DELETING', 'DELETED'));