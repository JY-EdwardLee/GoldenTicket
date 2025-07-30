CREATE TABLE Other_platform
(
    id            SERIAL PRIMARY KEY,
    platform      VARCHAR(30)  NOT NULL,
    price         INTEGER      NOT NULL,
    seat          VARCHAR(100) NOT NULL,
    game_dateTime TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    home_team     VARCHAR(50)  NOT NULL,
    away_team     VARCHAR(50)  NOT NULL,
    user_email character varying(255) COLLATE pg_catalog."default" NOT NULL,
);
