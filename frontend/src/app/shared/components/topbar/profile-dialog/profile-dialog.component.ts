import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService, User } from '../../../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-profile-dialog',
  template: `
    <div class="dialog-header">
      <h2 mat-dialog-title style="margin:0;">My Profile</h2>
      <button mat-icon-button (click)="toggleEdit()" *ngIf="!isEditing" title="Edit Profile">
        <mat-icon color="primary">edit</mat-icon>
      </button>
    </div>

    <mat-dialog-content class="profile-content">
      <div class="profile-header">
        <mat-icon class="large-avatar">account_circle</mat-icon>
        <h3>{{ user?.firstName }} {{ user?.lastName }}</h3>
        <span class="role-badge" [ngClass]="user?.role?.toLowerCase() || 'employee'">{{ user?.role }}</span>
      </div>
      
      <mat-divider></mat-divider>
      
      <!-- VIEW MODE -->
      <mat-list *ngIf="!isEditing">
        <mat-list-item>
          <mat-icon matListItemIcon color="primary">badge</mat-icon>
          <div matListItemTitle>Employee ID</div>
          <div matListItemLine>{{ user?.employeeId || user?.id }}</div>
        </mat-list-item>
        
        <mat-list-item>
          <mat-icon matListItemIcon color="primary">email</mat-icon>
          <div matListItemTitle>Email Address</div>
          <div matListItemLine>{{ user?.email }}</div>
        </mat-list-item>

        <mat-list-item>
          <mat-icon matListItemIcon color="primary">phone</mat-icon>
          <div matListItemTitle>Phone</div>
          <div matListItemLine>{{ user?.phone || 'Not provided' }}</div>
        </mat-list-item>

        <mat-list-item>
          <mat-icon matListItemIcon color="primary">home</mat-icon>
          <div matListItemTitle>Address</div>
          <div matListItemLine style="white-space: normal;">{{ user?.address || 'Not provided' }}</div>
        </mat-list-item>

        <mat-list-item>
          <mat-icon matListItemIcon color="warn">contact_emergency</mat-icon>
          <div matListItemTitle>Emergency Contact</div>
          <div matListItemLine>{{ user?.emergencyContact || 'Not provided' }}</div>
        </mat-list-item>
      </mat-list>

      <!-- EDIT MODE -->
      <form [formGroup]="editForm" *ngIf="isEditing" class="edit-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Phone Number</mat-label>
          <input matInput formControlName="phone" placeholder="+1234567890">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Home Address</mat-label>
          <textarea matInput formControlName="address" rows="2" placeholder="123 Street Name, City"></textarea>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Emergency Contact</mat-label>
          <input matInput formControlName="emergencyContact" placeholder="Name (Phone)">
        </mat-form-field>
      </form>

    </mat-dialog-content>
    
    <mat-dialog-actions align="end">
      <button mat-button *ngIf="isEditing" (click)="cancelEdit()">Cancel</button>
      <button mat-button mat-dialog-close *ngIf="!isEditing">Close</button>
      <button mat-raised-button color="primary" *ngIf="isEditing" (click)="saveProfile()" [disabled]="isLoading">
        {{ isLoading ? 'Saving...' : 'Save Changes' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .dialog-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-right: 15px;
    }
    .profile-content {
      min-width: 350px;
    }
    .profile-header {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 10px 0 20px;
      gap: 5px;
    }
    .large-avatar {
      font-size: 80px;
      height: 80px;
      width: 80px;
      color: #9e9e9e;
      margin-bottom: 10px;
    }
    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
    }
    .role-badge {
      font-size: 12px;
      padding: 4px 12px;
      border-radius: 12px;
      font-weight: 500;
      letter-spacing: 0.5px;
      color: white;
      text-transform: uppercase;
    }
    .role-badge.admin { background-color: #f44336; }
    .role-badge.manager { background-color: #ff9800; }
    .role-badge.employee { background-color: #2196f3; }
    
    .edit-form {
      display: flex;
      flex-direction: column;
      margin-top: 15px;
      gap: 10px;
    }
    .full-width {
      width: 100%;
    }
  `]
})
export class ProfileDialogComponent implements OnInit {
  user: User | null = null;
  isEditing = false;
  isLoading = false;
  editForm!: FormGroup;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(u => {
      this.user = u;
      if (!this.editForm) {
        this.initForm();
      }
    });
  }

  initForm(): void {
    this.editForm = this.fb.group({
      phone: [this.user?.phone || ''],
      address: [this.user?.address || ''],
      emergencyContact: [this.user?.emergencyContact || '']
    });
  }

  toggleEdit(): void {
    this.isEditing = true;
    this.initForm();
  }

  cancelEdit(): void {
    this.isEditing = false;
  }

  saveProfile(): void {
    if (this.editForm.invalid) return;

    this.isLoading = true;
    this.authService.updateProfile(this.editForm.value).subscribe({
      next: (updatedUser) => {
        this.isLoading = false;
        this.isEditing = false;
        this.snackBar.open('Profile updated successfully!', 'Close', { duration: 3000 });
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Failed to update profile.', 'Close', { duration: 3000 });
      }
    });
  }
}
