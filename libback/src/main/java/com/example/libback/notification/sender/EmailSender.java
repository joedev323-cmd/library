package com.example.libback.notification.sender;

public interface EmailSender {

    void send(String to, String subject, String message);
}