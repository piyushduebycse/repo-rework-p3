import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LeaveService, LeaveBalance } from '../../../core/services/leave.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-leave-apply',
  templateUrl: './leave-apply.component.html',
  styleUrls: ['./leave-apply.component.scss']
})
export class LeaveApplyComponent implements OnInit {
  leaveFormGroup!: FormGroup;
  balances: LeaveBalance[] = [];
  submitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private leaveService: LeaveService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.leaveFormGroup = this.formBuilder.group({
      leaveTypeId: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      reason: ['', Validators.required]
    });

    this.leaveService.getBalances().subscribe({
      next: res => this.balances = res,
      error: () => this.snackBar.open('Failed to load leave balances', 'Close', { duration: 3000 })
    });
  }

  onSubmit(): void {
    if (this.leaveFormGroup.invalid) return;

    this.submitting = true;
    
    // Convert Dates to ISO string
    const req = { ...this.leaveFormGroup.value };
    if (req.startDate instanceof Date) req.startDate = req.startDate.toISOString().split('T')[0];
    if (req.endDate instanceof Date) req.endDate = req.endDate.toISOString().split('T')[0];

    this.leaveService.applyLeave(req).subscribe({
      next: () => {
        this.snackBar.open('Leave application submitted successfully', 'Close', { duration: 3000 });
        this.router.navigate(['/leaves/my']);
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Failed to apply for leave', 'Close', { duration: 5000 });
        this.submitting = false;
      }
    });
  }
}
