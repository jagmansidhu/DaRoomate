package com.roomate.app.service.Implement;

import com.roomate.app.exception.ApiException;
import com.roomate.app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.roomate.app.util.EmailUtil.getEmailMessage;
import static com.roomate.app.util.EmailUtil.getPasswordMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImplement implements EmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String PASSWORD_RESET_EMAIL = "Password Reset Email";
    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendNewEmail(String name, String toEmail, String token) {
        try {
            var msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setFrom(fromEmail);
            msg.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            msg.setText(getEmailMessage(name, host, token));
            sender.send(msg);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new ApiException("Unable to send email");
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String name, String toEmail, String token) {
        try {
            var msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setFrom(fromEmail);
            msg.setSubject(PASSWORD_RESET_EMAIL);
            msg.setText(getPasswordMessage(name, host, token));
            sender.send(msg);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new ApiException("Unable to send email");
        }
    }
}
