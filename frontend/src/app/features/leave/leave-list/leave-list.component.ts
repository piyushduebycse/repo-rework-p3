import { Component, OnInit } from '@angular/core';
import { LeaveService, LeaveApplication } from '../../../core/services/leave.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-leave-list',
  templateUrl: './leave-list.component.html',
  styleUrls: ['./leave-list.component.scss']
})
export class LeaveListComponent implements OnInit {
  displayedColumns: string[] = ['leaveTypeName', 'startDate', 'endDate', 'status', 'appliedAt', 'actions'];
  dataSource: LeaveApplication[] = [];

  constructor(private leaveService: LeaveService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadMyLeaves();
  }

  loadMyLeaves(): void {
    this.leaveService.getMyLeaves().subscribe({
      next: (data) => {
        this.dataSource = data;
      },
      error: () => this.snackBar.open('Failed to load leaves', 'Close', { duration: 3000 })
    });
  }

  cancelLeave(id: number): void {
    if(confirm('Are you sure you want to cancel this leave application?')) {
      this.leaveService.cancelLeave(id).subscribe({
        next: () => {
          this.snackBar.open('Leave application cancelled', 'Close', { duration: 3000 });
          this.loadMyLeaves();
        },
        error: (err) => this.snackBar.open(err.error?.message || 'Failed to cancel leave', 'Close', { duration: 3000 })
      });
    }
  }
}
