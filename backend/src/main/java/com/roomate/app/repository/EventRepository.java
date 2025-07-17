package com.roomate.app.repository;

import com.roomate.app.dto.EventDto;
import com.roomate.app.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    @Query("SELECT u from EventEntity u WHERE u.user.authId = :authid" )
    List<EventEntity> getAllEventsForUser(@Param("authid") String authId);

    @Query("SELECT u from EventEntity u WHERE u.user.authId = :authid AND u.room.id = :roomid" )
    List<EventEntity> getAllEventsForUserRoom(@Param("roomid") UUID roomid, @Param("authid") String authId);
}
