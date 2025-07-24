package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    // 유저 정보 조회
    User selectUserByEmail(String email);

    // 유저 회원가입 등록
    void insertUser(User user);
}
