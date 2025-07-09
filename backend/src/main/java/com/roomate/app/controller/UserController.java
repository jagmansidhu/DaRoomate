package com.roomate.app.controller;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.entities.roleEntity.RolesEntity;
import com.roomate.app.repository.RoleRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.implementation.UserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserServiceImplementation userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserServiceImplementation userService, UserRepository userRepository, RoleRepository roleRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // EFFECTS : Adds additional information: First Name, Last Name, and Phone Number to user
    @PutMapping("/additional_info")
    public ResponseEntity<?> addAdditionalInfo(@AuthenticationPrincipal Jwt jwt, @RequestBody UserEntity updatedDetails) {
        String userId = jwt.getSubject();
        UserEntity updateUser = userService.updateUserProfile(userId, updatedDetails);

        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    // EFFECTS : Determines if user information has been inputted and complete
    @GetMapping("/profile-status")
    public ResponseEntity<Map<String, Boolean>> getProfileCompletionStatus(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        boolean isComplete = userService.isProfileCompleteInDatabase(auth0UserId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isComplete", isComplete);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // EFFECTS : Checks if user exists and return user, if no user create the user
    @GetMapping("/create_or_find_user")
    public ResponseEntity<UserEntity> createOrFindUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("first_name");
        String lastName = jwt.getClaimAsString("last_name");

        if (userService.userExists(userId, email)) {
            return ResponseEntity.ok(userService.getUserEntityByAuthID(userId));
        }

        UserEntity user = userService.createUserByAuthID(userId, email, firstName, lastName);

        roleRepository.findByName("ROLE_ROOMMATE").ifPresent(role -> user.setRoles(Set.of(role)));

        userRepository.save(user);

        return ResponseEntity.ok(userService.getUserEntityByAuthID(userId));
    }

}