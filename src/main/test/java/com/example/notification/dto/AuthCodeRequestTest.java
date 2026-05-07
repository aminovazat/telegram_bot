package com.example.notification.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AuthCodeRequestTest {

    @Test
    void testLombokMethods() {
        AuthCodeRequest request1 = new AuthCodeRequest("123", "555", "user1", "test@mfti.ru");
        AuthCodeRequest request2 = new AuthCodeRequest("123", "555", "user1", "test@mfti.ru");

        // Геттеры
        assertThat(request1.getChatId()).isEqualTo("123");
        assertThat(request1.getCode()).isEqualTo("555");

        // Equals/HashCode/ToString
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        assertThat(request1.toString()).contains("123", "test@mfti.ru");

        // Сеттеры (NoArgsConstructor)
        AuthCodeRequest empty = new AuthCodeRequest();
        empty.setChatId("456");
        assertThat(empty.getChatId()).isEqualTo("456");
    }
}