package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.UserEvent;
import com.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaUserEventListener {

    private final EmailService emailService;

    public KafkaUserEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void listen(UserEvent event) {
        String message;
        if ("CREATE".equalsIgnoreCase(event.getOperation())) {
            message = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        } else if ("DELETE".equalsIgnoreCase(event.getOperation())) {
            message = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            return;
        }
        emailService.sendSimpleMessage(event.getEmail(), "Уведомление", message);
    }
}
