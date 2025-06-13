package com.roomate.app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public UserDto() {
    }

    public UserDto(String id,String email) {
        this.id = id;
        this.email = email;
    }

}
