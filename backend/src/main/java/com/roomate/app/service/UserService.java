package com.roomate.app.service;

import com.roomate.app.entities.UserEntity;

public interface UserService {

    UserEntity createUserByEmail(String email, String firstName, String lastName);
    UserEntity updateUserProfile(String email, UserEntity updatedDetails);
    UserEntity updateUserEmail(String email);
    UserEntity updateUserFirstName(String email, String firstName);
    UserEntity updateUserLastName(String email, String lastName);
    UserEntity updateUserPhone(String email, String phone);
    UserEntity getUserEntityByEmail(String email);

    boolean isProfileCompleteInDatabase(String email);
    boolean userExists(String email);

}
