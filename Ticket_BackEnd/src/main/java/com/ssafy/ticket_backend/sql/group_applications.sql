CREATE TABLE group_applications
(
    application_id SERIAL PRIMARY KEY,
    group_id       INT    NOT NULL,
    user_id        BIGINT NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 개인 신청시간 (1빠가 그룹장)
    -- front 에서 지원자수 0일때는 -> 그룹장 하시겠습니까?? 물어보고 들어오게

    CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES groups (group_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);


