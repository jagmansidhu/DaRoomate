package com.roomate.app.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("DEBUG: JwtAuthenticationFilter - Auth Header: " + authHeader);

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                jwt = authHeader.substring(7);
                System.out.println("DEBUG: JwtAuthenticationFilter - Extracted JWT: " + jwt);

                if (jwt.isEmpty() || jwt.isBlank()) {
                    System.out.println("DEBUG: JwtAuthenticationFilter - Extracted JWT is empty or blank. Skipping token validation.");
                } else {
                    username = jwtTokenUtil.extractUsername(jwt);
                    System.out.println("DEBUG: JwtAuthenticationFilter - Extracted Username: " + username);
                }
            } catch (JWTDecodeException e) {
                // WE HAVE ISSUE HERE ON LOGIN BUT NOT ON VIEWING SECRET MIGHT JUST BE LIKE THAT BECAUSE WE GET UUID THIS TIME AROUND
                // AND WHEN WE CALL FOR REGISTER WE GET THE ACTUAL JWT TOKEN
                System.err.println("ERROR: JwtAuthenticationFilter - JWTDecodeException: " + e.getMessage() + " for token: [" + jwt + "]");
            } catch (Exception e) {
                System.err.println("ERROR: JwtAuthenticationFilter - Unexpected error during token extraction: " + e.getMessage());
            }

        } else {
            System.out.println("DEBUG: JwtAuthenticationFilter - Authorization header is null or does not start with 'Bearer '.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(username);
                System.out.println("DEBUG: JwtAuthenticationFilter - UserDetails loaded for: " + username);
            } catch (Exception e) {
                System.err.println("ERROR: JwtAuthenticationFilter - Failed to load user details for " + username + ": " + e.getMessage());
            }


            if (userDetails != null && jwtTokenUtil.isTokenValid(jwt, userDetails.getUsername())) {
                System.out.println("DEBUG: JwtAuthenticationFilter - Token is valid for user: " + userDetails.getUsername());
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(token);
                System.out.println("DEBUG: JwtAuthenticationFilter - SecurityContextHolder populated.");
            } else {
                System.out.println("DEBUG: JwtAuthenticationFilter - Token invalid or UserDetails mismatch/null. Token validity: " + (userDetails != null ? jwtTokenUtil.isTokenValid(jwt, userDetails.getUsername()) : "N/A - UserDetails null"));
            }
        } else {
            System.out.println("DEBUG: JwtAuthenticationFilter - Username is null or SecurityContextHolder already has authentication.");
        }

        filterChain.doFilter(request, response);
        System.out.println("DEBUG: JwtAuthenticationFilter - Filter chain completed.");
    }
}