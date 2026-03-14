import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'leaves', loadChildren: () => import('./features/leave/leave.module').then(m => m.LeaveModule) },
      { path: 'manager/leaves', loadChildren: () => import('./features/leave/leave.module').then(m => m.LeaveModule), canActivate: [AuthGuard], data: { roles: ['MANAGER', 'ADMIN'] } },
      { path: 'performance', loadChildren: () => import('./features/performance/performance.module').then(m => m.PerformanceModule) },
      { path: 'manager/performance', loadChildren: () => import('./features/performance/performance.module').then(m => m.PerformanceModule), canActivate: [AuthGuard], data: { roles: ['MANAGER', 'ADMIN'] } },
      { path: 'admin', loadChildren: () => import('./features/admin/admin.module').then(m => m.AdminModule), canActivate: [AuthGuard], data: { roles: ['ADMIN'] } },
    ]
  },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
