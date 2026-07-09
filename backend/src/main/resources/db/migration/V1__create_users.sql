CREATE TABLE users (
                       id         UUID         PRIMARY KEY,
                       username   VARCHAR(50)  NOT NULL,
                       email      VARCHAR(255) NOT NULL,
                       password   VARCHAR(255) NOT NULL,
                       role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
                       created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT uk_users_username UNIQUE (username),
                       CONSTRAINT uk_users_email UNIQUE (email)
);