package com.example.notification;

import com.example.notification.service.TelegramBotService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class NotificationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(NotificationServiceApplication.class, args);
  }

  // 👇 ЭТОТ НОВЫЙ МЕТОД РЕГИСТРИРУЕТ БОТА
  @Bean
  public TelegramBotsApi telegramBotsApi(TelegramBotService botService) {
    try {
      TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
      api.registerBot(botService);
      return api;
    } catch (TelegramApiException e) {
      throw new RuntimeException("Не удалось зарегистрировать бота: " + e.getMessage(), e);
    }
  }
}