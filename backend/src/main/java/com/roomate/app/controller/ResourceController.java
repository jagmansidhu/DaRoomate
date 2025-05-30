package com.roomate.app.controller;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.service.UserService;
import com.roomate.app.service.implementation.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt; // Import Jwt
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResourceController {
    private final UserServiceImplementation userService;

    public ResourceController(UserServiceImplementation userService) {
        this.userService = userService;
    }

    @GetMapping("/public_resource")
    public ResponseEntity<String> getPublicResource() {
        return ResponseEntity.ok("This is a public resource from Spring Boot.");
    }

    @GetMapping("/secret_resource")
    public ResponseEntity<String> getSecretResource(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String userEmail = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("first_name");
        String lastName = jwt.getClaimAsString("last_name");

        UserEntity currentUser = userService.findOrCreateUserByAuthId(userId, userEmail, firstName, lastName);

        return ResponseEntity.ok("This is a secret resource accessed by user: " + firstName +
                " (Auth0 UID: " + userId + ", Local DB ID: " + currentUser.getId() + ", Email: " + userEmail + ")");
    }

    // @PutMapping("/profile")
    // public ResponseEntity<UserEntity> updateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UserEntity updatedDetails) {
    //     String auth0UserId = jwt.getSubject();
    //     UserEntity updatedUser = userService.updateUserProfile(auth0UserId, updatedDetails);
    //     return ResponseEntity.ok(updatedUser);
    // }

}