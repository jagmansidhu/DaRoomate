package com.roomate.app.service.implementation;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserEntity findOrCreateUserByAuthId(String authId, String email, String firstName, String lastName) {
        Optional<UserEntity> existingUser = userRepository.findByAuthId(authId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            UserEntity newUser = new UserEntity();
            newUser.setAuthId(authId);
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);

            return userRepository.save(newUser);
        }
    }

    public Optional<UserEntity> findUserByauthId(String authId) {
        return userRepository.findByAuthId(authId);
    }

    public UserEntity updateUserProfile(String authId, UserEntity updatedDetails) {
        UserEntity user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new RuntimeException("User not found for Auth0 ID: " + authId)); // Or a more specific exception

        user.setFirstName(updatedDetails.getFirstName());
        user.setLastName(updatedDetails.getLastName());
        user.setPhone(updatedDetails.getPhone());

        return userRepository.save(user);
    }

}