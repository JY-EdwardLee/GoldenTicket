CREATE TABLE boards
(
    id   BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL -- 예: 공지사항, 자유게시판, 후기 등
);
