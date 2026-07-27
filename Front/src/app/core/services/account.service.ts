import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface AccountResponse {
  id: string;
  accountNumber: string;
  currency: string;
  balance: number;
  createdAt: string;
}

export interface AccountCreateRequest {
  currency: string;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/accounts`;

  getAccounts(): Observable<AccountResponse[]> {
    return this.http.get<AccountResponse[]>(this.apiUrl);
  }

  createAccount(request: AccountCreateRequest): Observable<AccountResponse> {
    return this.http.post<AccountResponse>(this.apiUrl, request);
  }

  getBalance(accountId: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${accountId}/balance`);
  }
}
