package com.roomate.app.repository;

import com.roomate.app.entity.CredentialEntity;
import com.roomate.app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {
    Optional<CredentialEntity> getCredentialByUserEntity(UserEntity userEntity);
}
