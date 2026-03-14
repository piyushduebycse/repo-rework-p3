import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EmployeeRequest {
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  departmentId: number;
  designationId: number;
  managerId?: number;
  joiningDate?: string;
  salary?: number;
}

export interface EmployeeResponse {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  emergencyContact: string;
  role: string;
  departmentId: number;
  departmentName: string;
  designationId: number;
  designationTitle: string;
  managerId: number;
  managerName: string;
  joiningDate: string;
  salary: number;
  isActive: boolean;
}

export interface AdminMetricsResponse {
  totalEmployees: number;
  activeEmployees: number;
  inactiveEmployees: number;
  pendingLeaves: number;
  submittedReviews: number;
}

export interface DepartmentResponse {
  id: number;
  name: string;
  isActive: boolean;
}

export interface DesignationResponse {
  id: number;
  title: string;
  isActive: boolean;
}

export interface LeaveTypeResponse {
  id: number;
  name: string;
  defaultQuota: number;
  isActive: boolean;
}

export interface HolidayResponse {
  id: number;
  name: string;
  date: string;
  year: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private http: HttpClient) {}

  // ── Employee Management ──

  addEmployee(request: EmployeeRequest): Observable<EmployeeResponse> {
    return this.http.post<EmployeeResponse>(`${environment.apiUrl}/admin/employees`, request);
  }

  updateEmployee(id: number, request: EmployeeRequest): Observable<EmployeeResponse> {
    return this.http.put<EmployeeResponse>(`${environment.apiUrl}/admin/employees/${id}`, request);
  }

  getAllEmployees(department?: string, designation?: string, isActive?: boolean): Observable<EmployeeResponse[]> {
    let params = new HttpParams();
    if (department) params = params.set('department', department);
    if (designation) params = params.set('designation', designation);
    if (isActive !== undefined) params = params.set('isActive', isActive.toString());

    return this.http.get<EmployeeResponse[]>(`${environment.apiUrl}/admin/employees`, { params });
  }

  deactivateEmployee(id: number): Observable<any> {
    return this.http.put(`${environment.apiUrl}/admin/employees/${id}/deactivate`, {});
  }

  reactivateEmployee(id: number): Observable<any> {
    return this.http.put(`${environment.apiUrl}/admin/employees/${id}/reactivate`, {});
  }

  getDashboardMetrics(): Observable<AdminMetricsResponse> {
    return this.http.get<AdminMetricsResponse>(`${environment.apiUrl}/admin/dashboard/metrics`);
  }

  // ── Department Settings ──

  getDepartments(): Observable<DepartmentResponse[]> {
    return this.http.get<DepartmentResponse[]>(`${environment.apiUrl}/admin/settings/departments`);
  }

  addDepartment(dept: { name: string }): Observable<DepartmentResponse> {
    return this.http.post<DepartmentResponse>(`${environment.apiUrl}/admin/settings/departments`, dept);
  }

  updateDepartment(id: number, dept: { name: string; isActive: boolean }): Observable<DepartmentResponse> {
    return this.http.put<DepartmentResponse>(`${environment.apiUrl}/admin/settings/departments/${id}`, dept);
  }

  // ── Designation Settings ──

  getDesignations(): Observable<DesignationResponse[]> {
    return this.http.get<DesignationResponse[]>(`${environment.apiUrl}/admin/settings/designations`);
  }

  addDesignation(desig: { title: string }): Observable<DesignationResponse> {
    return this.http.post<DesignationResponse>(`${environment.apiUrl}/admin/settings/designations`, desig);
  }

  updateDesignation(id: number, desig: { title: string; isActive: boolean }): Observable<DesignationResponse> {
    return this.http.put<DesignationResponse>(`${environment.apiUrl}/admin/settings/designations/${id}`, desig);
  }

  // ── Leave Type Settings ──

  getLeaveTypes(): Observable<LeaveTypeResponse[]> {
    return this.http.get<LeaveTypeResponse[]>(`${environment.apiUrl}/admin/settings/leave-types`);
  }

  addLeaveType(lt: { name: string; defaultQuota: number }): Observable<LeaveTypeResponse> {
    return this.http.post<LeaveTypeResponse>(`${environment.apiUrl}/admin/settings/leave-types`, lt);
  }

  updateLeaveType(id: number, lt: { name: string; defaultQuota: number; isActive: boolean }): Observable<LeaveTypeResponse> {
    return this.http.put<LeaveTypeResponse>(`${environment.apiUrl}/admin/settings/leave-types/${id}`, lt);
  }

  // ── Holiday Settings ──

  getHolidays(year?: number): Observable<HolidayResponse[]> {
    let params = new HttpParams();
    if (year) params = params.set('year', year.toString());
    return this.http.get<HolidayResponse[]>(`${environment.apiUrl}/admin/settings/holidays`, { params });
  }

  addHoliday(holiday: { name: string; date: string; year: number }): Observable<HolidayResponse> {
    return this.http.post<HolidayResponse>(`${environment.apiUrl}/admin/settings/holidays`, holiday);
  }

  deleteHoliday(id: number): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/admin/settings/holidays/${id}`);
  }
}
