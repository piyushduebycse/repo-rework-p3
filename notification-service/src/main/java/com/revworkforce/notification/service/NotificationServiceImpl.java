package com.revworkforce.notification.service;

import com.revworkforce.notification.entity.Employee;
import com.revworkforce.notification.entity.Notification;
import com.revworkforce.notification.entity.NotificationType;
import com.revworkforce.notification.repository.EmployeeRepository;
import com.revworkforce.notification.repository.NotificationRepository;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;

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
        Long currentUserId = getCurrentUserId();
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUserId);
    }

    @Override
    public List<Notification> getMyUnreadNotifications() {
        Long currentUserId = getCurrentUserId();
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(currentUserId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Long currentUserId = getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized to access this notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        Long currentUserId = getCurrentUserId();
        List<Notification> unread = notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(currentUserId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }
}
