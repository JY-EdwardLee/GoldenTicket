CREATE TABLE waitlists
(
    waitlist_id BIGSERIAL PRIMARY KEY,

    ticket_id   BIGINT NOT NULL,                                       -- FK: tickets.id
    user_id     BIGINT NOT NULL,                                       -- FK: users.id (응모자)
    game_id     BIGINT NOT NULL,                                       -- FK: games.id
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                   -- 대기 등록 시각

    CONSTRAINT fk_waitlist_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets (ticket_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_waitlist_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_waitlist_game FOREIGN KEY (game_id)
        REFERENCES games (game_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_waitlist_ticket_user UNIQUE (ticket_id, user_id) -- 중복 응모 방지
);
