package com.roomate.app.unit.service;

import com.roomate.app.dto.RegisterDto;
import com.roomate.app.dto.UserDTOS.UpdateProfileDto;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.entities.VerificationTokenEntity;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.repository.VerificationTokenRepository;
import com.roomate.app.service.JWTService;
import com.roomate.app.service.implementation.UserServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImplementation}.
 *
 * Uses Mockito to isolate the service from database, JWT, mail, and
 * password-encoder infrastructure – no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceIntegrationTest {

    // ── Mocks ────────────────────────────────────────────────────────────────

    @Mock private UserRepository               userRepository;
    @Mock private PasswordEncoder              passwordEncoder;
    @Mock private JWTService                   jwtService;
    @Mock private VerificationTokenRepository  verificationTokenRepository;
    @Mock private JavaMailSender               mailSender;

    @InjectMocks
    private UserServiceImplementation userService;

    // ── Shared fixtures ──────────────────────────────────────────────────────

    private static final String EMAIL    = "test@example.com";
    private static final String PASSWORD = "RawPass123!";
    private static final String ENCODED  = "$2a$12$hashedPassword";
    private static final String JWT      = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.payload.sig";

    private UserEntity stubUser;

    @BeforeEach
    void setUp() {
        stubUser = new UserEntity();
        stubUser.setId(UUID.randomUUID());
        stubUser.setEmail(EMAIL);
        stubUser.setFirstName("John");
        stubUser.setLastName("Doe");
        stubUser.setPhone("555-1234");
        stubUser.setPassword(ENCODED);
        stubUser.setEnabled(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // registerUser
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("registerUser()")
    class RegisterUser {

        private RegisterDto buildDto(String email) {
            RegisterDto dto = new RegisterDto();
            dto.setEmail(email);
            dto.setFirstName("John");
            dto.setLastName("Doe");
            dto.setPassword(PASSWORD);
            return dto;
        }

        @Test
        @DisplayName("returns JWT when email is new")
        void happyPath_returnsJwt() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED);
            when(userRepository.save(any(UserEntity.class))).thenReturn(stubUser);
            when(jwtService.generateToken(any(UserEntity.class))).thenReturn(JWT);

            String result = userService.registerUser(buildDto(EMAIL));

            assertThat(result).isEqualTo(JWT);
        }

        @Test
        @DisplayName("saves user with encoded password")
        void savesUserWithEncodedPassword() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED);
            when(userRepository.save(any(UserEntity.class))).thenReturn(stubUser);
            when(jwtService.generateToken(any(UserEntity.class))).thenReturn(JWT);

            userService.registerUser(buildDto(EMAIL));

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());
            UserEntity saved = captor.getValue();

            assertThat(saved.getPassword()).isEqualTo(ENCODED);
            assertThat(saved.getEmail()).isEqualTo(EMAIL);
            assertThat(saved.getFirstName()).isEqualTo("John");
            assertThat(saved.getLastName()).isEqualTo("Doe");
        }

        @Test
        @DisplayName("throws DuplicateKeyException when email already exists")
        void duplicateEmail_throws() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThatThrownBy(() -> userService.registerUser(buildDto(EMAIL)))
                    .isInstanceOf(DuplicateKeyException.class);

            verify(userRepository, never()).save(any());
            verify(jwtService, never()).generateToken(any());
        }

        @Test
        @DisplayName("email lookup is case-insensitive (lowercased before check)")
        void emailLookupIsCaseInsensitive() {
            String mixedCase = "TEST@Example.COM";
            when(userRepository.existsByEmail(mixedCase.toLowerCase())).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED);
            when(userRepository.save(any())).thenReturn(stubUser);
            when(jwtService.generateToken(any())).thenReturn(JWT);

            userService.registerUser(buildDto(mixedCase));

            verify(userRepository).existsByEmail(mixedCase.toLowerCase());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // userExists
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("userExists()")
    class UserExists {

        @Test
        @DisplayName("returns true when repository reports existing email")
        void existingEmail() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);
            assertThat(userService.userExists(EMAIL)).isTrue();
        }

        @Test
        @DisplayName("returns false for unknown email")
        void unknownEmail() {
            when(userRepository.existsByEmail("nobody@example.com")).thenReturn(false);
            assertThat(userService.userExists("nobody@example.com")).isFalse();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // getUserEntityByEmail
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getUserEntityByEmail()")
    class GetUserEntityByEmail {

        @Test
        @DisplayName("returns entity when found")
        void found() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));

            UserEntity result = userService.getUserEntityByEmail(EMAIL);

            assertThat(result).isSameAs(stubUser);
        }

        @Test
        @DisplayName("throws UsernameNotFoundException when not found")
        void notFound() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserEntityByEmail(EMAIL))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining(EMAIL);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // isProfileCompleteInDatabase
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isProfileCompleteInDatabase()")
    class IsProfileComplete {

        @Test
        @DisplayName("returns true when firstName, lastName, and phone are all set")
        void allFieldsPresent() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            assertThat(userService.isProfileCompleteInDatabase(EMAIL)).isTrue();
        }

        @Test
        @DisplayName("returns false when phone is missing")
        void missingPhone() {
            stubUser.setPhone(null);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            assertThat(userService.isProfileCompleteInDatabase(EMAIL)).isFalse();
        }

        @Test
        @DisplayName("returns false when firstName is blank")
        void blankFirstName() {
            stubUser.setFirstName("");
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            assertThat(userService.isProfileCompleteInDatabase(EMAIL)).isFalse();
        }

        @Test
        @DisplayName("returns false when lastName is null")
        void nullLastName() {
            stubUser.setLastName(null);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            assertThat(userService.isProfileCompleteInDatabase(EMAIL)).isFalse();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // updateUserProfile
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("updateUserProfile()")
    class UpdateUserProfile {

        private UpdateProfileDto buildUpdate(String firstName, String lastName,
                                             String phone,
                                             String curPassword, String newPassword) {
            UpdateProfileDto dto = new UpdateProfileDto();
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setPhone(phone);
            dto.setCurPassword(curPassword);
            dto.setPassword(newPassword);
            return dto;
        }

        @Test
        @DisplayName("updates firstName, lastName, and phone")
        void updatesBasicFields() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            when(userRepository.save(any())).thenReturn(stubUser);

            UpdateProfileDto dto = buildUpdate("Jane", "Smith", "999-0000", null, null);
            userService.updateUserProfile(EMAIL, dto);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());
            UserEntity saved = captor.getValue();

            assertThat(saved.getFirstName()).isEqualTo("jane");
            assertThat(saved.getLastName()).isEqualTo("smith");
            assertThat(saved.getPhone()).isEqualTo("999-0000");
        }

        @Test
        @DisplayName("updates password when current password matches")
        void updatesPasswordWhenMatches() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            when(passwordEncoder.matches(PASSWORD, ENCODED)).thenReturn(true);
            when(passwordEncoder.encode("NewPass456!")).thenReturn("$2a$newHash");
            when(userRepository.save(any())).thenReturn(stubUser);

            UpdateProfileDto dto = buildUpdate(null, null, null, PASSWORD, "NewPass456!");
            userService.updateUserProfile(EMAIL, dto);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("$2a$newHash");
        }

        @Test
        @DisplayName("throws 401 when current password does not match")
        void throwsWhenCurrentPasswordWrong() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            when(passwordEncoder.matches("wrongPassword", ENCODED)).thenReturn(false);

            UpdateProfileDto dto = buildUpdate(null, null, null, "wrongPassword", "NewPass456!");

            assertThatThrownBy(() -> userService.updateUserProfile(EMAIL, dto))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Current password is incorrect");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("skips password update when curPassword is null")
        void skipsPasswordWhenCurPasswordNull() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(stubUser));
            when(userRepository.save(any())).thenReturn(stubUser);

            UpdateProfileDto dto = buildUpdate("Jane", null, null, null, "NewPass456!");
            userService.updateUserProfile(EMAIL, dto);

            verify(passwordEncoder, never()).matches(any(), any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // verifyToken
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("verifyToken()")
    class VerifyToken {

        @Test
        @DisplayName("returns true and enables user when token is valid and not expired")
        void validToken_returnsTrue() {
            VerificationTokenEntity vt = mock(VerificationTokenEntity.class);
            when(vt.isExpired()).thenReturn(false);
            when(vt.getUser()).thenReturn(stubUser);
            when(verificationTokenRepository.findByToken("good-token"))
                    .thenReturn(Optional.of(vt));
            when(userRepository.save(stubUser)).thenReturn(stubUser);

            boolean result = userService.verifyToken("good-token");

            assertThat(result).isTrue();
            assertThat(stubUser.isEnabled()).isTrue();
            verify(userRepository).save(stubUser);
        }

        @Test
        @DisplayName("returns false when token is expired")
        void expiredToken_returnsFalse() {
            VerificationTokenEntity vt = mock(VerificationTokenEntity.class);
            when(vt.isExpired()).thenReturn(true);
            when(verificationTokenRepository.findByToken("expired-token"))
                    .thenReturn(Optional.of(vt));

            boolean result = userService.verifyToken("expired-token");

            assertThat(result).isFalse();
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("returns false when token does not exist")
        void missingToken_returnsFalse() {
            when(verificationTokenRepository.findByToken("unknown-token"))
                    .thenReturn(Optional.empty());

            assertThat(userService.verifyToken("unknown-token")).isFalse();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // sendVerificationEmail
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("sendVerificationEmail()")
    class SendVerificationEmail {

        @Test
        @DisplayName("sends mail with correct recipient and subject")
        void sendsMailWithCorrectFields() {
            userService.sendVerificationEmail(EMAIL, "abc-token-123");

            ArgumentCaptor<SimpleMailMessage> captor =
                    ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());

            SimpleMailMessage msg = captor.getValue();
            assertThat(msg.getTo()).containsExactly(EMAIL);
            assertThat(msg.getSubject()).isEqualTo("Email Verification");
            assertThat(msg.getText()).contains("abc-token-123");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // createToken
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("createToken()")
    class CreateToken {

        @Test
        @DisplayName("deletes old token, saves new one, returns UUID string")
        void createsAndPersistsToken() {
            UUID userId = stubUser.getId();
            when(verificationTokenRepository.save(any(VerificationTokenEntity.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            String token = userService.createToken(stubUser);

            verify(verificationTokenRepository).deleteByUser_Id(userId);
            verify(verificationTokenRepository).save(any(VerificationTokenEntity.class));

            // UUID.randomUUID().toString() format: 8-4-4-4-12
            assertThat(token).matches(
                    "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        }
    }
}
