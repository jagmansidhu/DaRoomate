package com.roomate.app.service.implementation;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.service.UserService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

public class UserServiceImplementation implements UserService, UserDetailsService {
    protected final com.roomate.app.repository.UserRepository userRepository;

    public UserServiceImplementation(com.roomate.app.repository.UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void register(UserEntity user) throws UserApiError {
        if (this.userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserApiError("User has already been registered with this email");
        }
        this.userRepository.save(user);
    }

    @Override
    public void userExists(UserEntity user) {

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        Set<GrantedAuthority> authorities = user.getRoles().stream().map((rolesEntity) ->
                new SimpleGrantedAuthority(rolesEntity.getName())).collect(Collectors.toSet());

        return new User(user.getEmail(), user.getPassword(), authorities);

    }
}
