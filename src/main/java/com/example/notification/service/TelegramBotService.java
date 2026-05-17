package com.example.notification.service;

import com.example.notification.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public TelegramBotService(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null) {
            log.warn("Получен null update, обработка пропущена");
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            System.out.println("Получено: " + text + " от " + chatId);

            if (text.equals("/start")) {
                sendMessage(chatId, "Бот запущен!\nНапиши /test для проверки.\nНапиши /info для отладки.\nНапиши /github для ссылки на репозиторий.");
            }
            else if (text.equals("/test")) {
                sendMessage(chatId, "Бот работает отлично!");
            }
            else if (text.equals("/info")) {
                int randomCode = (int) (Math.random() * 900000) + 100000;
                Long fromUserId = update.getMessage().getFrom() != null ? update.getMessage().getFrom().getId() : null;
                String username = update.getMessage().getFrom() != null ? update.getMessage().getFrom().getUserName() : null;
                String usernameText = (username != null && !username.isEmpty()) ? "@" + username : "не указан";

                String infoMessage = String.format(
                        "Chat ID: %s\n",
                        chatId
                );
                sendMessage(chatId, infoMessage);
            }
            else if (text.equals("/github")) {
                sendMessage(chatId, "GitHub репозиторий:\nhttps://github.com/aminovazat/telegram_bot");
            }
            else {
                sendMessage(chatId, "Используй команды:\n/start - приветствие\n/test - проверка\n/info - отладка\n/github - ссылка на репозиторий");
            }
        }
    }

    /**
     * Публичный метод для отправки кода подтверждения.
     * Вызывается из Kafka Consumer.
     */
    public void sendAuthCode(String chatId, String code) {
        String messageText = String.format(
                "Код подтверждения: %s\n\nНикому не сообщайте этот код. Он действителен 5 минут.",
                code
        );
        sendMessage(chatId, messageText);
    }

    /**
     * Публичный метод для отправки уведомлений о статусах заказа.
     * Вызывается из Kafka Consumer.
     */
    public void sendOrderStatusNotification(String chatId, String text) {
        sendMessage(chatId, text);
    }

    /**
     * Метод для отправки любого сообщения.
     * Если Telegram API не смог отправить сообщение, пробрасываем ошибку выше,
     * чтобы consumer не писал ложный лог "отправлено".
     */
    protected void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);

        try {
            execute(message);
            log.info("Сообщение отправлено в чат {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки в чат {}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить сообщение в Telegram", e);
        }
    }
}