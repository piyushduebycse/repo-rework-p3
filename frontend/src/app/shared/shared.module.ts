import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Material Modules
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Components
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { TopbarComponent } from './components/topbar/topbar.component';
import { NotificationBellComponent } from './components/notification-bell/notification-bell.component';
import { ProfileDialogComponent } from './components/topbar/profile-dialog/profile-dialog.component';

const materialModules = [
  MatToolbarModule,
  MatButtonModule,
  MatIconModule,
  MatSidenavModule,
  MatListModule,
  MatMenuModule,
  MatBadgeModule,
  MatDividerModule,
  MatCardModule,
  MatInputModule,
  MatFormFieldModule,
  MatSnackBarModule,
  MatDialogModule,
];

const sharedModules = [
  FormsModule,
  ReactiveFormsModule
];

@NgModule({
  declarations: [
    SidebarComponent,
    TopbarComponent,
    NotificationBellComponent,
    ProfileDialogComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    ...materialModules,
    ...sharedModules
  ],
  exports: [
    SidebarComponent,
    TopbarComponent,
    NotificationBellComponent,
    ...materialModules,
    ...sharedModules
  ]
})
export class SharedModule { }
