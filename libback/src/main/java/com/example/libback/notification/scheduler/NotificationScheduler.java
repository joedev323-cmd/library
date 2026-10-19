package com.example.libback.notification.scheduler;

import com.example.libback.notification.entity.Notification;
import com.example.libback.notification.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    public NotificationScheduler(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedDelay = 30000)
    public void processDueNotifications() {
        for (Notification notification :
                notificationService.findDueNotifications()) {

            Long id = notification.getId();

            if (notificationService.claimNotification(id)) {
                notificationService.sendClaimedNotification(id);
            }
        }
    }
}
