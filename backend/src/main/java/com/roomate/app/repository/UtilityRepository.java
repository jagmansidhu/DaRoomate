package com.roomate.app.repository;

import com.roomate.app.entities.UtilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilityRepository extends JpaRepository<UtilityEntity , Long> {
}
