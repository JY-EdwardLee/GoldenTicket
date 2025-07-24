package com.ssafy.ticket_backend.model;

/**
 * 게시판 타입
 */
public enum BoardType {
    NOTICE,  // 공지 게시판
    FREE,    // 자유 게시판
    GROUPVIEW; // 단체관람 게시판

    public static BoardType fromString(String value) {
        try {
            return BoardType.valueOf(value); // 일치하는 경우만 enum 타입으로 변경
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("유효하지 않은 게시판 타입: " + value);
        }
    }
}