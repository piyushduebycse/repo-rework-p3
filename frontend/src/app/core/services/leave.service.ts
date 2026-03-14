import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LeaveBalance {
  id: number;
  leaveTypeId: number;
  leaveTypeName: string;
  totalDays: number;
  usedDays: number;
  remainingDays: number;
}

export interface LeaveRequest {
  leaveTypeId: number;
  startDate: string;
  endDate: string;
  reason: string;
}

export interface LeaveApplication {
  id: number;
  employeeId: number;
  employeeName: string;
  leaveTypeId: number;
  leaveTypeName: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: string;
  managerComment?: string;
  appliedAt: string;
}

export interface CompanyHoliday {
  id: number;
  name: string;
  date: string;
  year: number;
}

@Injectable({ providedIn: 'root' })
export class LeaveService {
  constructor(private http: HttpClient) {}

  getBalances(): Observable<LeaveBalance[]> {
    return this.http.get<LeaveBalance[]>(`${environment.apiUrl}/leaves/balance`);
  }

  applyLeave(request: LeaveRequest): Observable<LeaveApplication> {
    return this.http.post<LeaveApplication>(`${environment.apiUrl}/leaves/apply`, request);
  }

  getMyLeaves(): Observable<LeaveApplication[]> {
    return this.http.get<LeaveApplication[]>(`${environment.apiUrl}/leaves/my`);
  }

  cancelLeave(id: number): Observable<any> {
    return this.http.put(`${environment.apiUrl}/leaves/${id}/cancel`, {});
  }

  getHolidays(): Observable<CompanyHoliday[]> {
    return this.http.get<CompanyHoliday[]>(`${environment.apiUrl}/leaves/holidays`);
  }

  // Manager specific
  getTeamLeaves(): Observable<LeaveApplication[]> {
    return this.http.get<LeaveApplication[]>(`${environment.apiUrl}/manager/leaves/team`);
  }

  approveLeave(id: number, comment?: string): Observable<LeaveApplication> {
    return this.http.put<LeaveApplication>(`${environment.apiUrl}/manager/leaves/${id}/approve`, { comment });
  }

  rejectLeave(id: number, comment: string): Observable<LeaveApplication> {
    return this.http.put<LeaveApplication>(`${environment.apiUrl}/manager/leaves/${id}/reject`, { comment });
  }
}
