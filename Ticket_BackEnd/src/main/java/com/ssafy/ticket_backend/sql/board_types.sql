CREATE TABLE board_types
(
    type VARCHAR(50) PRIMARY KEY -- 공지사항, 자유게시판 등 고유하게 사용
);


INSERT INTO board_types (type)
VALUES ('NOTICE'),
       ('FREE'),
       ('GROUPVIEW');
