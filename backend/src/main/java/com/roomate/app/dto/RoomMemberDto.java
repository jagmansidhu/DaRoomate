package com.roomate.app.dto;

import com.roomate.app.entities.room.RoomMemberEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
public class RoomMemberDto {
    private UUID id;
    private String userId;
    private String name;
    private String email;
    private RoomMemberEnum role;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;
}