CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,

                       email VARCHAR(255) NOT NULL UNIQUE,

                       password_hash VARCHAR(255) NOT NULL,

                       role VARCHAR(50) NOT NULL DEFAULT 'USER',

                       enabled BOOLEAN NOT NULL DEFAULT TRUE,

                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);