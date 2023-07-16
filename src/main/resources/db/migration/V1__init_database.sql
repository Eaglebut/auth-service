CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users
(
	id         UUID PRIMARY KEY       DEFAULT gen_random_uuid(),
	username   VARCHAR(64)   NOT NULL UNIQUE,
	password   VARCHAR(2048) NOT NULL,
	role       VARCHAR(32)   NOT NULL,
	first_name VARCHAR(64)   NOT NULL,
	last_name  VARCHAR(64)   NOT NULL,
	enabled    BOOLEAN       NOT NULL DEFAULT FALSE,
	created_at TIMESTAMP,
	updated_at TIMESTAMP
);