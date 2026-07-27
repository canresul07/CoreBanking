import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  private router = inject(Router);

  // TODO(Can): Implement getToken() method to return access token from memory or localStorage
  getToken(): string | null {
    if (typeof localStorage !== 'undefined') {
      return localStorage.getItem('token');
    }
    return null;
  }

  // TODO(Can): Implement saveToken() method to store access token
  saveToken(token: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('token', token);
      localStorage.setItem('isAuthenticated', 'true');
      
      // Basit bir şekilde JWT içerisinden rolü çözümlüyoruz (eğer varsa)
      const role = this.getRoleFromToken(token);
      localStorage.setItem('role', role);
    }
  }

  // TODO(Can): Implement clearToken() method to remove token on logout
  clearToken(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('isAuthenticated');
      localStorage.removeItem('role');
    }

    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    if (typeof localStorage !== 'undefined') {
      return localStorage.getItem('isAuthenticated') === 'true';
    }
    return false;
  }

  getRefreshToken(): string | null {
    if (typeof localStorage !== 'undefined') {
      return localStorage.getItem('refreshToken');
    }
    return null;
  }

  setRefreshToken(refreshToken: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('refreshToken', refreshToken);
    }
  }
  
  private getRoleFromToken(token: string): string {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      const parsed = JSON.parse(decoded);
      return parsed.role || 'user';
    } catch (e) {
      return 'user'; // Hata olursa varsayılan
    }
  }
}
