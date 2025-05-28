package com.roomate.app.controller;

import com.roomate.app.dto.UserDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.implementation.UserServiceImplementation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserDto> all() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone()
                ))
                .collect(Collectors.toList());
    }
}