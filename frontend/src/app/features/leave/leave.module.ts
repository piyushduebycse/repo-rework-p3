import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';

import { LeaveRoutingModule } from './leave-routing.module';
import { LeaveListComponent } from './leave-list/leave-list.component';
import { LeaveApplyComponent } from './leave-apply/leave-apply.component';
import { LeaveManagerComponent } from './leave-manager/leave-manager.component';

import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatStepperModule } from '@angular/material/stepper';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';

@NgModule({
  declarations: [
    LeaveListComponent,
    LeaveApplyComponent,
    LeaveManagerComponent
  ],
  imports: [
    CommonModule,
    LeaveRoutingModule,
    ReactiveFormsModule,
    SharedModule,
    MatTableModule,
    MatPaginatorModule,
    MatStepperModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
    MatTabsModule
  ]
})
export class LeaveModule { }
