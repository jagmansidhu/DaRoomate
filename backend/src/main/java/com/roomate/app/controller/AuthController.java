package com.roomate.app.controller;

import com.roomate.app.dto.LoginDto;
import com.roomate.app.dto.RegisterDto;
import com.roomate.app.dto.UserDTOS.UpdateProfileDto;
import com.roomate.app.dto.UserDTOS.UserDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.JWTService;
import com.roomate.app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;

    /** Default None for HTTPS/cross-origin Railway; set COOKIE_SAME_SITE=Lax after FE nginx proxy. */
    @Value("${app.cookie.same-site:None}")
    private String cookieSameSite;

    @GetMapping("/verify-status")
    public ResponseEntity<Map<String, Object>> getUserStatus(@AuthenticationPrincipal UserDetails user) {
        UserEntity usere = userRepository.getUserByEmail(user.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", usere != null);
        response.put("verified", usere.isEnabled());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@AuthenticationPrincipal UserDetails user) {
        UserEntity usere = userRepository.getUserByEmail(user.getUsername());
        if (!user.isEnabled()) {
            return ResponseEntity.badRequest().body("User already verified or not logged in");
        }
        String token = userService.createToken(usere);
        userService.sendVerificationEmail(user.getUsername(), token);
        return ResponseEntity.ok("Verification email resent");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto req) {
        try {
            userService.registerUser(req);
            return ResponseEntity.ok(Map.of("message", "Registration successful"));
        } catch (DuplicateKeyException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User with this email already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestParam("token") String token) {
        Boolean result = userService.verifyToken(token);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UpdateProfileDto req,
            HttpServletRequest request) {
        String email = request.getUserPrincipal().getName();
        UserEntity updatedUser = userService.updateUserProfile(email, req);
        return ResponseEntity.ok(UserDto.fromEntity(updatedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto req, HttpServletRequest request,
            HttpServletResponse response, CsrfToken csrfToken) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        UserEntity user = userService.getUserEntityByEmail(req.getEmail());
        String token = jwtService.generateToken(user);

        // Railway FE and API are different hosts today → need SameSite=None (same as main).
        // After FE nginx same-origin proxy is live, set COOKIE_SAME_SITE=Lax.
        boolean isSecure = request.isSecure()
                || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"));
        String sameSite = isSecure ? cookieSameSite : "Lax";

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(isSecure || "None".equalsIgnoreCase(sameSite))
                .path("/")
                .sameSite(sameSite)
                .maxAge(jwtService.getTokenExpirySeconds())
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        Map<String, Object> body = new HashMap<>();
        body.put("authenticated", true);
        if (csrfToken != null) {
            body.put("csrfToken", csrfToken.getToken());
        }
        return ResponseEntity.ok(body);
    }

    @GetMapping("/status")
    public ResponseEntity<?> authStatus(HttpServletRequest request, CsrfToken csrfToken) {
        Map<String, Object> body = new HashMap<>();
        if (csrfToken != null) {
            body.put("csrfToken", csrfToken.getToken());
        }

        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // Always 200 so the SPA can bootstrap CSRF even when logged out.
        // Cross-origin frontends cannot read the XSRF-TOKEN cookie; they use csrfToken from this body.
        if (token == null || token.isEmpty() || !token.contains(".")) {
            body.put("authenticated", false);
            return ResponseEntity.ok(body);
        }

        try {
            if (jwtService.isTokenValid(token)) {
                body.put("authenticated", true);
                body.put("username", jwtService.extractUsername(token));
                return ResponseEntity.ok(body);
            }
            body.put("authenticated", false);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            e.printStackTrace();
            body.put("authenticated", false);
            return ResponseEntity.ok(body);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        boolean isSecure = request.isSecure()
                || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"));
        String sameSite = isSecure ? cookieSameSite : "Lax";

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(isSecure || "None".equalsIgnoreCase(sameSite))
                .path("/")
                .sameSite(sameSite)
                .maxAge(0)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().build();
    }
}
