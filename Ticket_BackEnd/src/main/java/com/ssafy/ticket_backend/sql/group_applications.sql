CREATE TABLE group_applications
(
    application_id SERIAL PRIMARY KEY,
    group_id       INT    NOT NULL,
    user_id        BIGINT NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES groups (group_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id),

    CONSTRAINT uq_group_user UNIQUE (group_id, user_id)
);
