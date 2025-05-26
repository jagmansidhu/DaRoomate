package com.roomate.app.service;

import com.roomate.app.domain.RequestContext;
import com.roomate.app.dto.User;
import com.roomate.app.entity.RoleEntity;
import com.roomate.app.entity.UserEntity;
import com.roomate.app.enumeration.LoginType;
import com.roomate.app.enumeration.Permissions;

public interface UserService {
    void createUser(String firstName, String lastName, String email, String password);
    RoleEntity getRoleName(String name);
    void verifyAccountCode(String code);
    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String apply);
}
