package com.example.libback.service;

import com.example.libback.model.Auditlog;
import com.example.libback.repository.AuditLogRepository;
import com.example.libback.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void logAction(
            String action,
            String entityType,
            String entityId,
            String details
    ) {

        Auditlog log = new Auditlog();

        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()) {

            Object principal = authentication.getPrincipal();

            String username = null;

            if (principal instanceof UserDetails userDetails) {
                username = userDetails.getUsername();
            } else if (principal instanceof String) {
                username = (String) principal;
            }

            if (username != null
                    && !"anonymousUser".equals(username)) {

                userRepository.findByUsername(username)
                        .ifPresent(log::setActor);
            }
        }

        auditLogRepository.save(log);
    }
}
