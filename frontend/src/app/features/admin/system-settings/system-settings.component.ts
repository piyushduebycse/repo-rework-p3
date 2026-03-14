import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  AdminService,
  DepartmentResponse,
  DesignationResponse,
  LeaveTypeResponse,
  HolidayResponse
} from '../../../core/services/admin.service';

@Component({
  selector: 'app-system-settings',
  templateUrl: './system-settings.component.html',
  styleUrls: ['./system-settings.component.scss']
})
export class SystemSettingsComponent implements OnInit {
  // ── Departments ──
  departments: DepartmentResponse[] = [];
  newDeptName = '';
  editingDeptId: number | null = null;
  editingDeptName = '';
  editingDeptActive = true;

  // ── Designations ──
  designations: DesignationResponse[] = [];
  newDesigTitle = '';
  editingDesigId: number | null = null;
  editingDesigTitle = '';
  editingDesigActive = true;

  // ── Leave Types ──
  leaveTypes: LeaveTypeResponse[] = [];
  newLtName = '';
  newLtQuota = 0;
  editingLtId: number | null = null;
  editingLtName = '';
  editingLtQuota = 0;
  editingLtActive = true;

  // ── Holidays ──
  holidays: HolidayResponse[] = [];
  newHolidayName = '';
  newHolidayDate = '';
  holidayYear: number = new Date().getFullYear();

  constructor(private adminService: AdminService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.adminService.getDepartments().subscribe(d => this.departments = d);
    this.adminService.getDesignations().subscribe(d => this.designations = d);
    this.adminService.getLeaveTypes().subscribe(d => this.leaveTypes = d);
    this.adminService.getHolidays(this.holidayYear).subscribe(d => this.holidays = d);
  }

  // ── Department Methods ──

  addDepartment(): void {
    if (!this.newDeptName.trim()) return;
    this.adminService.addDepartment({ name: this.newDeptName.trim() }).subscribe({
      next: () => {
        this.newDeptName = '';
        this.snackBar.open('Department added', 'Close', { duration: 2000 });
        this.adminService.getDepartments().subscribe(d => this.departments = d);
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed to add department', 'Close', { duration: 3000 })
    });
  }

  startEditDept(dept: DepartmentResponse): void {
    this.editingDeptId = dept.id;
    this.editingDeptName = dept.name;
    this.editingDeptActive = dept.isActive;
  }

  cancelEditDept(): void {
    this.editingDeptId = null;
  }

  saveDept(): void {
    if (!this.editingDeptName.trim()) return;
    this.adminService.updateDepartment(this.editingDeptId!, {
      name: this.editingDeptName.trim(),
      isActive: this.editingDeptActive
    }).subscribe({
      next: () => {
        this.editingDeptId = null;
        this.snackBar.open('Department updated', 'Close', { duration: 2000 });
        this.adminService.getDepartments().subscribe(d => this.departments = d);
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed to update', 'Close', { duration: 3000 })
    });
  }

  // ── Designation Methods ──

  addDesignation(): void {
    if (!this.newDesigTitle.trim()) return;
    this.adminService.addDesignation({ title: this.newDesigTitle.trim() }).subscribe({
      next: () => {
        this.newDesigTitle = '';
        this.snackBar.open('Designation added', 'Close', { duration: 2000 });
        this.adminService.getDesignations().subscribe(d => this.designations = d);
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed to add designation', 'Close', { duration: 3000 })
    });
  }

  startEditDesig(desig: DesignationResponse): void {
    this.editingDesigId = desig.id;
    this.editingDesigTitle = desig.title;
    this.editingDesigActive = desig.isActive;
  }

  cancelEditDesig(): void {
    this.editingDesigId = null;
  }

  saveDesig(): void {
    if (!this.editingDesigTitle.trim()) return;
    this.adminService.updateDesignation(this.editingDesigId!, {
      title: this.editingDesigTitle.trim(),
      isActive: this.editingDesigActive
    }).subscribe({
      next: () => {
        this.editingDesigId = null;
        this.snackBar.open('Designation updated', 'Close', { duration: 2000 });
        this.adminService.getDesignations().subscribe(d => this.designations = d);
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed to update', 'Close', { duration: 3000 })
    });
  }

  // ── Leave Type Methods ──

  addLeaveType(): void {
    if (!this.newLtName.trim() || this.newLtQuota < 0) return;
    this.adminService.addLeaveType({ name: this.newLtName.trim(), defaultQuota: this.newLtQuota }).subscribe({
      next: () => {
        this.newLtName = '';
        this.newLtQuota = 0;
        this.snackBar.open('Leave type added', 'Close', { duration: 2000 });
        this.adminService.getLeaveTypes().subscribe(d => this.leaveTypes = d);
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed to add leave type', 'Close', { duration: 3000 })
    });
  }

  startEditLt(lt: LeaveTypeResponse): void {
    this.editingLtId = lt.id;
    this.editingLtName = lt.name;
    this.editingLtQuota = lt.defaultQuota;
    this.editingLtActive = lt.isActive;
  }

  cancelEditLt(): void {
    this.editingLtId = null;
  }

  saveLt(): void {
    if (!this.editingLtName.trim()) return;
    this.adminService.updateLeaveType(this.editingLtId!, {
      name: this.editingLtName.trim(),
      defaultQuota: this.editingLtQuota,
      isActive: this.editingLtActive
    }).subscribe({
      next: () => {
        this.editingLtId = null;
        this.snackBar.open('Leave type updated', 'Close', { duration: 2000 });
        this.adminService.getLeaveTypes().subscribe(d => this.leaveTypes = d);
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed to update', 'Close', { duration: 3000 })
    });
  }

  // ── Holiday Methods ──

  addHoliday(): void {
    if (!this.newHolidayName.trim() || !this.newHolidayDate) return;
    const date = this.newHolidayDate;
    const year = new Date(date).getFullYear();
    this.adminService.addHoliday({ name: this.newHolidayName.trim(), date, year }).subscribe({
      next: () => {
        this.newHolidayName = '';
        this.newHolidayDate = '';
        this.snackBar.open('Holiday added', 'Close', { duration: 2000 });
        this.adminService.getHolidays(this.holidayYear).subscribe(d => this.holidays = d);
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed to add holiday', 'Close', { duration: 3000 })
    });
  }

  deleteHoliday(id: number): void {
    this.adminService.deleteHoliday(id).subscribe({
      next: () => {
        this.snackBar.open('Holiday removed', 'Close', { duration: 2000 });
        this.adminService.getHolidays(this.holidayYear).subscribe(d => this.holidays = d);
      },
      error: () => this.snackBar.open('Failed to delete holiday', 'Close', { duration: 3000 })
    });
  }

  changeHolidayYear(): void {
    this.adminService.getHolidays(this.holidayYear).subscribe(d => this.holidays = d);
  }
}
