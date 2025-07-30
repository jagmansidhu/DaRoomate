package com.roomate.app.service;

import com.roomate.app.dto.UpdateProfileDto;
import com.roomate.app.entities.UserEntity;

public interface UserService {

    UserEntity updateUserProfile(String email, UpdateProfileDto updatedDetails);
    UserEntity getUserEntityByEmail(String email);

    boolean isProfileCompleteInDatabase(String email);
    boolean userExists(String email);

}
