CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_login_at TIMESTAMPTZ
);

CREATE TABLE short_urls (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    short_code   VARCHAR(10)   NOT NULL UNIQUE,
    original_url VARCHAR(2048) NOT NULL,
    user_id      BIGINT        NOT NULL REFERENCES users(id),
    active       BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_short_urls_user_id ON short_urls(user_id);