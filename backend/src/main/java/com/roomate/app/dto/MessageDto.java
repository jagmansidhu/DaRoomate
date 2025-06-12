package com.roomate.app.dto;

import com.roomate.app.entities.MessageEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String messengerId;
    private String messengerName;
    private MessageEnum messageType;
    private String message;
    private LocalDateTime timestamp;
    private String conversationId;

    public MessageDto(MessageEnum messageEnum, String userId) {
        this.messageType = messageEnum;
        this.messengerId = userId;
    }
}
