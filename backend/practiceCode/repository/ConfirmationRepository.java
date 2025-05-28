//package com.roomate.app.repository;
//
//import com.roomate.app.entity.ConfirmationEntity;
//import com.roomate.app.entity.UserEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {
//    Optional<ConfirmationEntity> findByCode(String code);
//    Optional<ConfirmationEntity> findByUserEntity(UserEntity userEntity);
//}
