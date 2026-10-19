package com.example.libback.notification.sender;

public interface NotificationSender {
    void send(String to, String subject, String message);
}
