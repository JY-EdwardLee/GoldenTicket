CREATE TABLE group_member (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_group_member_group FOREIGN KEY (group_id)
        REFERENCES group_watch(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_group_member_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
