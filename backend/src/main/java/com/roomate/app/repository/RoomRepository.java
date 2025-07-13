package com.roomate.app.repository;

import com.roomate.app.entities.room.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {

    @Query("SELECT r FROM RoomEntity r JOIN r.members m WHERE m.user.id = :userId")
    List<RoomEntity> findByMemberUserId(@Param("userId") Long userId);

    Optional<RoomEntity> findByRoomCode(String roomCode);
    boolean existsByRoomCode(String roomCode);
}