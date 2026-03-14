import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PerformanceService, ReviewRequest } from '../../../core/services/performance.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-performance-apply',
  templateUrl: './performance-apply.component.html',
  styleUrls: ['./performance-apply.component.scss']
})
export class PerformanceApplyComponent implements OnInit {
  reviewForm!: FormGroup;
  submitting = false;
  editingId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private performanceService: PerformanceService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.reviewForm = this.fb.group({
      reviewYear: [new Date().getFullYear(), Validators.required],
      keyDeliverables: ['', [Validators.required, Validators.minLength(10)]],
      accomplishments: ['', [Validators.required, Validators.minLength(20)]],
      areasOfImprovement: ['', [Validators.required, Validators.minLength(20)]],
      selfAssessmentRating: [3, [Validators.required, Validators.min(1), Validators.max(5)]]
    });

    // Check if editing a draft
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editingId = +idParam;
    }
  }

  onSubmit(): void {
    if (this.reviewForm.invalid) return;

    this.submitting = true;
    const request: ReviewRequest = this.reviewForm.value;

    // Save draft first, then submit
    this.performanceService.saveReviewDraft(request).subscribe({
      next: (saved) => {
        this.performanceService.submitReview(saved.id).subscribe({
          next: () => {
            this.snackBar.open('Self-review submitted successfully.', 'Close', { duration: 3000 });
            this.router.navigate(['/performance/my']);
          },
          error: (err) => {
            this.snackBar.open(err.error?.message || 'Failed to submit review.', 'Close', { duration: 5000 });
            this.submitting = false;
          }
        });
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Failed to save review.', 'Close', { duration: 5000 });
        this.submitting = false;
      }
    });
  }
}
