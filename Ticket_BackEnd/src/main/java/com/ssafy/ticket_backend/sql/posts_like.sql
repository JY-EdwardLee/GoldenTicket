CREATE TABLE posts_like
(
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,

    CONSTRAINT fk_postlike_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_postlike_post FOREIGN KEY (post_id)
        REFERENCES posts (post_id)
        ON DELETE CASCADE,

    CONSTRAINT pk_post_like PRIMARY KEY (user_id, post_id) -- 중복 좋아요 방지
);
