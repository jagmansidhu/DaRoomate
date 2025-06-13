package com.roomate.app.service.implementation;

import com.roomate.app.dto.UserDto;
import com.roomate.app.entities.chatEntities.MessageEntity;
import com.roomate.app.repository.MessageRepository;
import com.roomate.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImplt {

    private final MessageRepository messageRepository;
    private final ChatRoomServiceImplt chatRoomService;
    private final UserRepository userRepository;

    @Transactional
    public MessageEntity save(MessageEntity messageEntity) {
        var chatId = chatRoomService
                .getChatRoomId(messageEntity.getSenderId(), messageEntity.getRecipientId(), true)
                .orElseThrow(() -> new IllegalStateException("Failed to create or retrieve chat ID"));

        messageEntity.setChatId(chatId);

        messageEntity.setTimestamp(new Date());
        messageEntity.setRead(false);
        if (messageEntity.getMessageType() == null || messageEntity.getMessageType().isEmpty()) {
            messageEntity.setMessageType("TEXT");
        }
        MessageEntity savedMessage = messageRepository.save(messageEntity);
        return savedMessage;
    }

    public List<MessageEntity> findMessageEntitys(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(messageRepository::findByChatIdOrderByTimestampAsc).orElse(new ArrayList<>());
    }

    public Long countUnreadMessages(String userId) {
        return messageRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markMessageAsRead(String messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            messageRepository.save(message);
        });
    }

//    @Transactional
//    public void markMessagesAsReadBetweenUsers(String senderId, String recipientId) {
//        Optional<String> chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
//        if (chatId.isPresent()) {
//            messageRepository.markMessagesAsReadByChatIdAndSenderIdAndRecipientId(chatId.get(), senderId, recipientId);
//        }
//    }

    public List<String> findUserChats(String userId) {
        return messageRepository.findDistinctChatIdsBySenderIdOrRecipientId(userId);
    }

    @Transactional
    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }
//
//    public Map<String, Long> countUnreadMessagesPerSender(String userId) {
//        List<Object[]> unreadCountsRaw = messageRepository.countUnreadMessagesGroupedBySender(userId);
//        return unreadCountsRaw.stream()
//                .collect(Collectors.toMap(
//                        arr -> (String) arr[0],
//                        arr -> (Long) arr[1]
//                ));
//    }

    public List<UserDto> findAllChatUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId() != null ? user.getId().toString() : null, user.getEmail()))
                .collect(Collectors.toList());
    }
}
