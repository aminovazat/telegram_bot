package com.example.notification.consumer;

import com.example.notification.dto.AuthCodeRequest;
import com.example.notification.service.TelegramBotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthCodeConsumerTest {

    @Mock
    private TelegramBotService telegramBotService;

    @InjectMocks
    private AuthCodeConsumer authCodeConsumer;

    private AuthCodeRequest request;

    @BeforeEach
    void setUp() {
        request = new AuthCodeRequest();
        request.setUserId("1");
        request.setChatId("123456789");
        request.setCode("555777");
    }

    @Test
    void testAccept_Success() {
        // Вызываем метод
        authCodeConsumer.accept(request);

        // Проверяем, что метод сервиса был вызван ровно 1 раз с нужными параметрами
        verify(telegramBotService, times(1))
                .sendAuthCode(request.getChatId(), request.getCode());
    }

    @Test
    void testAccept_ExceptionHandling() {
        // Настраиваем мок так, чтобы он выкидывал ошибку
        doThrow(new RuntimeException("Telegram API Error"))
                .when(telegramBotService).sendAuthCode(anyString(), anyString());

        // Вызываем метод (он не должен пробрасывать Exception дальше благодаря try-catch)
        authCodeConsumer.accept(request);

        // Проверяем, что сервис всё равно вызывался
        verify(telegramBotService, times(1))
                .sendAuthCode(request.getChatId(), request.getCode());
    }
}