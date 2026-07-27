import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, of } from 'rxjs';
import { TokenStorageService } from './token-storage.service';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private router = inject(Router);

  constructor(private http: HttpClient, private tokenStorage: TokenStorageService) { }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((res: any) => {
        if (res && res.accessToken) {
          this.tokenStorage.saveToken(res.accessToken);
        }
        // NOT: Refresh token Backend tarafından HttpOnly Cookie olarak gönderildiği için
        // Frontend tarafında localStorage'a kaydetmemize gerek yoktur.
        // Tarayıcı otomatik olarak bu cookie'yi saklar.
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  logout(): void {
    this.tokenStorage.clearToken();
    // Normalde backend'in "/logout" endpointine istek atılıp HttpOnly cookie silinmeli.
    // Şimdilik sadece frontend'i temizliyoruz.
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
