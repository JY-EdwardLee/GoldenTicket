CREATE TABLE group_watch (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    user_cnt INTEGER DEFAULT 0,

    CONSTRAINT fk_group_game FOREIGN KEY (game_id)
        REFERENCES games(id)
        ON DELETE CASCADE
);
