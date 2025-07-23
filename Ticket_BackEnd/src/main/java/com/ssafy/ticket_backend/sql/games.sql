CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,               
	
    game_datetime TIMESTAMP NOT NULL,       -- 경기 일시 (더블헤더 포함)
    home_team VARCHAR(50) NOT NULL,         -- 홈팀
    away_team VARCHAR(50) NOT NULL,      	-- 어웨이 팀
    is_cancelled BOOLEAN DEFAULT FALSE      -- ex. 우천취소
);
