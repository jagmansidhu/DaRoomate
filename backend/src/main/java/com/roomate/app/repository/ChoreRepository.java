package com.roomate.app.repository;

import com.roomate.app.entities.ChoreEntity;
import com.roomate.app.entities.room.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChoreRepository extends JpaRepository<ChoreEntity, Long> {
    List<ChoreEntity> findByRoomAndDueAtAfter(RoomEntity room, LocalDateTime date);
    List<ChoreEntity> findByRoom(RoomEntity room);
    void deleteById(UUID choreId);
    @Query("SELECT c FROM ChoreEntity c " +
            "LEFT JOIN FETCH c.assignedToMember m " +
            "LEFT JOIN FETCH m.user " +
            "WHERE c.room = :room")
    List<ChoreEntity> findByRoomWithMemberAndUser(@Param("room") RoomEntity room);
    void deleteAllByRoomIdAndChoreName(UUID roomId, String choreName);

    @Query("SELECT u FROM ChoreEntity u WHERE u.assignedToMember.id IN :roomMemberIds")
    List<ChoreEntity> findAllByRoomMemberIds(List<UUID> roomMemberIds);
}
