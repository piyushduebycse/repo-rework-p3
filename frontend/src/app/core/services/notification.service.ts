import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Notification {
  id: number;
  recipientId: number;
  message: string;
  type: string;
  isRead: boolean;
  referenceId: number;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${environment.apiUrl}/notifications`);
  }

  getUnreadNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${environment.apiUrl}/notifications/unread`).pipe(
      tap(notifications => this.unreadCountSubject.next(notifications.length))
    );
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/notifications/${id}/read`, null);
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/notifications/read-all`, null).pipe(
      tap(() => this.unreadCountSubject.next(0))
    );
  }

  updateUnreadCount(count: number): void {
    this.unreadCountSubject.next(count);
  }
}
