import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
    constructor(
        private router: Router,
        private authService: AuthService
    ) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const currentUser = this.authService.getCurrentUser();
        if (currentUser) {
            // Check if route is restricted by role
            if (route.data['roles'] && route.data['roles'].indexOf(currentUser.role) === -1) {
                // Role not authorized, redirect to home page
                this.router.navigate(['/']);
                return false;
            }
            // Authorized
            return true;
        }

        // Not logged in, redirect to login page with the return url
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
        return false;
    }
}
