package com.example.libback.service;

import com.example.libback.model.Auditlog;
import com.example.libback.model.User;
import com.example.libback.repository.AuditLogRepository;
import com.example.libback.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void logAction(String accessionId, String action, String details) {
        Auditlog log = new Auditlog();
        log.setAccessionId(accessionId);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        // 1. Extract the username of the logged-in staff/admin
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) 
                ? ((UserDetails) principal).getUsername() 
                : "SYSTEM";

        // 2. Resolve the username to their numeric User ID (actorId)
        long actorId = 0L; // Default system/fallback ID
        if (!"SYSTEM".equals(username)) {
            Optional<User> currentUser = userRepository.findByUsername(username);
            if (currentUser.isPresent()) {
                actorId = currentUser.get().getUserId(); // Make sure your User entity has .getId()
            }
        }
        log.setActorId(actorId);

        auditLogRepository.save(log);
    }
}