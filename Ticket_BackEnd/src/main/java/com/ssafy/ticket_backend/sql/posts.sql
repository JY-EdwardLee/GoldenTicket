CREATE TABLE posts
(
    post_id    BIGSERIAL PRIMARY KEY,

    board_id   VARCHAR(50)  NOT NULL, -- FK: board.id
    user_id    BIGINT       NOT NULL, -- FK: users.id

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    view_count INTEGER   DEFAULT 0,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    image_url  TEXT,

    like_count INTEGER   DEFAULT 0,
    is_delete  BOOLEAN   DEFAULT FALSE,

    CONSTRAINT fk_post_board FOREIGN KEY (board_id)
        REFERENCES board_types (type)
        ON DELETE CASCADE,

    CONSTRAINT fk_post_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
);
