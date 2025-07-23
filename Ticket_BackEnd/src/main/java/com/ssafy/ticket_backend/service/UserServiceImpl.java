package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    
    @Override
    public User selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }
}
