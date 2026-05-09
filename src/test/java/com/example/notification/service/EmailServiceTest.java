package com.example.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailService emailService;

  @Test
  void shouldBuildAndSendSimpleEmailMessage() {
    emailService.sendEmail("to@test.com", "Subject", "Body");

    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    verifyNoMoreInteractions(mailSender);

    SimpleMailMessage message = messageCaptor.getValue();
    assertThat(message.getFrom()).isEqualTo("food-delivery@team7.com");
    assertThat(message.getTo()).containsExactly("to@test.com");
    assertThat(message.getSubject()).isEqualTo("Subject");
    assertThat(message.getText()).isEqualTo("Body");
  }
}
