import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LeaveListComponent } from './leave-list/leave-list.component';
import { LeaveApplyComponent } from './leave-apply/leave-apply.component';
import { LeaveManagerComponent } from './leave-manager/leave-manager.component';

const routes: Routes = [
  { path: 'my', component: LeaveListComponent },
  { path: 'apply', component: LeaveApplyComponent },
  { path: 'team', component: LeaveManagerComponent }, // Accessible by manager
  { path: '', redirectTo: 'my', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LeaveRoutingModule { }
