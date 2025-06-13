//package com.roomate.app.WebSocket;
//
//import com.roomate.app.dto.MessageDto;
//import com.roomate.app.entities.chatEntities.MessageEnum;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class WebSocketEventListener {
//
//    private final SimpMessageSendingOperations messagingTemplate;
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
//        if (userId != null) {
//            log.info("User disconnected: " + userId);
//
//            MessageDto leaveMessage = new MessageDto(MessageEnum.LEAVE, userId);
//
//            messagingTemplate.convertAndSend("/topic/public", leaveMessage);
//        }
//    }
//
//}