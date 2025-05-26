package com.example.demo.serviceTest;

import com.roomate.app.dto.User;
import com.roomate.app.entity.CredentialEntity;
import com.roomate.app.entity.RoleEntity;
import com.roomate.app.entity.UserEntity;
import com.roomate.app.enumeration.Permissions;
import com.roomate.app.repository.CredentialRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.Implement.UserServiceImplement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CredentialRepository credentialRepository;
    @InjectMocks
    private UserServiceImplement userService;

    @Test
    @DisplayName("Test Find User by ID")
    public void getUserByUserIdTest() {
        // Arrange - Given
        var userEntity = new UserEntity();
        userEntity.setId(0L);
        userEntity.setUserId("1");
        userEntity.setEmail("<EMAIL>");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setCreatedAt(java.time.LocalDateTime.now());
        userEntity.setUpdatedAt(java.time.LocalDateTime.now());
        userEntity.setLastLogin(java.time.LocalDateTime.now());
        userEntity.setAccountNonExpired(true);
        var roleEntity = new RoleEntity("USER", Permissions.USER);
        userEntity.setRoles(roleEntity);

        var credentialEntity = new CredentialEntity();
        credentialEntity.setUpdatedAt(java.time.LocalDateTime.now());
        credentialEntity.setUserEntity(userEntity);
        credentialEntity.setPassword("<PASSWORD>");

        when(userRepository.findUserEntityByUserId("1")).thenReturn(java.util.Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(0L)).thenReturn(java.util.Optional.of(credentialEntity));

        // Act - When
        var userByUserId = userService.getUserByUserId("1");


        // Assert - Expected Result

        assertThat(userByUserId.getUserId()).isEqualTo(userEntity.getUserId());
        assertThat(userByUserId.getFirstName()).isEqualTo(userEntity.getFirstName());

    }
}
