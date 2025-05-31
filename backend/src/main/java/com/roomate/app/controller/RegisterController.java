//package com.roomate.app.controller;
//
//import com.roomate.app.dto.UserDto;
//import com.roomate.app.repository.UserRepository;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/user")
//public class RegisterController {
////    private final RegisterServiceImplementation registerService;
//
//    private final UserRepository userRepository;
//
//    public RegisterController( UserRepository userRepository) {
////        this.registerService = registerService;
//        this.userRepository = userRepository;
//    }
//
//
////    @PostMapping("/register")
////    public ResponseEntity<?> registerApi(@RequestBody UserEntity user, HttpServletRequest request) throws UserApiError {
////        return registerService.register(user);
////    }
//
////    @CrossOrigin(origins = "http://localhost:3000")
//    @GetMapping("/all")
//    @ResponseBody
//    public List<UserDto> all() {
//        return userRepository.findAll().stream()
//                .map(user -> new UserDto(
//                        user.getId(),
//                        user.getFirstName(),
//                        user.getLastName(),
//                        user.getEmail(),
//                        user.getPhone()
//                ))
//                .collect(Collectors.toList());
//    }
//}