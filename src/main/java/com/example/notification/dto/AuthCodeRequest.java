package com.example.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthCodeRequest {
  private String chatId;   // ID чата в Telegram
  private String code;     // Сгенерированный код
  private String userId;   // ID пользователя в вашей системе
}