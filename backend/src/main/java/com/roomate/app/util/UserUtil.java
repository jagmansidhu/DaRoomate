package com.roomate.app.util;

import com.roomate.app.entity.RoleEntity;
import com.roomate.app.entity.UserEntity;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.EMPTY;


public class UserUtil {

    public static UserEntity createUserEntity (String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .lastLogin(now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(false)
                .mfa(false)
                .loginAttempts(0)
                .QrCodeSecret(EMPTY)
//                .phone(EMPTY)
                .bio(EMPTY)
                .imageUrl(EMPTY)
                .roles(role)
                .build();

    }
}
