package com.roomate.app.controller;

import com.roomate.app.dto.UserDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.implementation.RegisterServiceImplementation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class RegisterController {
    private final RegisterServiceImplementation registerService;

    private final UserRepository userRepository;

    public RegisterController(RegisterServiceImplementation registerService, UserRepository userRepository) {
        this.registerService = registerService;
        this.userRepository = userRepository;
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerApi(@RequestBody UserEntity user, HttpServletRequest request) throws UserApiError {
        return registerService.register(user);
    }

//    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/all")
    @ResponseBody
    public List<UserDto> all() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getPassword()
                ))
                .collect(Collectors.toList());
    }
}