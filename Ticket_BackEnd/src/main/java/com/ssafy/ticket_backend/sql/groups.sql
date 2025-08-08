CREATE TABLE groups
(
    group_id         SERIAL PRIMARY KEY,
    game_id          BIGINT NOT NULL,
    is_active        BOOLEAN DEFAULT TRUE, -- 모집 중 여부 (정원 20)
    is_ended         BOOLEAN DEFAULT TRUE, -- 경기 종료 여부
    applicants_count INTEGER DEFAULT 0,    -- 현재 신청 인원
    CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES games (game_id)
);