//package com.roomate.app.service.implementation;
//
//import com.roomate.app.dto.LoginDto;
//import com.roomate.app.repository.UserRepository;
//import com.roomate.app.service.LoginService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
//@Service
//public class LoginServiceImplementation implements LoginService {
//    @Autowired
//    private final UserRepository userRepository;
//
//    @Autowired
//    private final AuthenticationManager authenticationManager;
//
//    @Autowired
//    private final JwtTokenUtil jwtTokenUtil;
//
//    public LoginServiceImplementation(UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
//        this.userRepository = userRepository;
//        this.authenticationManager = authenticationManager;
//        this.jwtTokenUtil = jwtTokenUtil;
//    }
//
//    @Override
//    public ResponseEntity<String> login(LoginDto loginDto, HttpServletRequest request) {
//
//        UsernamePasswordAuthenticationToken authenticationRequest =
//                new UsernamePasswordAuthenticationToken(
//                        loginDto.getEmail(),
//                        loginDto.getPassword()
//                );
//
//        try {
//            Authentication authentication = authenticationManager.authenticate(authenticationRequest);
//            System.out.println("Authentication SUCCESS for user: " + authentication.getName());
//
//            String token = jwtTokenUtil.generateToken(authentication.getName());
//
//            return ResponseEntity.ok(token);
//
//        } catch (org.springframework.security.core.AuthenticationException e) {
//            System.err.println("Authentication FAILED for user: " + loginDto.getEmail() + " - Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }
//
//}
