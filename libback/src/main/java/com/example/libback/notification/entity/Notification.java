package com.example.libback.notification.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 5000)
    private String message;

    @Column(nullable = false)
    private Instant deadlineAt;

    @Column(nullable = false)
    private Instant reminderAt;

    @Column(nullable = false)
    private String status = "PENDING";

    private Instant sentAt;

    private int attempts;

    @Column(length = 2000)
    private String lastError;

    private Instant processingAt;
    private Instant nextAttemptAt;

    @Column(name = "reference_key", unique = true, length = 150)
    private String referenceKey;

    public String getReferenceKey() {
        return referenceKey;
    }

    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }

    public Long getId() { return id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getDeadlineAt() { return deadlineAt; }
    public void setDeadlineAt(Instant deadlineAt) {
        this.deadlineAt = deadlineAt;
    }

    public Instant getReminderAt() { return reminderAt; }
    public void setReminderAt(Instant reminderAt) {
        this.reminderAt = reminderAt;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getLastError() { return lastError; }
    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Instant getProcessingAt() {
        return processingAt;
    }

    public void setProcessingAt(Instant processingAt) {
        this.processingAt = processingAt;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(Instant nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }
}