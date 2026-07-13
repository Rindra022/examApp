CREATE TABLE picture_submission
(
    id        UUID         NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_picture_submission PRIMARY KEY (id)
);