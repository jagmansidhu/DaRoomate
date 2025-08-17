package com.roomate.app.repository;

import com.roomate.app.entities.room.RoomEntity;
import com.roomate.app.entities.room.RoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMemberEntity, UUID> {
    List<RoomMemberEntity> findByRoomId(UUID roomId);
    Optional<RoomMemberEntity> findByRoomIdAndUserId(UUID roomId, Long userId);
    boolean existsByRoomIdAndUserId(UUID roomId, Long userId);

    Optional<RoomMemberEntity> getRoomMemberEntityById(UUID id);

    Optional<RoomMemberEntity> getRoomMemberEntityByUserId(Long userId);

    @Query("SELECT m FROM RoomMemberEntity m WHERE m.room.id = :roomId")
    List<RoomMemberEntity> findByRoomID(@Param("roomId") UUID roomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomMemberEntity m WHERE m.room.id = :roomId")
    void deleteAllByRoomId(@Param("roomId") UUID roomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomMemberEntity m WHERE m.room.id = :roomId AND m.user.id = :userId")
    void deleteByRoomIdAndUserId(@Param("roomId") UUID roomId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomMemberEntity m WHERE m.id = :id AND m.user.id = :userId")
    void deleteByMemberIdAndUserId(@Param("id") UUID id, @Param("userId") Long userId);

}
