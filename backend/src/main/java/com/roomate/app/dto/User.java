package com.roomate.app.dto;

import lombok.Data;

@Data
public class User {
    private Long id;
    private Long createdBy;
    private Long updateBy;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    //    private String phone;
    private String bio;
    private String imageUrl;
    private String QrCodeSecret;
    private String QrCodeImageUri;
    private String lastLogin;
    private String createdAt;
    private String updateAt;
    private String roles;
    private String authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean mfa;
}
