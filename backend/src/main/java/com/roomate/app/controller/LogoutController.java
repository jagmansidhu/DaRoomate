//package com.roomate.app.controller;
//
//import com.roomate.app.service.implementation.LogoutServiceImplementation;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/user")
//public class LogoutController {
//
//    @Autowired
//    private final LogoutServiceImplementation logoutServiceImplementation;
//
//    public LogoutController(LogoutServiceImplementation logoutServiceImplementation) {
//        this.logoutServiceImplementation = logoutServiceImplementation;
//    }
//
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        boolean loggedOut = logoutServiceImplementation.logoutUser(request, response, authentication);
//
//        if (loggedOut) {
//            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("No active session to log out or already logged out.", HttpStatus.OK);
//        }
//    }
//}
