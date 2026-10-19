package com.example.libback.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record NotificationRequest(
        @NotBlank
        @Email
        String recipient,

        @NotBlank
        @Size(max = 200)
        String subject,

        @NotBlank
        @Size(max = 5000)
        String message,

        @NotNull
        Instant deadlineAt,

        @NotNull
        Instant reminderAt
) {}