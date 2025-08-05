import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

export interface User {
  id: number;
  mobileNumber: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  isActive: boolean;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  token?: string;
  userId?: number;
  user?: User;
}

export interface LoginRequest {
  mobileNumber: string;
  verificationCode?: string;
}

export interface RegisterRequest {
  mobileNumber: string;
  firstName: string;
  lastName?: string;
  email?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8081/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private tokenSubject = new BehaviorSubject<string | null>(null);

  public currentUser$ = this.currentUserSubject.asObservable();
  public token$ = this.tokenSubject.asObservable();
  public isLoggedIn$ = this.currentUserSubject.pipe(map(user => !!user));

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Check for existing session
    this.loadStoredAuth();
  }

  private loadStoredAuth(): void {
    const token = localStorage.getItem('authToken');
    const userStr = localStorage.getItem('currentUser');
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        this.tokenSubject.next(token);
        this.currentUserSubject.next(user);
      } catch (error) {
        this.clearAuth();
      }
    }
  }

  login(mobileNumber: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, { mobileNumber })
      .pipe(
        map(response => {
          if (response.success && response.token && response.user) {
            this.setAuth(response.token, response.user);
          }
          return response;
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(error);
        })
      );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, request)
      .pipe(
        map(response => {
          if (response.success && response.token && response.user) {
            this.setAuth(response.token, response.user);
          }
          return response;
        }),
        catchError(error => {
          console.error('Registration error:', error);
          return throwError(error);
        })
      );
  }

  validateToken(): Observable<any> {
    const token = this.tokenSubject.value;
    if (!token) {
      return throwError('No token available');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post(`${this.API_URL}/validate`, {}, { headers })
      .pipe(
        catchError(error => {
          console.error('Token validation error:', error);
          this.logout();
          return throwError(error);
        })
      );
  }

  logout(): void {
    this.clearAuth();
    this.router.navigate(['/login']);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    return this.tokenSubject.value;
  }

  isAuthenticated(): boolean {
    return !!this.currentUserSubject.value && !!this.tokenSubject.value;
  }

  private setAuth(token: string, user: User): void {
    localStorage.setItem('authToken', token);
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.tokenSubject.next(token);
    this.currentUserSubject.next(user);
  }

  private clearAuth(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    this.tokenSubject.next(null);
    this.currentUserSubject.next(null);
  }

  // Mock verification for demo purposes
  verifyCode(mobileNumber: string, code: string): Observable<AuthResponse> {
    // In real implementation, this would call backend verification endpoint
    if (code === '123456') {
      const mockUser: User = {
        id: 1,
        mobileNumber: mobileNumber,
        firstName: 'Alex',
        lastName: 'Johnson',
        email: 'alex.johnson@example.com',
        isActive: true
      };
      
      const mockToken = 'mock-jwt-token-' + Date.now();
      this.setAuth(mockToken, mockUser);
      
      return new Observable(observer => {
        observer.next({
          success: true,
          message: 'Verification successful',
          token: mockToken,
          userId: mockUser.id,
          user: mockUser
        });
        observer.complete();
      });
    } else {
      return throwError({ message: 'Invalid verification code' });
    }
  }

  // Get authorization headers for API calls
  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }
} 