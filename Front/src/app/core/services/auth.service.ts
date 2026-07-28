import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { TokenStorageService } from './token-storage.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private tokenStorage = inject(TokenStorageService);
  private router = inject(Router);
  
  private apiUrl = 'http://localhost:8080/api/auth';
  private loggedIn = new BehaviorSubject<boolean>(!!this.tokenStorage.getToken());
  
  isLoggedIn$ = this.loggedIn.asObservable();

  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch (e) {
      return null;
    }
  }

  getRole(): string | null {
    const token = this.tokenStorage.getToken();
    if (token) {
      const decoded: any = this.decodeToken(token);
      return decoded?.role || decoded?.roles || null;
    }
    return null;
  }

  isAdmin(): boolean {
    const role = this.getRole();
    return role === 'ADMIN' || role === 'ROLE_ADMIN';
  }

  getUsername(): string {
    const token = this.tokenStorage.getToken();
    if (token) {
      const decoded: any = this.decodeToken(token);
      return decoded?.sub || 'Kullanıcı';
    }
    return 'Kullanıcı';
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((res: any) => {
        if (res && res.accessToken) {
          this.tokenStorage.saveToken(res.accessToken);
          this.loggedIn.next(true);
        }
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  logout(): void {
    this.tokenStorage.clearToken();
    this.loggedIn.next(false);
  }

  refreshToken(): Observable<any> {
    // HttpOnly Cookie otomatik olarak tarayıcı tarafından gönderilmesi için
    // { withCredentials: true } opsiyonunu ekliyoruz.
    return this.http.post(`${this.apiUrl}/refresh`, {}, { withCredentials: true }).pipe(
      tap((res: any) => {
        if (res && res.accessToken) {
          this.tokenStorage.saveToken(res.accessToken);
        }
      })
    );
  }
}
