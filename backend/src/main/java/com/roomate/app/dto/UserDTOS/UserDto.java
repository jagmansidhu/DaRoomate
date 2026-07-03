package com.roomate.app.dto.UserDTOS;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class UserDto {
    private String authId;
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public UserDto(UUID id, String firstName, String lastName, @NotNull String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserDto(String email) {
        this.email = email;
    }

    public UserDto() {

    }

    public UserDto(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
