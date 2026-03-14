import { Component, OnInit } from '@angular/core';
import { AuthService, User } from '../../core/services/auth.service';
import { LeaveService, LeaveBalance, CompanyHoliday } from '../../core/services/leave.service';
import { AdminService, AdminMetricsResponse } from '../../core/services/admin.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  leaveBalances: LeaveBalance[] = [];
  holidays: CompanyHoliday[] = [];
  adminMetrics: AdminMetricsResponse | null = null;

  constructor(
    private authService: AuthService,
    private leaveService: LeaveService,
    private adminService: AdminService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      this.loadDashboardData();
    });
  }

  loadDashboardData(): void {
    if (!this.currentUser) return;

    // Everyone gets their leave balances and company holidays
    this.leaveService.getBalances().subscribe(res => this.leaveBalances = res);
    this.leaveService.getHolidays().subscribe(res => {
      this.holidays = res.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    });

    // Admin specific data
    if (this.currentUser.role === 'ADMIN') {
      this.adminService.getDashboardMetrics().subscribe(res => this.adminMetrics = res);
    }
  }
}
