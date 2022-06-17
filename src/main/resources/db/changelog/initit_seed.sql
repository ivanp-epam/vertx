-- liquibase formatted sql

-- changeset liquibase:2
INSERT INTO users(login, password, age, is_deleted)
VALUES ('John Doe', '123123', 10, false),
       ('John Smith', '123123', 11, false),
       ('Michael Doe', '123123', 12, false),
       ('Michael Smith', '123123', 13, false),
       ('Benjamin Doe', '123123', 14, false),
       ('Benjamin Smith', '123123', 15, false);

-- rollback select 1;