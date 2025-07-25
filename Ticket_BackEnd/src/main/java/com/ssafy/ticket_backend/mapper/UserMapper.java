package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.request.UserSignupRequest;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    // 유저 정보 조회
    User selectUserByEmail(@Param("email") String email);

    // 유저 회원가입 등록
    void insertUser(UserSignupRequest userSignupRequest);

    // 마이페이지 조회
    MyPageResponse getMyPageByEmail(String email);

    // 개인 정보 수정
    void updateUser(@Param("email") String email,
        @Param("userPatchRequest") UserPatchRequest userPatchRequest);
}
