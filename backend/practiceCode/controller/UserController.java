//package com.roomate.app.controller;
//
//import com.roomate.app.domain.Response;
//import com.roomate.app.dtorequest.UserRequest;
//import com.roomate.app.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//
//import static com.roomate.app.util.RequestUtil.getResponse;
//import static java.util.Collections.emptyMap;
//import static org.springframework.http.HttpStatus.CREATED;
//import static org.springframework.http.HttpStatus.OK;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping(path = {"/user"})
//public class UserController {
//    private final UserService userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<Response> createUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
//        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
//        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account Created", CREATED));
//    }
//
//    @GetMapping("/verify/account")
//    public ResponseEntity<Response> verifyAccount(@RequestParam("code") String code, HttpServletRequest request) {
//        userService.verifyAccountCode(code);
//        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account Verified", OK));
//    }
//
//    private URI getUri() {
//        return URI.create("");
//    }
//}
