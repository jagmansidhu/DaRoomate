package com.roomate.app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EventUserDto {
    private String authId;
    private String firstName;
    private String lastName;
    private String email;

    public EventUserDto() {
    }

    public EventUserDto(String authId, String firstName, String lastName, String email) {
        this.authId = authId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
} 