package com.example.notification.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthCodeRequestTest {

  @Test
  void noArgsConstructorAndSettersShouldPopulateRequest() {
    AuthCodeRequest request = new AuthCodeRequest();

    request.setChatId("chat-1");
    request.setCode("123456");
    request.setUserId("user-1");

    assertThat(request.getChatId()).isEqualTo("chat-1");
    assertThat(request.getCode()).isEqualTo("123456");
    assertThat(request.getUserId()).isEqualTo("user-1");
    assertThat(request.toString()).contains("chat-1", "123456", "user-1");
  }

  @Test
  void shouldUseLombokValueSemantics() {
    AuthCodeRequest first = new AuthCodeRequest("chat-1", "123456", "user-1");
    AuthCodeRequest same = new AuthCodeRequest("chat-1", "123456", "user-1");
    AuthCodeRequest different = new AuthCodeRequest("chat-2", "123456", "user-1");

    assertThat(first).isEqualTo(same);
    assertThat(first).hasSameHashCodeAs(same);
    assertThat(first).isNotEqualTo(different);
    assertThat(first).isNotEqualTo(null);
    assertThat(first).isNotEqualTo("chat-1");
  }

  @Test
  void shouldHandleNullFieldsInGeneratedMethods() {
    AuthCodeRequest empty = new AuthCodeRequest();
    AuthCodeRequest sameEmpty = new AuthCodeRequest(null, null, null);

    assertThat(empty).isEqualTo(sameEmpty);
    assertThat(empty.hashCode()).isEqualTo(sameEmpty.hashCode());
    assertThat(empty.toString()).contains("chatId=null", "code=null", "userId=null");
  }
}
