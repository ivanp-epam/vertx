-- liquibase formatted sql

-- changeset liquibase:1
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id         uuid DEFAULT uuid_generate_v4(),
    login      VARCHAR NOT NULL,
    password   VARCHAR NOT NULL,
    age        INTEGER,
    is_deleted BOOLEAN,
    PRIMARY KEY (id)
);

-- rollback DROP TABLE users;
-- rollback DROP EXTENSION "uuid-ossp";