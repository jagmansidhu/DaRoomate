package com.roomate.app.repository;

import com.roomate.app.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByAuthId(String authId);

    boolean existsByEmail(String email);

    boolean existsByAuthId(String authId);

    UserEntity getUserByEmail(String email);

    UserEntity findByEmail(String email);
}
