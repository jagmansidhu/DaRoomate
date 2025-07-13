package com.roomate.app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class RoomDto {
    private Long id;
    private String name;
    private String address;
    private String description;
    private String roomCode;
    private String headRoommateId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoomMemberDto> members;
}


