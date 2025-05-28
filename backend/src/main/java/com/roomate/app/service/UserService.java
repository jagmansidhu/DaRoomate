package com.roomate.app.service;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void register(UserEntity user) throws UserApiError;
    void userExists(UserEntity user);
}
