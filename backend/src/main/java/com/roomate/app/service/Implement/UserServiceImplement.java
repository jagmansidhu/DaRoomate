package com.roomate.app.service.Implement;

import com.roomate.app.entity.ConfirmationEntity;
import com.roomate.app.entity.CredentialEntity;
import com.roomate.app.entity.RoleEntity;
import com.roomate.app.entity.UserEntity;
import com.roomate.app.enumeration.EventType;
import com.roomate.app.enumeration.Permissions;
import com.roomate.app.event.UserEvent;
import com.roomate.app.exception.ApiException;
import com.roomate.app.repository.ConfirmationRepository;
import com.roomate.app.repository.CredentialRepository;
import com.roomate.app.repository.RoleRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.roomate.app.util.UserUtil.createUserEntity;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final RoleRepository roleRepository;
//    private final BCryptPasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;

    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        var credentialEntity = new CredentialEntity(userEntity, password);
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getCode())));
    }

    @Override
    public RoleEntity getRoleName(String name) {
        var role = roleRepository.findByRoleName(name);
        return role.orElseThrow(() -> new ApiException("Role not found"));

    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Permissions.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }

}
