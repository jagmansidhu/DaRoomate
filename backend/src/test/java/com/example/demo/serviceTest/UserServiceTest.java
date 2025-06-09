package com.example.demo.serviceTest;

import com.roomate.app.StartOneApplication;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.implementation.UserServiceImplementation;
import org.apache.catalina.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = StartOneApplication.class)
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@ActiveProfiles("Test")
public class UserServiceTest {

    @Autowired
    private UserServiceImplementation userService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity userEntity;

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
    }

    @Test
    void userExistsTrueTest() {
        userEntity = new UserEntity("!23", "TESTTT@AMOUNGUS.ca", "", "");
        userRepository.save(userEntity);

        assertTrue(userService.userExists("!23", "TESTTT@AMOUNGUS.ca"));
    }

    @Test
    void userExistsFalseTest() {
        userEntity = new UserEntity("!23", "TESTTT@AMOUNGUS.ca", "", "");
        userRepository.save(userEntity);

        assertFalse(userService.userExists("1", "TESTTT@AMOUNGUS.ca"));
    }

    @Test
    void createUserTest() {
        UserEntity createUser = userService.createUserByAuthID("!23", "TESTTT@AMOUNGUS.ca", "", "");

        assertThat(createUser.getAuthId()).isEqualTo("!23");
    }

    @Test
    void findUserByAuthIdTest() {
        userEntity = new UserEntity("!23", "TESTTT@AMOUNGUS.ca", "", "");
        UserEntity insertedUser = userRepository.save(userEntity);

        UserEntity findUser = userService.getUserByAuthId("!23");

        assertThat(insertedUser).isEqualTo(findUser);
    }

    @Test
    void updateUserTest() {
        userEntity = new UserEntity("!23", "", "", "TESTTT@AMOUNGUS.ca","");
        userRepository.save(userEntity);

        UserEntity updateEntity = new UserEntity("!23","amman", "jo","TESTTT@AMOUNGUS.ca", "1234465");

        userEntity = userService.updateUserProfile("!23", updateEntity);

        assertThat(updateEntity.getAuthId()).isEqualTo(userEntity.getAuthId());
        assertThat(updateEntity.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(updateEntity.getLastName().toUpperCase()).isEqualTo(userEntity.getLastName().toUpperCase());
        assertThat(updateEntity.getPhone()).isEqualTo(userEntity.getPhone());
        assertThat(updateEntity.getEmail()).isEqualTo(userEntity.getEmail());
    }

    @Test
    void isUserCompleteCompletedTest() {
        userEntity = new UserEntity("!23", "Lin", "Sta", "TESTTT@AMOUNGUS.ca","123456789");
        userRepository.save(userEntity);

        assertTrue(userService.isProfileCompleteInDatabase("!23"));
    }

    @Test
    void isUserCompleteIncompletedTest() {
        userEntity = new UserEntity("!23", "", "", "TESTTT@AMOUNGUS.ca","123456789");
        userRepository.save(userEntity);

        assertFalse(userService.isProfileCompleteInDatabase("!23"));
    }



}
