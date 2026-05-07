package com.example.notification.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BotConfigTest {

  @Test
  void shouldExposeConfiguredTokenAndUsername() {
    BotConfig config = new BotConfig();

    config.setToken("token");
    config.setUsername("bot");

    assertThat(config.getToken()).isEqualTo("token");
    assertThat(config.getUsername()).isEqualTo("bot");
    assertThat(config.toString()).contains("token", "bot");
  }

  @Test
  void shouldUseLombokValueSemantics() {
    BotConfig first = new BotConfig();
    first.setToken("token");
    first.setUsername("bot");

    BotConfig same = new BotConfig();
    same.setToken("token");
    same.setUsername("bot");

    BotConfig different = new BotConfig();
    different.setToken("another-token");
    different.setUsername("bot");

    assertThat(first).isEqualTo(same);
    assertThat(first).hasSameHashCodeAs(same);
    assertThat(first).isNotEqualTo(different);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("token");
  }
}
