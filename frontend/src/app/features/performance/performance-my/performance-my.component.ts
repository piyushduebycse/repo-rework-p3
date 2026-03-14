import { Component, OnInit, OnDestroy } from '@angular/core';
import { PerformanceService, ReviewResponse } from '../../../core/services/performance.service';
import { GoalService, GoalResponse } from '../../../core/services/goal.service';
import { AuthService, User } from '../../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { GoalDialogComponent } from './goal-dialog/goal-dialog.component';

@Component({
  selector: 'app-performance-my',
  templateUrl: './performance-my.component.html',
  styleUrls: ['./performance-my.component.scss']
})
export class PerformanceMyComponent implements OnInit, OnDestroy {
  reviews: ReviewResponse[] = [];
  goals: GoalResponse[] = [];
  currentUser: User | null = null;
  expandedReviewId: number | null = null;
  private userSub!: Subscription;

  constructor(
    private performanceService: PerformanceService,
    private goalService: GoalService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.userSub = this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.loadMyPerformanceData();
      }
    });
  }

  ngOnDestroy(): void {
    this.userSub?.unsubscribe();
  }

  loadMyPerformanceData(): void {
    this.performanceService.getMyReviews().subscribe({
      next: res => this.reviews = res,
      error: () => this.snackBar.open('Failed to load reviews', 'Close', { duration: 3000 })
    });
    this.goalService.getMyGoals().subscribe({
      next: res => this.goals = res,
      error: () => this.snackBar.open('Failed to load goals', 'Close', { duration: 3000 })
    });
  }

  toggleExpand(id: number): void {
    if (this.expandedReviewId === id) {
      this.expandedReviewId = null;
    } else {
      this.expandedReviewId = id;
    }
  }

  openGoalDialog(): void {
    const dialogRef = this.dialog.open(GoalDialogComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadMyPerformanceData(); // reload goals
      }
    });
  }

  updateGoalProgress(id: number, event: any): void {
    const progress = Number(event.target ? event.target.value : event);
    this.goalService.updateGoal(id, { progressPercentage: progress }).subscribe({
      next: () => {
        this.snackBar.open('Progress updated', 'Close', { duration: 2000 });
        this.loadMyPerformanceData();
      },
      error: () => this.snackBar.open('Failed to update progress', 'Close', { duration: 3000 })
    });
  }

  markGoalComplete(id: number): void {
    this.goalService.updateGoal(id, { status: 'COMPLETED', progressPercentage: 100 }).subscribe({
      next: () => {
        this.snackBar.open('Goal marked as complete', 'Close', { duration: 3000 });
        this.loadMyPerformanceData();
      },
      error: () => this.snackBar.open('Failed to complete goal', 'Close', { duration: 3000 })
    });
  }

  deleteGoal(id: number): void {
    if (confirm('Are you sure you want to delete this goal?')) {
      this.goalService.deleteGoal(id).subscribe({
        next: () => {
          this.snackBar.open('Goal deleted', 'Close', { duration: 3000 });
          this.loadMyPerformanceData();
        },
        error: () => this.snackBar.open('Failed to delete goal', 'Close', { duration: 3000 })
      });
    }
  }
}
