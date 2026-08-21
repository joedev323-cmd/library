package com.example.libback.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log") // Changed hyphen to underscore to keep SQL databases happy
public class Auditlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long movementId;  

    @Column(nullable = false, length = 50)
    private String accessionId; 
    @Column(nullable = false, length = 50)
    private String action; // e.g., "CHECKOUT", "RETURN", "RENEWAL", "DAMAGE_FLAG"

    @Column(columnDefinition = "TEXT")
    private String details; // Stores human-readable notes about the event history

    private long actorId; // The user_id of the staff member or student who initiated the event

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // Real system clock tracking

    public Auditlog() {}

    public Auditlog(String accessionId, String action, String details, long actorId) {
        this.accessionId = accessionId;
        this.action = action;
        this.details = details;
        this.actorId = actorId;
        this.timestamp = LocalDateTime.now();
    }

    public long getMovementId() {
        return movementId;
    }

    public void setMovementId(long movementId) {
        this.movementId = movementId;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getActorId() {
        return actorId;
    }

    public void setActorId(long actorId) {
        this.actorId = actorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    
}