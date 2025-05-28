package com.roomate.app.repository;

import com.roomate.app.entities.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RolesEntity, Long> {
    RolesEntity findByName(String name);
}
