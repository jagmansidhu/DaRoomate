package com.roomate.app.controller;

import com.roomate.app.dto.AuthDto;
import com.roomate.app.dto.LoginDto;
import com.roomate.app.dto.RegisterDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.service.JWTService;
import com.roomate.app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {
    private final boolean ISPROD = false;
    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<AuthDto> register(@RequestBody RegisterDto req) {
        String token = userService.registerUser(req);
        return ResponseEntity.ok(new AuthDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@RequestBody LoginDto req, HttpServletResponse response) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        UserEntity user = userService.getUserEntityByEmail(req.getEmail());

        String token = jwtService.generateToken(user);

        // Create cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(7 * 24 * 60 * 60);

        if (ISPROD) {
            jwtCookie.setSecure(true);
            response.setHeader("Set-Cookie", String.format(
                    "jwt=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=None; Secure",
                    token,
                    7 * 24 * 60 * 60
            ));
        } else {
            response.setHeader("Set-Cookie", String.format(
                    "jwt=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=Lax",
                    token,
                    7 * 24 * 60 * 60
            ));
        }


        return ResponseEntity.ok(new AuthDto(token));
    }


    @GetMapping("/status")
    public ResponseEntity<?> authStatus(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            System.out.println("cookies is null");
            return ResponseEntity.status(401).body("Cookie Null");
        }

        System.out.println("Cookies:");
        for (Cookie cookie : request.getCookies()) {
            System.out.println(cookie.getName() + " = " + cookie.getValue());
        }

        String token = null;
        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }
        System.out.println("Extracted JWT token: " + token);

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body("Token Empty");
        }

        if (jwtService.isTokenValid(token)) {
            String user = jwtService.extractUsername(token);
            return ResponseEntity.ok(Map.of("username", user));
        } else {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie", "jwt=; Path=/; HttpOnly; Max-Age=0; SameSite=Lax");
        return ResponseEntity.ok().build();
    }
}
