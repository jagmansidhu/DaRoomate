//package com.roomate.app.controller;
//
//import com.roomate.app.dto.LoginDto;
//import com.roomate.app.repository.UserRepository;
//import com.roomate.app.service.implementation.LoginServiceImplementation;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/user")
//public class LoginController {
//
//    private final LoginServiceImplementation loginService;
//
//    public LoginController(LoginServiceImplementation loginService) {
//        this.loginService = loginService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
//        try {
//            return loginService.login(loginDto, request);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }
//
//
//}
