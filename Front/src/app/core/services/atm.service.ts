import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AtmService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/atm`;

  deposit(accountId: string, amount: number): Observable<{message: string, newBalance: number}> {
    return this.http.post<{message: string, newBalance: number}>(`${this.apiUrl}/deposit`, { accountId, amount });
  }

  withdraw(accountId: string, amount: number): Observable<{message: string, newBalance: number}> {
    return this.http.post<{message: string, newBalance: number}>(`${this.apiUrl}/withdraw`, { accountId, amount });
  }
}
