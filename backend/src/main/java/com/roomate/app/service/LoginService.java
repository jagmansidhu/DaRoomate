package com.roomate.app.service;

import com.roomate.app.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface LoginService {
    ResponseEntity<String> login(LoginDto loginDto, HttpServletRequest request);
}
