import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import { SystemSettingsComponent } from './system-settings/system-settings.component';

const routes: Routes = [
  { path: 'employees', component: EmployeeListComponent },
  { path: 'settings', component: SystemSettingsComponent },
  { path: '', redirectTo: 'employees', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
