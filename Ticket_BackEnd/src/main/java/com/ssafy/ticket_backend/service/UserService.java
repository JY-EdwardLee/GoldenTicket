package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.model.User;


public interface UserService {
    User selectUserByEmail(String email);

}
