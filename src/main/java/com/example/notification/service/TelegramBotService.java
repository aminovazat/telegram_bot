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
    if (update.hasMessage() && update.getMessage().hasText()) {
      String chatId = update.getMessage().getChatId().toString();
      String text = update.getMessage().getText();

      System.out.println("Получено: " + text + " от " + chatId);

      if (text.equals("/start")) {
        sendMessage(chatId, "✅ Бот запущен!\nНапиши /test для проверки.\nНапиши /info для отладки.\nНапиши /github для ссылки на репозиторий.");
      }
      else if (text.equals("/test")) {
        sendMessage(chatId, "✅ Бот работает отлично!");
      }
      else if (text.equals("/info")) {
        int randomCode = (int) (Math.random() * 900000) + 100000;
        String username = update.getMessage().getFrom().getUserName();
        String usernameText = (username != null && !username.isEmpty()) ? "@" + username : "не указан";

        String infoMessage = String.format(
          "📋 *Отладочная информация*\n\n" +
            "🆔 *Chat ID:* `%s`\n" +
            "👤 *User ID:* `%s`\n" +
            "📛 *Username:* %s\n" +
            "🔢 *Рандомный код:* `%d`\n" +
            "⏰ *Время:* `%s`\n\n" +
            "💡 Этот chatId нужно сохранить в БД для отправки кодов подтверждения.",
          chatId,
          update.getMessage().getFrom().getId().toString(),
          usernameText,
          randomCode,
          java.time.LocalDateTime.now().toString().substring(0, 19)
        );
        sendMessage(chatId, infoMessage);
      }
      else if (text.equals("/github")) {
        sendMessage(chatId, "📦 *GitHub репозиторий:*\n[github.com/aminovazat/telegram_bot](https://github.com/aminovazat/telegram_bot)");
      }
      else {
        sendMessage(chatId, "Используй команды:\n/start - приветствие\n/test - проверка\n/info - отладка\n/github - ссылка на репозиторий");
      }
    }
  }

  /**
   * Публичный метод для отправки кода подтверждения
   * Вызывается из Kafka Consumer
   */
  public void sendAuthCode(String chatId, String code) {
    String messageText = String.format(
      "🔐 *Код подтверждения:* `%s`\n\nНикому не сообщайте этот код. Он действителен 5 минут.",
      code
    );
    sendMessage(chatId, messageText);
  }

  /**
   * Приватный метод для отправки любого сообщения
   */
  private void sendMessage(String chatId, String text) {
    SendMessage message = new SendMessage(chatId, text);
    message.setParseMode("Markdown");
    try {
      execute(message);
      log.info("Сообщение отправлено в чат {}", chatId);
    } catch (TelegramApiException e) {
      log.error("Ошибка отправки в чат {}: {}", chatId, e.getMessage());
    }
  }
}