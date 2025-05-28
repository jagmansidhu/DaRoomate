package com.roomate.app.service.implementation;

import com.roomate.app.entities.RolesEntity;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.repository.RoleRepository;
import com.roomate.app.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegisterServiceImplementation implements RegisterService {
    protected final com.roomate.app.repository.UserRepository userRepository;

    private final RoleRepository roleRepository;;
    private final PasswordEncoder passwordEncoder;

    public RegisterServiceImplementation(com.roomate.app.repository.UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
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
