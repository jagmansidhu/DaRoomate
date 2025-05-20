package com.roomate.app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfig {

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    // EFFECT: Creates a UserDetailsService with two users: user and user2
    //         meaning I can authenticate as both users instead of spring default.
    @Bean
    public UserDetailsService userDetailsService() {
        var user1 = User.withDefaultPasswordEncoder()
                .username("user")
                .password("monkey")
                .roles("USER")
                .build();

        var user2 = User.withDefaultPasswordEncoder()
                .username("user2")
                .password("yes")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(List.of(user1, user2));
    }

}
