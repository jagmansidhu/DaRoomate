package com.roomate.app.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventDto {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RoomDto rooms;
    private UserDto user;
    private LocalDateTime created;
    private LocalDateTime updated;
}
