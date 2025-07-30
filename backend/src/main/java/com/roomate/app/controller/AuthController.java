package com.roomate.app.controller;

import com.roomate.app.dto.AuthDto;
import com.roomate.app.dto.LoginDto;
import com.roomate.app.dto.RegisterDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.entities.roleEntity.RoleEnum;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.JWTService;
import com.roomate.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {

    private final UserService userService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthDto> register(@RequestBody RegisterDto req) {
        UserEntity user = new UserEntity();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@RequestBody LoginDto req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        UserEntity user = userService.getUserEntityByEmail(req.getEmail());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthDto(token));
    }
}



