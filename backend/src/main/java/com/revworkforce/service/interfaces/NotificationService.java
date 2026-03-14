package com.revworkforce.service.interfaces;

import com.revworkforce.entity.Notification;

import java.util.List;

public interface NotificationService {
    void createNotification(Long recipientId, String message, com.revworkforce.entity.NotificationType type, Long referenceId);
    List<Notification> getMyNotifications();
    List<Notification> getMyUnreadNotifications();
    void markAsRead(Long notificationId);
    void markAllAsRead();
}
