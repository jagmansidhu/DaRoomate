package com.roomate.app.service;

import com.roomate.app.dto.RegisterDto;
import com.roomate.app.dto.UpdateProfileDto;
import com.roomate.app.entities.UserEntity;
import org.springframework.dao.DuplicateKeyException;

public interface UserService {

    UserEntity updateUserProfile(String email, UpdateProfileDto updatedDetails);

    UserEntity getUserEntityByEmail(String email);

    boolean isProfileCompleteInDatabase(String email);

    boolean userExists(String email);

    String registerUser(RegisterDto req) throws DuplicateKeyException;
}
