package com.roomate.app.controller;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Map;


@Controller
@RequestMapping("/api/profile/")
public class ProfileController {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String auth0Domain;

    @Value("${auth0.client-id}")
    private String auth0ClientId;

    @Value("${auth0.client-secret}")
    private String auth0ClientSecret;

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
//        String email = payload.get("email");
//        if (email == null || email.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Email is required."));
//        }
//
//        try {
//            AuthAPI authAPI = new AuthAPI(auth0Domain, auth0ClientId, auth0ClientSecret);
//
//            authAPI.resetPassword(email, "Username-Password-Authentication")
//                    .execute();
//
//            return ResponseEntity.ok(Collections.singletonMap("message", "Password reset email sent successfully."));
//
//        } catch (Auth0Exception e) {
//            System.err.println("Auth0 password reset error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("message", "Failed to send password reset email: " + e.getMessage()));
//        }
//    }

    @PutMapping("/updateEmail")
    public ResponseEntity<?> updateEmail(@AuthenticationPrincipal Jwt jwt, @RequestBody String updatedDetails) {
        String userId = jwt.getSubject();
        UserEntity updateUser = userService.updateUserEmail(userId, updatedDetails);

        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }
}
