package com.example.notification.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // Используем профиль application-test.yaml, если он есть
class BotConfigTest {

    @Autowired
    private BotConfig botConfig;

    @Test
    void testConfigurationPropertiesBinding() {
        // Проверяем, что поля не пустые (подставятся из твоего YAML)
        assertThat(botConfig.getToken()).isNotNull();
        assertThat(botConfig.getUsername()).isEqualTo("Food_delivery_not_bot");
    }

    @Test
    void testLombokMethods() {
        // Тест для покрытия геттеров, сеттеров, equals и hashCode (для 90%+ покрытия)
        BotConfig config1 = new BotConfig();
        config1.setToken("test-token");
        config1.setUsername("test-user");

        BotConfig config2 = new BotConfig();
        config2.setToken("test-token");
        config2.setUsername("test-user");

        // Проверка Getter
        assertThat(config1.getToken()).isEqualTo("test-token");
        assertThat(config1.getUsername()).isEqualTo("test-user");

        // Проверка Equals и HashCode (Lombok @Data)
        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());

        // Проверка ToString
        assertThat(config1.toString()).contains("test-token", "test-user");
    }
}