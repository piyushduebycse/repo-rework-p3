import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ReviewResponse {
  id: number;
  employeeId: number;
  employeeName: string;
  reviewYear: number;
  keyDeliverables: string;
  accomplishments: string;
  areasOfImprovement: string;
  selfAssessmentRating: number;
  status: string;
  managerFeedback: string;
  managerRating: number;
  submittedAt: string;
  reviewedAt: string;
}

export interface ReviewRequest {
  reviewYear: number;
  keyDeliverables: string;
  accomplishments: string;
  areasOfImprovement: string;
  selfAssessmentRating: number;
}

export interface FeedbackRequest {
  managerFeedback: string;
  managerRating: number;
}

@Injectable({ providedIn: 'root' })
export class PerformanceService {
  constructor(private http: HttpClient) {}

  saveReviewDraft(request: ReviewRequest): Observable<ReviewResponse> {
    return this.http.post<ReviewResponse>(`${environment.apiUrl}/performance/review`, request);
  }

  submitReview(id: number): Observable<ReviewResponse> {
    return this.http.put<ReviewResponse>(`${environment.apiUrl}/performance/review/${id}/submit`, {});
  }

  getMyReviews(): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(`${environment.apiUrl}/performance/reviews`);
  }

  // Manager endpoints
  getTeamReviews(): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(`${environment.apiUrl}/manager/performance/reviews`);
  }

  submitFeedback(id: number, request: FeedbackRequest): Observable<ReviewResponse> {
    return this.http.put<ReviewResponse>(`${environment.apiUrl}/manager/performance/reviews/${id}/feedback`, request);
  }
}
