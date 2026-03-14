import { Component, OnInit, Input } from '@angular/core';
import { AuthService, User } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { MatDrawer } from '@angular/material/sidenav';
import { MatDialog } from '@angular/material/dialog';
import { ProfileDialogComponent } from './profile-dialog/profile-dialog.component';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss']
})
export class TopbarComponent implements OnInit {
  @Input() drawer!: MatDrawer;
  currentUser: User | null = null;

  constructor(
    private authService: AuthService, 
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => this.currentUser = user);
  }

  openProfile(): void {
    this.dialog.open(ProfileDialogComponent, {
      width: '400px',
      panelClass: 'custom-dialog-container'
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
