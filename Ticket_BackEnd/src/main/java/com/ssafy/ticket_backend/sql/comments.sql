CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,

    post_id BIGINT NOT NULL,          -- FK: post.id
    user_id BIGINT NOT NULL,          -- FK: users.id

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    content TEXT NOT NULL,
    like_count INTEGER DEFAULT 0,
    is_delete BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_comment_post FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comment_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
