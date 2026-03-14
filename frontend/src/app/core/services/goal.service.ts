import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface GoalRequest {
  description: string;
  deadline?: string;
  priority: string;
}

export interface GoalUpdate {
  status?: string;
  progressPercentage?: number;
}

export interface GoalResponse {
  id: number;
  employeeId: number;
  employeeName: string;
  description: string;
  deadline: string;
  priority: string;
  status: string;
  progressPercentage: number;
  managerComment?: string;
}

@Injectable({ providedIn: 'root' })
export class GoalService {
  constructor(private http: HttpClient) {}

  createGoal(request: GoalRequest): Observable<GoalResponse> {
    return this.http.post<GoalResponse>(`${environment.apiUrl}/goals`, request);
  }

  getMyGoals(): Observable<GoalResponse[]> {
    return this.http.get<GoalResponse[]>(`${environment.apiUrl}/goals`);
  }

  updateGoal(id: number, update: GoalUpdate): Observable<GoalResponse> {
    return this.http.put<GoalResponse>(`${environment.apiUrl}/goals/${id}`, update);
  }

  deleteGoal(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/goals/${id}`);
  }

  // Manager functions
  getTeamMemberGoals(empId: number): Observable<GoalResponse[]> {
    return this.http.get<GoalResponse[]>(`${environment.apiUrl}/manager/goals/${empId}`);
  }

  addComment(id: number, comment: string): Observable<GoalResponse> {
    return this.http.put<GoalResponse>(`${environment.apiUrl}/manager/goals/${id}/comment`, { comment });
  }
}
