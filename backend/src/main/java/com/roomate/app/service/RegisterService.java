package com.roomate.app.service;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


public interface RegisterService {
    ResponseEntity<?> register(UserEntity user) throws UserApiError;
    void userExists(UserEntity user);
}
