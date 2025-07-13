package com.roomate.app.service.implementation;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.RoleRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
    }

    // EFFECTS :Checks if User currently exists in db
    @Override
    public boolean userExists(String authId, String email) {
        return userRepository.existsByEmail(email) && userRepository.existsByAuthId(authId);
    }

    // EFFECTS : Creates a new user if user is not alreedy in database and then return user
    @Override
    @Transactional
    public UserEntity createUserByAuthID(String authId, String email, String firstName, String lastName) {
        UserEntity newUser = new UserEntity();
        newUser.setAuthId(authId);
        newUser.setEmail(email);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        return userRepository.save(newUser);
    }

    // EFFECTS : Updates user profile with new First name, Last name, and Phone num
    @Override
    public UserEntity updateUserProfile(String authId, UserEntity updatedDetails) {
        UserEntity user = getUserEntityByAuthID(authId);

        if (!updatedDetails.getFirstName().isEmpty()) {
            user.setFirstName(updatedDetails.getFirstName().toLowerCase());
        }
        if (!updatedDetails.getLastName().isEmpty()) {
            user.setLastName(updatedDetails.getLastName().toLowerCase());
        }
        if (!updatedDetails.getPhone().isEmpty()) {
            user.setPhone(updatedDetails.getPhone().toLowerCase());
        }

        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUserEmail(String authId, String email) {
        UserEntity userEntity = getUserEntityByAuthID(authId);
        userEntity.setEmail(email);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUserFirstName(String authId, String firstName) {
        UserEntity userEntity = getUserEntityByAuthID(authId);
        userEntity.setFirstName(firstName);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUserLastName(String authId, String lastName) {
        UserEntity userEntity = getUserEntityByAuthID(authId);
        userEntity.setLastName(lastName);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateUserPhone(String authId, String phone) {
        UserEntity userEntity = getUserEntityByAuthID(authId);
        userEntity.setPhone(phone);
        return userRepository.save(userEntity);
    }

    // EFFECTS : Determines if profile is complete in the database
    @Override
    public boolean isProfileCompleteInDatabase(String authId) {
        UserEntity user = getUserEntityByAuthID(authId);

        boolean firstNameProvided = user.getFirstName() != null && !user.getFirstName().isEmpty();
        boolean lastNameProvided = user.getLastName() != null && !user.getLastName().isEmpty();
        boolean phoneProvided = user.getPhone() != null && !user.getPhone().isEmpty();

        return firstNameProvided & lastNameProvided & phoneProvided;
    }

    @Override
    public UserEntity getUserEntityByAuthID(String authId) {
        UserEntity user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new RuntimeException("User not found for Auth0 ID: " + authId));
        return user;
    }
}