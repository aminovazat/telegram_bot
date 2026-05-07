package com.example.notification;

import com.example.notification.service.TelegramBotService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class NotificationServiceApplicationTest {

  @Test
  void mainStartsSpringApplication() {
    String[] args = {"--server.port=0"};

    try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
      NotificationServiceApplication.main(args);

      springApplication.verify(() -> SpringApplication.run(NotificationServiceApplication.class, args));
    }
  }

  @Test
  void telegramBotsApiRegistersBot() throws Exception {
    NotificationServiceApplication application = new NotificationServiceApplication();
    TelegramBotService botService = mock(TelegramBotService.class);

    try (MockedConstruction<TelegramBotsApi> construction = mockConstruction(TelegramBotsApi.class)) {
      TelegramBotsApi api = application.telegramBotsApi(botService);

      assertThat(api).isSameAs(construction.constructed().get(0));
      verify(api).registerBot(botService);
    }
  }

  @Test
  void telegramBotsApiWrapsRegistrationFailure() {
    NotificationServiceApplication application = new NotificationServiceApplication();
    TelegramBotService botService = mock(TelegramBotService.class);

    try (MockedConstruction<TelegramBotsApi> ignored = mockConstruction(
      TelegramBotsApi.class,
      (api, context) -> doThrow(new TelegramApiException("boom")).when(api).registerBot(botService)
    )) {
      assertThatThrownBy(() -> application.telegramBotsApi(botService))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(TelegramApiException.class)
        .hasMessageContaining("boom");
    }
  }
}
