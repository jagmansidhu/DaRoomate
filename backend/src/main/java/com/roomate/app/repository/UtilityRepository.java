package com.roomate.app.repository;

import com.roomate.app.entities.UtilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UtilityRepository extends JpaRepository<UtilityEntity , Long> {
    List<UtilityEntity> findByRoomId(UUID roomId);
}
