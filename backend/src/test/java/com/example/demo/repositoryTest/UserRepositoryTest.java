package com.example.demo.repositoryTest;

import com.roomate.app.StartOneApplication;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = StartOneApplication.class)
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private UserEntity userEntity;

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
    }

    @Test
    void newUserTestAdded() {
        userEntity = new UserEntity("!23", "TESTING", "LASTTESTING", "TESTTT@AMOUNGUS.ca", "12345");
        UserEntity insertedUser = userRepository.save(userEntity);

        Optional<UserEntity> findUser = userRepository.findById(insertedUser.getId());

        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get()).isEqualTo(userEntity);
    }

    @Test
    void getByAuthIdTest() {
        userEntity = new UserEntity("!23", "TESTING", "LASTTESTING", "TESTTT@AMOUNGUS.ca", "12345");
        UserEntity insertedUser = userRepository.save(userEntity);

        Optional<UserEntity> findUser = userRepository.findById(insertedUser.getId());

        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get().getAuthId()).isEqualTo(insertedUser.getAuthId());
    }
}
