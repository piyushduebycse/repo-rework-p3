import { Component, OnInit } from '@angular/core';
import { NotificationService, Notification } from '../../../core/services/notification.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notification-bell',
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.component.scss']
})
export class NotificationBellComponent implements OnInit {
  notifications: Notification[] = [];
  unreadCount = 0;

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    this.notificationService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });
  }

  loadNotifications(): void {
    this.notificationService.getNotifications().subscribe({
      next: (data) => {
        this.notifications = data.slice(0, 10); // Show latest 10
        const unread = data.filter(n => !n.isRead).length;
        this.notificationService.updateUnreadCount(unread);
      },
      error: (err) => console.error('Failed to load notifications', err)
    });
  }

  markAsRead(notification: Notification, event: Event): void {
    event.stopPropagation();
    if (!notification.isRead) {
      this.notificationService.markAsRead(notification.id).subscribe(() => {
        notification.isRead = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
        this.notificationService.updateUnreadCount(this.unreadCount);
      });
    }
  }

  markAllAsRead(event: Event): void {
    event.stopPropagation();
    this.notificationService.markAllAsRead().subscribe(() => {
      this.notifications.forEach(n => n.isRead = true);
    });
  }

  handleNotificationClick(notification: Notification): void {
    if (!notification.isRead) {
      this.markAsRead(notification, new Event('click')); // Mark read silently
    }
    
    // Navigate based on type
    if (notification.type.includes('LEAVE')) {
      this.router.navigate(['/manager/leaves/team']); // Or my leaves depending on audience, for now direct to leaves
    } else if (notification.type.includes('REVIEW')) {
      this.router.navigate(['/performance/my']);
    }
  }
}
