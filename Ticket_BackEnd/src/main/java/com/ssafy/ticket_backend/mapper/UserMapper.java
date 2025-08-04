package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.request.UserSignupRequest;
import com.ssafy.ticket_backend.dto.response.LoginUserResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.PostUserResponse;
import com.ssafy.ticket_backend.model.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    // 유저 정보 조회(커뮤니티 용)
    User selectUserByEmail(String email);

    // 유저 정보 조회(커뮤니티 용)
    PostUserResponse selectUserByPostId(Long PostId);

    // 유저 회원가입 등록
    void insertUser(UserSignupRequest userSignupRequest);

    // 로그인한 유저 정보 조회
    LoginUserResponse selectLogingUserByEmail(String email);

    // 마이페이지 조회
    MyPageResponse getMyPageByEmail(String email);

    // 개인 정보 수정
    void updateUser(@Param("email") String email,
        @Param("userPatchRequest") UserPatchRequest userPatchRequest);

    // S3 사용자 이미지 업데이트
    int updateUserProfileImage(@Param("userId") Long userId, @Param("imageKey") String imageKey);

    // S3 사용자 프로필 이미지 key 조회
    String getUserProfileImageKey(Long userId);

    // 회원 탈퇴
    void deleteUserByEmail(String email);

    // 더미데이터 용도
    List<String> getUserEmailForDummy();

    User selectUserByUserId(@Param("userId") Long userId);

    void decreaseWeightByUserId(@Param("userId") Long userId);

    void increaseWeightByUserId(@Param("userId") Long userId);
}
