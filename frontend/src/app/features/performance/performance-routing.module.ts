import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PerformanceMyComponent } from './performance-my/performance-my.component';
import { PerformanceApplyComponent } from './performance-apply/performance-apply.component';
import { PerformanceManagerComponent } from './performance-manager/performance-manager.component';

const routes: Routes = [
  { path: 'my', component: PerformanceMyComponent },
  { path: 'apply', component: PerformanceApplyComponent },
  { path: 'apply/:id', component: PerformanceApplyComponent }, // for draft editing
  { path: 'team', component: PerformanceManagerComponent }, // Manager view
  { path: '', redirectTo: 'my', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PerformanceRoutingModule { }
