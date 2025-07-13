package com.roomate.app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class RoomMemberDto {
    private Long id;
    private String userId;
    private String name;
    private String email;
    private com.roomate.app.entities.room.RoomMemberEnum role;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;
}