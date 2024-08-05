package com.amorgakco.backend.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
    SMS("sms"),
    FCM("fcm");

    private final String type;
}
