import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, Role, UserProfile } from '../../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api';
  private currentUserSubject = new BehaviorSubject<UserProfile | null>(this.getStoredUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, payload).pipe(
      tap((response) => {
        const user: UserProfile = {
          username: response.username,
          firstName: response.firstName,
          lastName: response.lastName,
          role: response.role
        };
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): UserProfile | null {
    return this.currentUserSubject.value ?? this.getStoredUser();
  }

  hasRole(role: Role): boolean {
    return this.getCurrentUser()?.role === role;
  }

  private getStoredUser(): UserProfile | null {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) as UserProfile : null;
  }
}
