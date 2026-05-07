package com.example.notification.service;

import com.example.notification.config.BotConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotServiceTest {

    @Mock
    private BotConfig botConfig;

    private TelegramBotService botService;

    @BeforeEach
    void setUp() {
        // Используем lenient(), чтобы избежать UnnecessaryStubbingException
        lenient().when(botConfig.getToken()).thenReturn("test_token");
        lenient().when(botConfig.getUsername()).thenReturn("test_bot");

        // Инициализируем spy вручную, так как у сервиса нет конструктора без аргументов
        botService = spy(new TelegramBotService(botConfig));
    }

    @Test
    void testGetters() {
        assertThat(botService.getBotToken()).isEqualTo("test_token");
        assertThat(botService.getBotUsername()).isEqualTo("test_bot");
    }

    @Test
    void testSendAuthCode() throws TelegramApiException {
        doReturn(null).when(botService).execute(any(SendMessage.class));

        botService.sendAuthCode("1802785551", "999888");

        verify(botService, times(1)).execute(any(SendMessage.class));
    }

    @Test
    void testOnUpdateReceived_StartCommand() throws TelegramApiException {
        Update update = createMockUpdate("/start");
        doReturn(null).when(botService).execute(any(SendMessage.class));

        botService.onUpdateReceived(update);

        verify(botService, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    void testOnUpdateReceived_TestCommand() throws TelegramApiException {
        Update update = createMockUpdate("/test");
        doReturn(null).when(botService).execute(any(SendMessage.class));

        botService.onUpdateReceived(update);

        verify(botService, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    void testOnUpdateReceived_GithubCommand() throws TelegramApiException {
        Update update = createMockUpdate("/github");
        doReturn(null).when(botService).execute(any(SendMessage.class));

        botService.onUpdateReceived(update);

        verify(botService, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    void testOnUpdateReceived_InfoCommand() throws TelegramApiException {
        Update update = createMockUpdate("/info");

        // Мокаем данные пользователя для команды /info
        User user = mock(User.class);
        when(update.getMessage().getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1802785551L);
        when(user.getUserName()).thenReturn("nachyn");

        doReturn(null).when(botService).execute(any(SendMessage.class));

        botService.onUpdateReceived(update);

        verify(botService, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    void testOnUpdateReceived_UnknownCommand() throws TelegramApiException {
        Update update = createMockUpdate("Привет");
        doReturn(null).when(botService).execute(any(SendMessage.class));

        botService.onUpdateReceived(update);

        // Проверяем, что бот ответил на неизвестную команду
        verify(botService, atLeastOnce()).execute(any(SendMessage.class));
    }

    @Test
    void testSendMessage_Exception() throws TelegramApiException {
        // Заставляем execute выбросить ошибку, чтобы покрыть блок catch
        doThrow(TelegramApiException.class).when(botService).execute(any(SendMessage.class));

        botService.sendAuthCode("123", "000");

        // Если тест не упал, значит try-catch в сервисе сработал корректно
        verify(botService).execute(any(SendMessage.class));
    }

    // Вспомогательный метод для создания мока Update
    private Update createMockUpdate(String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(12345L);
        return update;
    }
}