package com.roomate.app.controller;

import com.roomate.app.dto.MessageDto;
import com.roomate.app.entities.MessageEntity;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.MessageRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.implementation.UserServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final MessageRepository messageRepository;
    private final UserServiceImplementation userService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDto messageDto) {
        if (messageDto.getMessengerId() == null) {
            System.err.println("User not found for email: " + messageDto.getMessengerId() + ". CHAT message not saved.");
            return;
        }

        UserEntity userEntity = userRepository.getUserByEmail(messageDto.getMessengerId());

        MessageEntity messageEntity = new MessageEntity(userEntity, messageDto.getMessageType(), messageDto.getMessage(), LocalDateTime.now());

        System.out.println(messageDto.getMessage()+"Meesage");

        System.out.println(userEntity.getEmail());

        messageRepository.save(messageEntity);

        System.out.println(messageDto.getMessage()+"Meesage");

        messagingTemplate.convertAndSend("/topic/public", messageDto);
    }

//    @MessageMapping("/chat.addUser")
//    public void addUser(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
//        headerAccessor.getSessionAttributes().put("userId", messageDto.getId());
//        messageDto.setMessageType(MessageEnum.JOIN);
//        messagingTemplate.convertAndSend("/topic/public", messageDto);
//    }
}
