package com.example.notification.consumer;

import com.example.notification.dto.AuthCodeRequest;
import com.example.notification.service.TelegramBotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AuthCodeConsumerTest {

  @Mock
  private TelegramBotService telegramBotService;

  @InjectMocks
  private AuthCodeConsumer authCodeConsumer;

  @Test
  void shouldCallTelegramServiceWhenRequestIsValid() {
    AuthCodeRequest request = new AuthCodeRequest("chat-123", "654321", "user-42");

    authCodeConsumer.accept(request);

    verify(telegramBotService, times(1)).sendAuthCode("chat-123", "654321");
    verifyNoMoreInteractions(telegramBotService);
  }

  @Test
  void shouldSwallowExceptionFromTelegramService() {
    AuthCodeRequest request = new AuthCodeRequest("chat-123", "654321", "user-42");
    doThrow(new RuntimeException("Telegram unavailable"))
      .when(telegramBotService).sendAuthCode("chat-123", "654321");

    authCodeConsumer.accept(request);

    verify(telegramBotService, times(1)).sendAuthCode("chat-123", "654321");
    verifyNoMoreInteractions(telegramBotService);
  }
}
