package com.example.libback.repository;

import com.example.libback.model.Auditlog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<Auditlog, Long> {
    // Custom query to find logs by a specific user (optional but useful for reports)
    List<Auditlog> findByActorId(long actorId);
}