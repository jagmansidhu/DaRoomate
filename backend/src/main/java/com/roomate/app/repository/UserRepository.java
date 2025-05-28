package com.roomate.app.repository;

import com.roomate.app.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>{

    List<UserEntity> findByLastName(String lastName);
    List<UserEntity> findByFirstName(String firstName);
    UserEntity findById(long id);

}
