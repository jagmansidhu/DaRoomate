package com.roomate.app.repository;

import com.roomate.app.entities.room.RoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMemberEntity, UUID> {
    List<RoomMemberEntity> findByRoomId(UUID roomId);
    Optional<RoomMemberEntity> findByRoomIdAndUserId(UUID roomId, Long userId);
    boolean existsByRoomIdAndUserId(UUID roomId, Long userId);
}
