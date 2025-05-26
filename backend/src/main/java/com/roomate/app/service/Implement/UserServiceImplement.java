package com.roomate.app.service.Implement;

import com.roomate.app.cache.CacheStore;
import com.roomate.app.domain.RequestContext;
import com.roomate.app.dto.User;
import com.roomate.app.entity.ConfirmationEntity;
import com.roomate.app.entity.CredentialEntity;
import com.roomate.app.entity.RoleEntity;
import com.roomate.app.entity.UserEntity;
import com.roomate.app.enumeration.EventType;
import com.roomate.app.enumeration.LoginType;
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

import java.time.LocalDateTime;
import java.util.Map;

import static com.roomate.app.util.UserUtil.createUserEntity;
import static com.roomate.app.util.UserUtil.fromUserEntity;

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
    private final CacheStore<String, Integer> userCache;
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

    @Override
    public void verifyAccountCode(String code) {
        var confirmationEntity = getUserConfirmation(code);
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (userCache.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(1);
                    userEntity.setAccountNonLocked(true);
                    userCache.put(email, 0);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userCache.put(email, userCache.get(userEntity.getEmail()) + 1);

                if (userEntity.getLoginAttempts() >= 3) {
                    userEntity.setAccountNonLocked(false);
                    log.info("User account locked, too many Login attempts");
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setLoginAttempts(0);
                userEntity.setAccountNonLocked(true);
                userEntity.setLastLogin(LocalDateTime.now());
                userCache.invalidate(userEntity.getEmail());
                log.info("User account unlocked");
            }
        }

        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String id) {
        return null;
    }

    @Override
    public User getUserByEmail(String email){
        UserEntity user = getUserEntityByEmail(email);
        return fromUserEntity(user, user.getRoles(), getUserCredentialsById(user.getId()));
    }

    @Override
    public CredentialEntity getUserCredentialsById(Long id) {
        var credentialByUserEntityId = credentialRepository.getCredentialByUserEntityId(id);
        return credentialByUserEntityId.orElseThrow(() -> new ApiException("Unable to find user credentials from given userId.")) ;
    }

    private UserEntity getUserEntityByEmail(String email) {
        var userByEmail = userRepository.findByEmail(email);
        return userByEmail
                .orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityByUserId(String id) {
        var userByName = userRepository.findUserEntityByUserId(id);
        return userByName
                .orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(String code) {
        return confirmationRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Code Incorrect or User Registered"));
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Permissions.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }

}
