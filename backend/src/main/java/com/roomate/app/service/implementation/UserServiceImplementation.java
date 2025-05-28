package com.roomate.app.service.implementation;

import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.service.UserService;

public class UserServiceImplementation implements UserService {
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
}
