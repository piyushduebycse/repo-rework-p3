import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

export interface User {
  id: number;
  employeeId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  phone?: string;
  address?: string;
  emergencyContact?: string;
  token?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private currentUserSubject: BehaviorSubject<User | null>;
    public currentUser: Observable<User | null>;

    constructor(private http: HttpClient, private router: Router) {
        const storedUser = localStorage.getItem('currentUser');
        this.currentUserSubject = new BehaviorSubject<User | null>(storedUser ? JSON.parse(storedUser) : null);
        this.currentUser = this.currentUserSubject.asObservable();
    }

    public get currentUserValue(): User | null {
        return this.currentUserSubject.value;
    }

    public getCurrentUser(): User | null {
        return this.currentUserValue;
    }

    public getToken(): string | null {
        return this.currentUserValue?.token || null;
    }

    login(identifier: string, password: string) {
        return this.http.post<any>(`${environment.apiUrl}/auth/login`, { identifier, password })
            .pipe(map(user => {
                // store user details and jwt token in local storage to keep user logged in between page refreshes
                localStorage.setItem('currentUser', JSON.stringify(user));
                this.currentUserSubject.next(user);
                return user;
            }));
    }

    updateProfile(data: { phone?: string, address?: string, emergencyContact?: string }): Observable<User> {
        return this.http.put<User>(`${environment.apiUrl}/auth/me`, data).pipe(map(updatedUser => {
            const current = this.currentUserValue;
            if (current) {
                const newUser = { ...current, ...updatedUser, token: current.token };
                localStorage.setItem('currentUser', JSON.stringify(newUser));
                this.currentUserSubject.next(newUser);
                return newUser;
            }
            return updatedUser;
        }));
    }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
    }
}
