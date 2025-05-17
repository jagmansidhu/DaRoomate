package com.roomate.app.event.listener;

import com.roomate.app.event.UserEvent;
import com.roomate.app.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.roomate.app.enumeration.EventType.REGISTRATION;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final EmailService emailService;

    @EventListener
    public void onUserEvent(UserEvent userEvent) {
        switch (userEvent.getType()) {
            case REGISTRATION ->
                    emailService.sendNewEmail(userEvent.getUserEntity().getFirstName(), userEvent.getUserEntity().getEmail(), (String) userEvent.getData().get("key"));
            case PASSWORD_RESET ->
                    emailService.sendPasswordResetEmail(userEvent.getUserEntity().getFirstName(), userEvent.getUserEntity().getEmail(), (String) userEvent.getData().get("key"));
            default -> {
            }
        }
    }
}
