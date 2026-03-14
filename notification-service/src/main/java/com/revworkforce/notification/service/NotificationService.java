package com.revworkforce.notification.service;

import com.revworkforce.notification.entity.Notification;
import com.revworkforce.notification.entity.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(Long recipientId, String message, NotificationType type, Long referenceId);
    List<Notification> getMyNotifications();
    List<Notification> getMyUnreadNotifications();
    void markAsRead(Long notificationId);
    void markAllAsRead();
}
