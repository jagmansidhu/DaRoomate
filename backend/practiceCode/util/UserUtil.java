package com.roomate.app.util;

import com.roomate.app.dto.User;
import com.roomate.app.entity.CredentialEntity;
import com.roomate.app.entity.RoleEntity;
import com.roomate.app.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

import static com.roomate.app.constant.Constant.NINETY_DAYS_CREDENTIALS_EXPIRY;
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

    public static User fromUserEntity(UserEntity user, RoleEntity roles, CredentialEntity userCredentialsEntity) {
        User userDto = new User();

        BeanUtils.copyProperties(user, userDto);
        userDto.setLastLogin(user.getLastLogin().toString());
        userDto.setCredentialsNonExpired(isCredentialNonExpired(userCredentialsEntity));
        userDto.setCreatedAt(user.getCreatedAt().toString());
        userDto.setUpdateAt(user.getUpdatedAt().toString());
        userDto.setRoles(roles.getRoleName());
        userDto.setAuthorities(roles.getAuthorities().getValue());
        return userDto;
    }

    public static boolean isCredentialNonExpired(CredentialEntity userCredentialsEntity) {
        return userCredentialsEntity.getUpdatedAt().plusDays(NINETY_DAYS_CREDENTIALS_EXPIRY).isAfter(now());
    }

}
