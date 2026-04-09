package com.example.notification.consumer;

import com.example.notification.dto.AuthCodeRequest;
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

  @Override
  public void accept(AuthCodeRequest request) {
    log.info("Получен запрос на отправку кода для пользователя {} в чат {}", request.getUserId(), request.getChatId());
    try {
      telegramBotService.sendAuthCode(request.getChatId(), request.getCode());
      log.info("Код {} успешно отправлен пользователю {}", request.getCode(), request.getUserId());
    } catch (Exception e) {
      log.error("ОШИБКА при отправке кода пользователю {}: {}", request.getUserId(), e.getMessage());
    }
  }
}