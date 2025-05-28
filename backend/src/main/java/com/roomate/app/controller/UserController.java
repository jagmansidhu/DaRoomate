package com.roomate.app.controller;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.UserService;
import com.roomate.app.service.implementation.UserServiceImplementation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController extends UserServiceImplementation {

    public UserController(UserRepository userRepository) {
        super(userRepository);
    }

    @PostMapping("/user/register")
    public void registerApi(@RequestBody UserEntity user) throws UserApiError {
        register(user);
    }

    @GetMapping("/user")
    @ResponseBody
    List<UserEntity> all() {
        return userRepository.findAll();
    }
}