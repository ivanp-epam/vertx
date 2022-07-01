-- liquibase formatted sql

-- changeset liquibase:3
-- CREATE TYPE permission AS ENUM ('READ', 'WRITE', 'DELETE', 'SHARE', 'UPLOAD_FILES');

CREATE TABLE groups
(
    id          uuid DEFAULT uuid_generate_v4(),
    name        VARCHAR NOT NULL,
    permissions TEXT[],
    PRIMARY KEY (id),
    CONSTRAINT
        groups_permissions_constraint
        CHECK(permissions <@ ARRAY['READ', 'WRITE', 'DELETE', 'SHARE', 'UPLOAD_FILES'])
);

CREATE TABLE users_groups
(
    id        uuid DEFAULT uuid_generate_v4(),
    users_id  uuid NOT NULL,
    groups_id uuid NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_groups__users
        FOREIGN KEY(users_id)
            REFERENCES users(id),
    CONSTRAINT fk_user_groups__groups
        FOREIGN KEY(groups_id)
            REFERENCES groups(id)
);

INSERT INTO groups(name, permissions)
    VALUES ('everything', array['READ', 'WRITE', 'DELETE', 'SHARE', 'UPLOAD_FILES']);

INSERT INTO users_groups(users_id, groups_id)
    SELECT users.id as user_id, groups.id as groups_id
    FROM users, groups;


-- rollback DROP TABLE users_groups;
-- rollback DROP TABLE groups;
