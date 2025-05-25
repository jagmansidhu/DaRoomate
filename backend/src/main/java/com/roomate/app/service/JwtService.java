package com.roomate.app.service;

import com.roomate.app.domain.Token;
import com.roomate.app.domain.TokenData;
import com.roomate.app.dto.User;
import com.roomate.app.enumeration.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> tokenstringFunction);
    Optional<String> extractToken(HttpServletRequest request, String tokenType);
    void addCookie(HttpServletResponse response, User user, TokenType tokenType);
    <T> T getTokenData(HttpServletRequest request, String token, Function<TokenData, T> tokenFunction);
}
