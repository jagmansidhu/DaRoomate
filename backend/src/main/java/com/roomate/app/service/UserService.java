package com.roomate.app.service;

import com.roomate.app.entities.UserEntity;

public interface UserService {

    UserEntity createUserByAuthID(String authId, String email, String firstName, String lastName);
    UserEntity updateUserProfile(String authId, UserEntity updatedDetails);
    UserEntity getUserEntityByAuthID(String authId);
    UserEntity updateUserEmail(String authId, String email);
    UserEntity updateUserFirstName(String authId, String firstName);
    UserEntity updateUserLastName(String authId, String lastName);
    UserEntity updateUserPhone(String authId, String phone);

    boolean isProfileCompleteInDatabase(String authId);
    boolean userExists(String authId, String email);

}
