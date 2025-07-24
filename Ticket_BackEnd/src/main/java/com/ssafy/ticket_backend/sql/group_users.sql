CREATE TABLE group_users
(
    group_user_id BIGSERIAL PRIMARY KEY,
    group_id      BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,

    CONSTRAINT fk_group_member_group FOREIGN KEY (group_id)
        REFERENCES group_entry (group_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_group_member_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
);
