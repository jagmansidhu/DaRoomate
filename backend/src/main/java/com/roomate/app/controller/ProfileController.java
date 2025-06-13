package com.roomate.app.controller;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/profile/")
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // EFFECTS : Updates User Email
    @PutMapping("/updateEmail")
    public ResponseEntity<?> updateEmail(@AuthenticationPrincipal Jwt jwt, @RequestBody String updatedDetails) {
        String userId = jwt.getSubject();
        UserEntity updateUser = userService.updateUserEmail(userId, updatedDetails);

        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

}
