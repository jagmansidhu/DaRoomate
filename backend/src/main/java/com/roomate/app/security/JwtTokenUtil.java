package com.roomate.app.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit; // Import for TimeUnit

@Component
public class JwtTokenUtil {

    // NEED TO CHANGE!!!
//    @Value("${jwt.secret}")
    private String SECRET_KEY = "secret";

//    @Value("${jwt.expiration.minutes}")
    private long JWT_EXPIRATION_MINUTES = 60;

    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());

        long nowMillis = System.currentTimeMillis();
        Date issuedAt = new Date(nowMillis);
        Date expiresAt = new Date(nowMillis + TimeUnit.MINUTES.toMillis(JWT_EXPIRATION_MINUTES));

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }


    private DecodedJWT getDecodedJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception){
            System.err.println("JWT Verification failed: " + exception.getMessage());
            throw exception;
        }
    }

    public String extractUsername(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT.getSubject();
    }

//    public Date extractExpiration(String token) {
//        DecodedJWT decodedJWT = getDecodedJWT(token);
//        return decodedJWT.getExpiresAt();
//    }

    public Boolean isTokenValid(String token, String username) {
        try {
            DecodedJWT decodedJWT = getDecodedJWT(token); // Validates token and checks signature, expiration
            final String extractedUsername = decodedJWT.getSubject();
            return (extractedUsername.equals(username));
        } catch (JWTVerificationException e) {
            return false;
        }
    }

//    public Boolean isTokenExpired(String token) {
//        try {
//            DecodedJWT decodedJWT = JWT.decode(token);
//            return decodedJWT.getExpiresAt().before(new Date());
//        } catch (com.auth0.jwt.exceptions.JWTDecodeException e) {
//            System.err.println("Error decoding token for expiration check: " + e.getMessage());
//            return true;
//        }
//    }
}