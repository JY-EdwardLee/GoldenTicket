CREATE TABLE Other_platform
(
    id            SERIAL PRIMARY KEY,
    platform      VARCHAR(30)  NOT NULL,        -- NOL", TICKETLINK
    price         INTEGER      NOT NULL,        -- 가격
    seat          VARCHAR(100) NOT NULL,        -- 좌석
    game_dateTime TIMESTAMP WITHOUT TIME ZONE NOT NULL,     -- game 정보와 일치
    home_team     VARCHAR(50)  NOT NULL,                    -- game 정보와 일치
    away_team     VARCHAR(50)  NOT NULL,                    -- game 정보와 일치
    user_email character varying(255) COLLATE pg_catalog."default" NOT NULL,    --
);
