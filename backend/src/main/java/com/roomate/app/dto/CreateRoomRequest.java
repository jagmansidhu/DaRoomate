package com.roomate.app.dto;

import com.roomate.app.entities.room.RoomMemberEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateRoomRequest {
    private String name;
    private String address;
    private String description;
}

