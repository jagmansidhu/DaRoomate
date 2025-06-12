package com.roomate.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messenger_user_Id", referencedColumnName = "id")
    @NotNull
    @JsonIgnore
    private UserEntity messenger;
    private MessageEnum messageType;
    @NotNull
    private String message;
    @NotNull
    private LocalDateTime dateTime;
    private String conversationId;

    public MessageEntity(UserEntity messenger, MessageEnum messageType, String message, LocalDateTime now) {
        this.messenger = messenger;
        this.messageType = messageType;
        this.message = message;
        this.dateTime = now;
    }
}
