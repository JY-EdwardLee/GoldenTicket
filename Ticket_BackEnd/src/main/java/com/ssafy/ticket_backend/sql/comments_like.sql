CREATE TABLE comment_like (
    user_id INT NOT NULL,
    comment_id INT NOT NULL,

    CONSTRAINT fk_commentlike_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_commentlike_comment FOREIGN KEY (comment_id)
        REFERENCES comments(id)
        ON DELETE CASCADE,

    CONSTRAINT pk_comment_like PRIMARY KEY (user_id, comment_id)
);
