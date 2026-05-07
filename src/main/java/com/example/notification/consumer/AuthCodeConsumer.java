package com.example.notification.consumer;

import com.example.notification.dto.AuthCodeRequest;
import com.example.notification.service.EmailService;
import com.example.notification.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCodeConsumer implements Consumer<AuthCodeRequest> {

    private final TelegramBotService telegramBotService;
    private final EmailService emailService; // Добавили сервис почты

    @Override
    public void accept(AuthCodeRequest request) {
        log.info("Начало обработки уведомлений для пользователя: {}", request.getUserId());

        // 1. Отправка в Telegram
        try {
            telegramBotService.sendAuthCode(request.getChatId(), request.getCode());
            log.info("Код подтверждения отправлен в Telegram (ChatID: {})", request.getChatId());
        } catch (Exception e) {
            log.error("Ошибка при отправке в Telegram для пользователя {}: {}",
                    request.getUserId(), e.getMessage());
        }

        // 2. Отправка на Email (Твоя основная задача по SMTP)
        try {
            String subject = "Код подтверждения | Food Delivery";
            String messageBody = String.format(
                    "Здравствуйте!\n\nВаш код подтверждения: %s\nКод действителен в течение 5 минут.",
                    request.getCode()
            );

            emailService.sendEmail(request.getEmail(), subject, messageBody);
            log.info("Письмо успешно отправлено на почту: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Ошибка при отправке Email для пользователя {}: {}",
                    request.getUserId(), e.getMessage());
        }

        log.info("Обработка запроса для пользователя {} завершена", request.getUserId());
    }
}