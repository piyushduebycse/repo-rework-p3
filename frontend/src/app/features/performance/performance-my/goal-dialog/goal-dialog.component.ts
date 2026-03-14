import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { GoalService } from '../../../../core/services/goal.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-goal-dialog',
  templateUrl: './goal-dialog.component.html',
  styleUrls: ['./goal-dialog.component.scss']
})
export class GoalDialogComponent {
  goalForm: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<GoalDialogComponent>,
    private goalService: GoalService,
    private snackBar: MatSnackBar
  ) {
    this.goalForm = this.fb.group({
      description: ['', Validators.required],
      priority: ['MEDIUM', Validators.required],
      deadline: [null]
    });
  }

  onSubmit(): void {
    if (this.goalForm.valid) {
      this.isSubmitting = true;
      const formValue = this.goalForm.value;
      
      this.goalService.createGoal(formValue).subscribe({
        next: (createdGoal) => {
          this.snackBar.open('Goal created successfully', 'Close', { duration: 3000 });
          this.dialogRef.close(createdGoal);
        },
        error: (err) => {
          console.error(err);
          this.snackBar.open('Failed to create goal', 'Close', { duration: 3000 });
          this.isSubmitting = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
