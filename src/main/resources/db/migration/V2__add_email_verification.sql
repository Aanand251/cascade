ALTER TABLE users
    ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE users
    ADD COLUMN email_verified_at TIMESTAMPTZ;

CREATE TABLE email_verification_tokens (
                                           id BIGSERIAL PRIMARY KEY,

                                           user_id BIGINT NOT NULL,

                                           token_hash VARCHAR(64) NOT NULL UNIQUE,

                                           expires_at TIMESTAMPTZ NOT NULL,

                                           used_at TIMESTAMPTZ,

                                           created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                           CONSTRAINT fk_email_verification_tokens_user
                                               FOREIGN KEY (user_id)
                                                   REFERENCES users(id)
                                                   ON DELETE CASCADE
);

CREATE INDEX idx_email_verification_tokens_user_id
    ON email_verification_tokens(user_id);

CREATE INDEX idx_email_verification_tokens_expires_at
    ON email_verification_tokens(expires_at);