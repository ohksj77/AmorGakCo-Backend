package com.amorgakco.backend.notification.infrastructure.consumer.fcm;

import com.amorgakco.backend.fcmtoken.repository.FcmTokenRepository;
import com.amorgakco.backend.notification.infrastructure.consumer.NotificationRequest;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmWebPushConsumer {

    private final FcmTokenRepository fcmTokenRepository;

    @RabbitListener(queues = "fcm")
    public void send(final NotificationRequest request) {
        fcmTokenRepository
                .findById(request.receiver().getId().toString())
                .ifPresent(
                        token ->
                                createMessage(
                                        token.getToken(),
                                        request.notificationTitle().getTitle(),
                                        request.content()));
    }

    public Message createMessage(final String token, final String title, final String content) {
        return Message.builder()
                .setWebpushConfig(
                        WebpushConfig.builder()
                                .setNotification(
                                        WebpushNotification.builder()
                                                .setTitle(title)
                                                .setBody(content)
                                                .build())
                                .build())
                .setToken(token)
                .build();
    }
}