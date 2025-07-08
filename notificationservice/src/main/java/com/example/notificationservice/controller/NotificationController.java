package com.example.notificationservice.controller;

import com.example.notificationservice.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendManualEmail(@RequestParam String email, @RequestParam String operation) {
        String text;
        if ("CREATE".equalsIgnoreCase(operation)) {
            text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            return "Неверная операция";
        }

        emailService.sendSimpleMessage(email, "Уведомление", text);
        return "Email отправлен";
    }
}
