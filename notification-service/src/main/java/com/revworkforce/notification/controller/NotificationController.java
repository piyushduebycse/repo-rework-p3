package com.revworkforce.notification.controller;

import com.revworkforce.common.dto.NotificationRequest;
import com.revworkforce.common.dto.response.NotificationResponse;
import com.revworkforce.notification.entity.Notification;
import com.revworkforce.notification.entity.NotificationType;
import com.revworkforce.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getMyUnreadNotifications().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(
                request.getRecipientId(),
                request.getMessage(),
                NotificationType.valueOf(request.getType()),
                request.getReferenceId()
        );
        return ResponseEntity.ok().build();
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipient().getId())
                .message(notification.getMessage())
                .type(notification.getType().name())
                .isRead(notification.getIsRead())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
