CREATE TABLE users
(
    user_id           BIGSERIAL PRIMARY KEY,

    email             VARCHAR(255) NOT NULL UNIQUE,
    gender            VARCHAR(10),
    user_name         VARCHAR(100),
    user_role         VARCHAR(50),
    nickname          VARCHAR(100),
    birth_date        DATE,
    profile_photo_url TEXT,
    phone_number      VARCHAR(20),

    my_team           VARCHAR(50),
    social_provider   VARCHAR(50),
    weight            NUMERIC(5, 2) DEFAULT 0.0, -- 0.0~999.99
    transfer_number   INTEGER       DEFAULT 0,
    receive_number    INTEGER       DEFAULT 0,
    panelty_point     INTEGER       DEFAULT 0,
    is_block          BOOLEAN       DEFAULT FALSE,
    is_delete         BOOLEAN       DEFAULT FALSE
);