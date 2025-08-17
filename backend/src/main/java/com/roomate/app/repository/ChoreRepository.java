package com.roomate.app.repository;

import com.roomate.app.entities.ChoreEntity;
import com.roomate.app.entities.room.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChoreRepository extends JpaRepository<ChoreEntity, Long> {
    List<ChoreEntity> findByRoomAndDueAtAfter(RoomEntity room, LocalDateTime date);

}
