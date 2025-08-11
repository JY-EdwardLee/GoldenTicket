package com.ssafy.ticket_backend.handler.service;

import com.ssafy.ticket_backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// 스프링 시큐리티 인증 시 유저 정보 불러오기
// UserServiceImpl은 비즈니스 로직 담당하는 서비스 계층이니
// 역할 분리 및 유지보수 위함
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userService.selectUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);

        }

        return new CustomUserDetails(user);
    }
}
