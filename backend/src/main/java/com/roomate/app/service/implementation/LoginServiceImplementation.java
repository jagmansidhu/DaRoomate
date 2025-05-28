package com.roomate.app.service.implementation;

import com.roomate.app.dto.LoginDto;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImplementation implements LoginService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final AuthenticationManager authenticationManager;

    public LoginServiceImplementation(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public ResponseEntity<String> login(LoginDto loginDto, HttpServletRequest request) {

        System.out.println("Attempt login with: " + loginDto.getEmail() + " / " + loginDto.getPassword());

        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                );
        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationRequest);
            System.out.println("Authentication SUCCESS for user: " + authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession();
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            return ResponseEntity.ok().build();
        } catch (org.springframework.security.core.AuthenticationException e) {
            System.err.println("Authentication FAILED for user: " + loginDto.getEmail() + " - Error: " + e.getMessage());
            throw e;
        }
    }
}
