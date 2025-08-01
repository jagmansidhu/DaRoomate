package com.roomate.app.service.implementation;

import com.roomate.app.dto.RegisterDto;
import com.roomate.app.dto.UpdateProfileDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.JWTService;
import com.roomate.app.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;


    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public

    // EFFECTS :Checks if User currently exists in db
    @Override
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String registerUser(RegisterDto req) throws DuplicateKeyException {
        UserEntity user = new UserEntity();
        String email = req.getEmail();

        if(userExists(email)){
            throw new DuplicateKeyException(email);
        }

        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return jwtService.generateToken(user);
    }


    // EFFECTS : Updates user profile with new First name, Last name, and Phone num
    @Override
    public UserEntity updateUserProfile(String email, UpdateProfileDto updatedDetails) {
        UserEntity user = getUserEntityByEmail(email);

        if (!updatedDetails.getFirstName().isEmpty()) {
            user.setFirstName(updatedDetails.getFirstName().toLowerCase());
        }
        if (!updatedDetails.getLastName().isEmpty()) {
            user.setLastName(updatedDetails.getLastName().toLowerCase());
        }
        if (!updatedDetails.getEmail().isEmpty()) {
            user.setEmail(updatedDetails.getEmail().toLowerCase());
        }

        if (!updatedDetails.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedDetails.getPassword());
            user.setPassword(encodedPassword);
        }

        return userRepository.save(user);
    }

    // EFFECTS : Determines if profile is complete in the database
    @Override
    public boolean isProfileCompleteInDatabase(String email ) {
        UserEntity user = getUserEntityByEmail(email);

        boolean firstNameProvided = user.getFirstName() != null && !user.getFirstName().isEmpty();
        boolean lastNameProvided = user.getLastName() != null && !user.getLastName().isEmpty();
        boolean phoneProvided = user.getPhone() != null && !user.getPhone().isEmpty();

        return firstNameProvided & lastNameProvided & phoneProvided;
    }

    @Override
    public UserEntity getUserEntityByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> new User(
                        user.getEmail(),
                        user.getPassword(),
                        getAuthorities(user)
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserEntity user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoles()));
    }
}