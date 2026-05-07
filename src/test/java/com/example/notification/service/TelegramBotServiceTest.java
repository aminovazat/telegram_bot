package com.example.notification.service;

import com.example.notification.config.BotConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TelegramBotServiceTest {

  private CapturingTelegramBotService botService;

  @BeforeEach
  void setUp() {
    BotConfig config = new BotConfig();
    config.setToken("test-token");
    config.setUsername("test-bot");
    botService = new CapturingTelegramBotService(config);
  }

  @Test
  void shouldSendStartMessageForStartCommand() {
    Update update = createTextUpdate(1001L, 2002L, "tester", "/start");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    SentMessage message = botService.sentMessages.get(0);
    assertThat(message.chatId()).isEqualTo("1001");
    assertThat(message.text()).contains("Бот запущен");
  }

  @Test
  void shouldSendHealthMessageForTestCommand() {
    Update update = createTextUpdate(1001L, 2002L, "tester", "/test");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    assertThat(botService.sentMessages.get(0).text()).contains("Бот работает отлично");
  }

  @Test
  void shouldSendInfoMessageForInfoCommandWithStableFragments() {
    Update update = createTextUpdate(1001L, 2002L, "tester", "/info");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    String text = botService.sentMessages.get(0).text();
    assertThat(text).contains("Отладочная информация");
    assertThat(text).contains("Chat ID:* `1001`");
    assertThat(text).contains("User ID:* `2002`");
    assertThat(text).contains("Username:* @tester");
    assertThat(text).contains("Рандомный код");
  }

  @Test
  void shouldSendGithubMessageForGithubCommand() {
    Update update = createTextUpdate(1001L, 2002L, "tester", "/github");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    assertThat(botService.sentMessages.get(0).text()).contains("github.com/aminovazat/telegram_bot");
  }

  @Test
  void shouldSendFallbackMessageForUnknownCommand() {
    Update update = createTextUpdate(1001L, 2002L, "tester", "/unknown");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    assertThat(botService.sentMessages.get(0).text()).contains("Используй команды");
  }

  @Test
  void shouldIgnoreNullUpdate() {
    botService.onUpdateReceived(null);

    assertThat(botService.sentMessages).isEmpty();
  }

  @Test
  void shouldIgnoreUpdateWithoutMessage() {
    botService.onUpdateReceived(new Update());

    assertThat(botService.sentMessages).isEmpty();
  }

  @Test
  void shouldIgnoreMessageWithoutText() {
    Update update = mock(Update.class);
    Message message = mock(Message.class);
    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    when(message.hasText()).thenReturn(false);

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).isEmpty();
  }

  @Test
  void shouldHandleEmptyTextAsUnknownCommand() {
    Update update = createTextUpdate(1001L, 2002L, "tester", "");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    assertThat(botService.sentMessages.get(0).text()).contains("Используй команды");
  }

  @Test
  void shouldSendAuthCodeMessage() {
    botService.sendAuthCode("7777", "123456");

    assertThat(botService.sentMessages).hasSize(1);
    SentMessage message = botService.sentMessages.get(0);
    assertThat(message.chatId()).isEqualTo("7777");
    assertThat(message.text()).contains("Код подтверждения:* `123456`");
  }

  @Test
  void shouldDisplayUnknownUsernameInInfoCommand() {
    Update update = createTextUpdate(1001L, 2002L, null, "/info");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    assertThat(botService.sentMessages.get(0).text()).contains("Username:* не указан");
  }

  @Test
  void shouldHandleInfoCommandWhenSenderIsMissing() {
    Update update = mock(Update.class);
    Message message = mock(Message.class);
    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    when(message.hasText()).thenReturn(true);
    when(message.getChatId()).thenReturn(1001L);
    when(message.getText()).thenReturn("/info");
    when(message.getFrom()).thenReturn(null);

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    String text = botService.sentMessages.get(0).text();
    assertThat(text).contains("User ID:* `не указан`");
    assertThat(text).contains("Username:* не указан");
  }

  @Test
  void shouldReturnConfiguredBotCredentials() {
    assertThat(botService.getBotToken()).isEqualTo("test-token");
    assertThat(botService.getBotUsername()).isEqualTo("test-bot");
  }

  @Test
  void shouldDisplayUnknownUsernameWhenUsernameIsEmpty() {
    Update update = createTextUpdate(1001L, 2002L, "", "/info");

    botService.onUpdateReceived(update);

    assertThat(botService.sentMessages).hasSize(1);
    assertThat(botService.sentMessages.get(0).text())
      .contains("Username:*")
      .doesNotContain("@");
  }

  @Test
  void sendMessageShouldCreateMarkdownTelegramMessage() {
    ExecutingTelegramBotService executingBotService = new ExecutingTelegramBotService(createConfig());

    executingBotService.sendMessage("chat-9", "hello");

    assertThat(executingBotService.executedMethods).hasSize(1);
    SendMessage message = (SendMessage) executingBotService.executedMethods.get(0);
    assertThat(message.getChatId()).isEqualTo("chat-9");
    assertThat(message.getText()).isEqualTo("hello");
    assertThat(message.getParseMode()).isEqualTo("Markdown");
  }

  @Test
  void sendMessageShouldSwallowTelegramApiException() {
    ExecutingTelegramBotService executingBotService = new ExecutingTelegramBotService(createConfig());
    executingBotService.exceptionToThrow = new TelegramApiException("Telegram is down");

    executingBotService.sendMessage("chat-9", "hello");

    assertThat(executingBotService.executedMethods).hasSize(1);
  }

  private static Update createTextUpdate(Long chatId, Long userId, String username, String text) {
    Update update = mock(Update.class);
    Message message = mock(Message.class);
    User user = mock(User.class);

    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    when(message.hasText()).thenReturn(true);
    when(message.getChatId()).thenReturn(chatId);
    when(message.getText()).thenReturn(text);
    when(message.getFrom()).thenReturn(user);
    when(user.getId()).thenReturn(userId);
    when(user.getUserName()).thenReturn(username);

    return update;
  }

  private static BotConfig createConfig() {
    BotConfig config = new BotConfig();
    config.setToken("test-token");
    config.setUsername("test-bot");
    return config;
  }

  private static class CapturingTelegramBotService extends TelegramBotService {

    private final List<SentMessage> sentMessages = new ArrayList<>();

    private CapturingTelegramBotService(BotConfig botConfig) {
      super(botConfig);
    }

    @Override
    protected void sendMessage(String chatId, String text) {
      sentMessages.add(new SentMessage(chatId, text));
    }
  }

  private record SentMessage(String chatId, String text) {
  }

  private static class ExecutingTelegramBotService extends TelegramBotService {

    private final List<BotApiMethod<?>> executedMethods = new ArrayList<>();
    private TelegramApiException exceptionToThrow;

    private ExecutingTelegramBotService(BotConfig botConfig) {
      super(botConfig);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method)
      throws TelegramApiException {
      executedMethods.add(method);
      if (exceptionToThrow != null) {
        throw exceptionToThrow;
      }
      return null;
    }
  }
}
