import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
    constructor(
        private router: Router,
        private authService: AuthService
    ) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const currentUser = this.authService.getCurrentUser();
        
        if (currentUser) {
            // Check if route is restricted by specific roles
            const expectedRoles = route.data['roles'];
            if (expectedRoles && expectedRoles.indexOf(currentUser.role) === -1) {
                // Role not authorized, redirect to unauthorized page or home
                this.router.navigate(['/unauthorized']);
                return false;
            }
            // User role is authorized
            return true;
        }

        // Fallback if not logged in
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
        return false;
    }
}
