import { Component, OnInit } from '@angular/core';
import { AuthService, User } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  currentUser: User | null = null;
  
  menuItems: { label: string, icon: string, route: string, roles: string[] }[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard', roles: ['ADMIN', 'MANAGER', 'EMPLOYEE'] },
    { label: 'My Leaves', icon: 'event', route: '/leaves/my', roles: ['ADMIN', 'MANAGER', 'EMPLOYEE'] },
    { label: 'Team Leaves', icon: 'event_available', route: '/manager/leaves/team', roles: ['ADMIN', 'MANAGER'] },
    { label: 'My Performance', icon: 'assessment', route: '/performance/my', roles: ['ADMIN', 'MANAGER', 'EMPLOYEE'] },
    { label: 'Team Performance', icon: 'supervisor_account', route: '/manager/performance/team', roles: ['ADMIN', 'MANAGER'] },
    { label: 'Employee Management', icon: 'people', route: '/admin/employees', roles: ['ADMIN'] },
    { label: 'System Settings', icon: 'settings', route: '/admin/settings', roles: ['ADMIN'] },
  ];

  filteredMenu: any[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.filteredMenu = this.menuItems.filter(item => item.roles.includes(user.role));
      }
    });
  }
}
