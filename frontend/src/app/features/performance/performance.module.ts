import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';

import { PerformanceRoutingModule } from './performance-routing.module';
import { PerformanceMyComponent } from './performance-my/performance-my.component';
import { PerformanceApplyComponent } from './performance-apply/performance-apply.component';
import { PerformanceManagerComponent } from './performance-manager/performance-manager.component';
import { GoalDialogComponent } from './performance-my/goal-dialog/goal-dialog.component';

import { MatTableModule } from '@angular/material/table';
import { MatStepperModule } from '@angular/material/stepper';
import { MatSliderModule } from '@angular/material/slider';
import { MatExpansionModule } from '@angular/material/expansion';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

@NgModule({
  declarations: [
    PerformanceMyComponent,
    PerformanceApplyComponent,
    PerformanceManagerComponent,
    GoalDialogComponent
  ],
  imports: [
    CommonModule,
    PerformanceRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    SharedModule,
    MatTableModule,
    MatStepperModule,
    MatSliderModule,
    MatExpansionModule,
    MatSelectModule,
    DragDropModule,
    MatDialogModule,
    MatTabsModule,
    MatDatepickerModule,
    MatNativeDateModule
  ]
})
export class PerformanceModule { }
