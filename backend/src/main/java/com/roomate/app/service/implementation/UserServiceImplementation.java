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

    // EFFECTS : Creates a new user if user is not alreedy in database and then return user
    @Override
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

    // EFFECTS : Updates user profile with new First name, Last name, and Phone num
    @Override
    public UserEntity updateUserProfile(String authId, UserEntity updatedDetails) {
        UserEntity user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new RuntimeException("User not found for Auth0 ID: " + authId));

        user.setFirstName(updatedDetails.getFirstName().toLowerCase());
        user.setLastName(updatedDetails.getLastName().toUpperCase());
        user.setPhone(updatedDetails.getPhone());

        return userRepository.save(user);
    }

    // Effects : Determines if profile is complete in the database
    @Override
    public boolean isProfileCompleteInDatabase(String authId) {
        UserEntity user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new RuntimeException("User not found for Auth0 ID: " + authId));

        boolean firstNameProvided = user.getFirstName() != null && !user.getFirstName().isEmpty();
        boolean lastNameProvided = user.getLastName() != null && !user.getLastName().isEmpty();
        boolean phoneProvided = user.getPhone() != null && !user.getPhone().isEmpty();

        return firstNameProvided & lastNameProvided & phoneProvided;
    }
}