//package com.roomate.app.dtorequest;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotEmpty;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class UserRequest {
//    @NotEmpty(message = "First name is required")
//    private String firstName;
//    @NotEmpty(message = "Last name is required")
//    private String lastName;
//    @NotEmpty(message = "Email is required")
//    @Email(message = "Email is invalid")
//    private String email;
//    @NotEmpty(message = "Password is required")
//    private String password;
//    private String phone;
//    private String bio;
//}
