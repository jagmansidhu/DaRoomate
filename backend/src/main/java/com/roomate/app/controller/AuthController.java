package com.roomate.app.controller;

import com.roomate.app.dto.AuthDto;
import com.roomate.app.dto.LoginDto;
import com.roomate.app.dto.RegisterDto;
import com.roomate.app.dto.UpdateProfileDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.service.JWTService;
import com.roomate.app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<AuthDto> register(@Valid @RequestBody RegisterDto req) {
        String token = userService.registerUser(req);
        return ResponseEntity.ok(new AuthDto(token));
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserEntity> updateProfile(@Valid @RequestBody UpdateProfileDto req, HttpServletRequest request) {
        String email = request.getUserPrincipal().getName();
        UserEntity updatedUser = userService.updateUserProfile(email, req);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@Valid @RequestBody LoginDto req, HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                Cookie cleared = new Cookie(cookie.getName(), "");
                cleared.setPath("/");
                cleared.setMaxAge(0);
                cleared.setHttpOnly(true);
                response.addCookie(cleared);
            }
        }

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        UserEntity user = userService.getUserEntityByEmail(req.getEmail());

        String token = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new AuthDto(token));
    }

    @GetMapping("/status")
    public ResponseEntity<?> authStatus(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            System.out.println("Cookies are null");
            return ResponseEntity.status(401).body("Cookie Null");
        }

        String token = null;
        for (Cookie cookie : cookies) {
            System.out.println("Found cookie: " + cookie.getName() + " = " + cookie.getValue());
            if ("jwt".equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }

        if (token == null || token.isEmpty() || !token.contains(".")) {
            System.out.println("Malformed or missing token");
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        try {
            if (jwtService.isTokenValid(token)) {
                String user = jwtService.extractUsername(token);
                return ResponseEntity.ok(Map.of("username", user));
            } else {
                return ResponseEntity.status(401).body("Invalid token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Malformed token");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.setHeader("Set-Cookie", "jwt=; Path=/; HttpOnly; Max-Age=0; SameSite=Lax");
        return ResponseEntity.ok().build();
    }
}
