package com.revworkforce.service.impl;

import com.revworkforce.entity.Employee;
import com.revworkforce.entity.Notification;
import com.revworkforce.entity.NotificationType;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.EmployeeRepository;
import com.revworkforce.repository.NotificationRepository;
import com.revworkforce.service.interfaces.AuthService;
import com.revworkforce.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final AuthService authService;

    @Override
    public void createNotification(Long recipientId, String message, NotificationType type, Long referenceId) {
        Employee recipient = employeeRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getMyNotifications() {
        Employee current = authService.getCurrentUser();
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(current.getId());
    }

    @Override
    public List<Notification> getMyUnreadNotifications() {
        Employee current = authService.getCurrentUser();
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(current.getId());
    }

    @Override
    public void markAsRead(Long notificationId) {
        Employee current = authService.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized to access this notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        Employee current = authService.getCurrentUser();
        List<Notification> unread = notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(current.getId());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }
}
