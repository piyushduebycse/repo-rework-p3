import { Component, OnInit } from '@angular/core';
import { LeaveService, LeaveApplication } from '../../../core/services/leave.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-leave-manager',
  templateUrl: './leave-manager.component.html',
  styleUrls: ['./leave-manager.component.scss']
})
export class LeaveManagerComponent implements OnInit {
  displayedColumns: string[] = ['employeeName', 'leaveTypeName', 'dates', 'reason', 'status', 'actions'];
  dataSource: LeaveApplication[] = [];

  constructor(private leaveService: LeaveService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadTeamLeaves();
  }

  loadTeamLeaves(): void {
    this.leaveService.getTeamLeaves().subscribe({
      next: (data) => {
        this.dataSource = data;
      },
      error: () => this.snackBar.open('Failed to load team leaves', 'Close', { duration: 3000 })
    });
  }

  approveLeave(id: number): void {
    this.leaveService.approveLeave(id, 'Approved by manager.').subscribe({
      next: () => {
        this.snackBar.open('Leave approved successfully', 'Close', { duration: 3000 });
        this.loadTeamLeaves();
      },
      error: () => this.snackBar.open('Error approving leave', 'Close', { duration: 3000 })
    });
  }

  rejectLeave(id: number): void {
    const reason = prompt('Please enter a reason for rejection:');
    if (reason !== null && reason.trim() !== '') {
      this.leaveService.rejectLeave(id, reason).subscribe({
        next: () => {
          this.snackBar.open('Leave rejected', 'Close', { duration: 3000 });
          this.loadTeamLeaves();
        },
        error: () => this.snackBar.open('Error rejecting leave', 'Close', { duration: 3000 })
      });
    }
  }
}
