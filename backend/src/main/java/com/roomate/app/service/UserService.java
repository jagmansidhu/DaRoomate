package com.roomate.app.service;

import com.roomate.app.entities.UserEntity;

public interface UserService {

    UserEntity findOrCreateUserByAuthId(String authId, String email, String firstName, String lastName);
    UserEntity updateUserProfile(String authId, UserEntity updatedDetails);
    boolean isProfileCompleteInDatabase(String authId);

}
