import { Component, OnInit } from '@angular/core';
import { PerformanceService, ReviewResponse } from '../../../core/services/performance.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-performance-manager',
  templateUrl: './performance-manager.component.html',
  styleUrls: ['./performance-manager.component.scss']
})
export class PerformanceManagerComponent implements OnInit {
  displayedColumns: string[] = ['employeeName', 'reviewYear', 'status', 'submittedAt', 'actions'];
  dataSource: ReviewResponse[] = [];

  selectedReview: ReviewResponse | null = null;
  feedbackText = '';
  score = 0;

  constructor(
    private performanceService: PerformanceService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadTeamReviews();
  }

  loadTeamReviews(): void {
    this.performanceService.getTeamReviews().subscribe({
      next: (data) => this.dataSource = data,
      error: () => this.snackBar.open('Failed to load team reviews.', 'Close', { duration: 3000 })
    });
  }

  viewReview(review: ReviewResponse): void {
    this.selectedReview = review;
    this.score = review.managerRating || 3;
    this.feedbackText = review.managerFeedback || '';
  }

  closeReview(): void {
    this.selectedReview = null;
  }

  submitFeedback(): void {
    if (!this.selectedReview) return;

    this.performanceService.submitFeedback(this.selectedReview.id, {
      managerRating: this.score,
      managerFeedback: this.feedbackText
    }).subscribe({
      next: () => {
        this.snackBar.open('Feedback submitted successfully.', 'Close', { duration: 3000 });
        this.closeReview();
        this.loadTeamReviews();
      },
      error: () => this.snackBar.open('Failed to submit feedback.', 'Close', { duration: 3000 })
    });
  }
}
