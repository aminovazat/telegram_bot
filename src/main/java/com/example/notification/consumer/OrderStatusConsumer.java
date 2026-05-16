package com.example.notification.consumer;

import com.example.notification.dto.OrderStatusNotificationRequest;
import com.example.notification.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusConsumer implements Consumer<OrderStatusNotificationRequest> {

    private final TelegramBotService telegramBotService;

    @Override
    public void accept(OrderStatusNotificationRequest request) {
        if (request == null) {
            log.warn("Получено пустое уведомление о статусе заказа");
            return;
        }

        log.info(
                "Получено уведомление о заказе: userId={}, orderId={}, status={}",
                request.getUserId(),
                request.getOrderId(),
                request.getStatus()
        );

        if (request.getChatId() == null || request.getChatId().isBlank()) {
            log.warn("Уведомление о заказе пропущено: chatId пустой, orderId={}", request.getOrderId());
            return;
        }

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            log.warn("Уведомление о заказе пропущено: message пустой, orderId={}", request.getOrderId());
            return;
        }

        try {
            telegramBotService.sendOrderStatusNotification(
                    request.getChatId(),
                    request.getMessage()
            );

            log.info(
                    "Уведомление о заказе отправлено в Telegram: chatId={}, orderId={}",
                    request.getChatId(),
                    request.getOrderId()
            );
        } catch (Exception e) {
            log.error(
                    "Ошибка отправки уведомления о заказе в Telegram: orderId={}, error={}",
                    request.getOrderId(),
                    e.getMessage()
            );
        }
    }
}