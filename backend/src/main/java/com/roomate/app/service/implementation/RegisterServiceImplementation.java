package com.roomate.app.service.implementation;

import com.roomate.app.entities.RolesEntity;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.repository.RoleRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RegisterServiceImplementation implements RegisterService {
    @Autowired
    protected final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public RegisterServiceImplementation(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public ResponseEntity<?> register(UserEntity userEntity) throws UserApiError {
        if (this.userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            throw new UserApiError("User has already been registered with this email");
        }
        UserEntity user = new UserEntity();
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setEmail(userEntity.getEmail());
        user.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        user.setPhone(userEntity.getPhone());

        RolesEntity role = roleRepository.findByName("ROLE_USER");
        user.setRoles(Set.of(role));

        this.userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @Override
    public void userExists(UserEntity user) {

    }
}
