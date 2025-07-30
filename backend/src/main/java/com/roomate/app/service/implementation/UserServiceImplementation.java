package com.roomate.app.service.implementation;

import com.roomate.app.dto.UpdateProfileDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // EFFECTS :Checks if User currently exists in db
    @Override
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // EFFECTS : Creates a new user if user is not alreedy in database and then return user
    @Override
    @Transactional
    public UserEntity createUserByEmail(String email, String firstName, String lastName) {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        return userRepository.save(newUser);
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

    @Override
    public UserEntity updateUserEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        userEntity.setEmail(email);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUserFirstName(String email, String firstName) {
        UserEntity userEntity = getUserEntityByEmail(email);
        userEntity.setFirstName(firstName);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUserLastName(String email, String lastName) {
        UserEntity userEntity = getUserEntityByEmail(email);
        userEntity.setLastName(lastName);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUserPhone(String email, String phone) {
        UserEntity userEntity = getUserEntityByEmail(email);
        userEntity.setPhone(phone);
        return userRepository.save(userEntity);
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