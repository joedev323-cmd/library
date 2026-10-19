package com.example.libback.notification.repository;

import com.example.libback.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByStatusAndReminderAtLessThanEqual(
            String status, Instant now);

    @Query("""
    SELECT n FROM Notification n
    WHERE n.status = 'PENDING'
      AND n.reminderAt <= :now
      AND (
          n.nextAttemptAt IS NULL
          OR n.nextAttemptAt <= :now
      )
    """)
    List<Notification> findDueNotifications(
            @Param("now") Instant now
    );

    @Modifying
    @Query("""
    UPDATE Notification n
    SET n.status = 'PROCESSING',
        n.processingAt = :now,
        n.attempts = n.attempts + 1
    WHERE n.id = :id
      AND n.status = 'PENDING'
      AND n.reminderAt <= :now
      AND (
          n.nextAttemptAt IS NULL
          OR n.nextAttemptAt <= :now
      )
    """)
    int claimNotification(
            @Param("id") Long id,
            @Param("now") Instant now
    );



    Optional<Notification> findByReferenceKey(String referenceKey);

    boolean existsByReferenceKey(String referenceKey);

}