import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  AdminService,
  EmployeeResponse,
  EmployeeRequest,
  AdminMetricsResponse,
  DepartmentResponse,
  DesignationResponse
} from '../../../core/services/admin.service';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {
  displayedColumns: string[] = [
    'employeeId', 'name', 'email', 'departmentName',
    'designationTitle', 'role', 'isActive', 'actions'
  ];

  dataSource = new MatTableDataSource<EmployeeResponse>([]);
  metrics: AdminMetricsResponse | null = null;
  departments: DepartmentResponse[] = [];
  designations: DesignationResponse[] = [];
  managers: EmployeeResponse[] = [];

  // Filters
  filterDepartment = '';
  filterDesignation = '';
  filterStatus = '';
  searchText = '';

  // Dialog
  employeeForm!: FormGroup;
  isEditing = false;
  editingId: number | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('employeeDialog') employeeDialog!: TemplateRef<any>;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadData();
  }

  initForm(): void {
    this.employeeForm = this.fb.group({
      employeeId: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      role: ['EMPLOYEE', Validators.required],
      departmentId: [null, Validators.required],
      designationId: [null, Validators.required],
      managerId: [null],
      joiningDate: [null],
      salary: [null]
    });
  }

  loadData(): void {
    this.adminService.getDashboardMetrics().subscribe(m => this.metrics = m);
    this.adminService.getDepartments().subscribe(d => this.departments = d.filter(x => x.isActive));
    this.adminService.getDesignations().subscribe(d => this.designations = d.filter(x => x.isActive));
    this.loadEmployees();
  }

  loadEmployees(): void {
    const dept = this.filterDepartment || undefined;
    const desig = this.filterDesignation || undefined;
    const active = this.filterStatus === '' ? undefined : this.filterStatus === 'true';

    this.adminService.getAllEmployees(dept, desig, active).subscribe({
      next: (data) => {
        this.dataSource = new MatTableDataSource(data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.dataSource.filterPredicate = (row: EmployeeResponse, filter: string) => {
          const search = filter.toLowerCase();
          return row.employeeId.toLowerCase().includes(search) ||
            row.firstName.toLowerCase().includes(search) ||
            row.lastName.toLowerCase().includes(search) ||
            row.email.toLowerCase().includes(search) ||
            (row.departmentName || '').toLowerCase().includes(search) ||
            (row.designationTitle || '').toLowerCase().includes(search);
        };
        if (this.searchText) {
          this.dataSource.filter = this.searchText.trim().toLowerCase();
        }
        this.managers = data.filter(e => e.role === 'MANAGER' || e.role === 'ADMIN');
      },
      error: () => this.snackBar.open('Failed to load employees', 'Close', { duration: 3000 })
    });
  }

  applySearch(): void {
    this.dataSource.filter = this.searchText.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  applyFilters(): void {
    this.loadEmployees();
  }

  clearFilters(): void {
    this.filterDepartment = '';
    this.filterDesignation = '';
    this.filterStatus = '';
    this.searchText = '';
    this.loadEmployees();
  }

  openAddDialog(): void {
    this.isEditing = false;
    this.editingId = null;
    this.employeeForm.reset({ role: 'EMPLOYEE' });
    this.employeeForm.get('employeeId')?.enable();
    this.employeeForm.get('email')?.enable();
    this.dialog.open(this.employeeDialog, { width: '560px', disableClose: true });
  }

  openEditDialog(emp: EmployeeResponse): void {
    this.isEditing = true;
    this.editingId = emp.id;
    this.employeeForm.patchValue({
      employeeId: emp.employeeId,
      firstName: emp.firstName,
      lastName: emp.lastName,
      email: emp.email,
      role: emp.role,
      departmentId: emp.departmentId,
      designationId: emp.designationId,
      managerId: emp.managerId,
      joiningDate: emp.joiningDate,
      salary: emp.salary
    });
    this.employeeForm.get('employeeId')?.disable();
    this.employeeForm.get('email')?.disable();
    this.dialog.open(this.employeeDialog, { width: '560px', disableClose: true });
  }

  saveEmployee(): void {
    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }

    const raw = this.employeeForm.getRawValue();
    const request: EmployeeRequest = {
      employeeId: raw.employeeId,
      firstName: raw.firstName,
      lastName: raw.lastName,
      email: raw.email,
      role: raw.role,
      departmentId: raw.departmentId,
      designationId: raw.designationId,
      managerId: raw.managerId || null,
      joiningDate: raw.joiningDate || null,
      salary: raw.salary || null
    };

    const obs = this.isEditing
      ? this.adminService.updateEmployee(this.editingId!, request)
      : this.adminService.addEmployee(request);

    obs.subscribe({
      next: () => {
        this.snackBar.open(
          this.isEditing ? 'Employee updated successfully' : 'Employee added successfully',
          'Close', { duration: 3000 }
        );
        this.dialog.closeAll();
        this.loadData();
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Operation failed', 'Close', { duration: 5000 });
      }
    });
  }

  toggleStatus(emp: EmployeeResponse): void {
    const obs = emp.isActive
      ? this.adminService.deactivateEmployee(emp.id)
      : this.adminService.reactivateEmployee(emp.id);

    obs.subscribe({
      next: () => {
        this.snackBar.open(
          emp.isActive ? 'Employee deactivated' : 'Employee reactivated',
          'Close', { duration: 3000 }
        );
        this.loadData();
      },
      error: () => this.snackBar.open('Failed to update status', 'Close', { duration: 3000 })
    });
  }
}
