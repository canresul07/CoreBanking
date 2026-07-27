import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Card } from './card.service';

export interface Loan {
  id: string;
  amount: number;
  interestRate: number;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin';

  getPendingCards(): Observable<Card[]> {
    return this.http.get<Card[]>(`${this.apiUrl}/cards/pending`);
  }

  approveCard(id: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/cards/${id}/approve`, {});
  }

  rejectCard(id: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/cards/${id}/reject`, {});
  }

  getPendingLoans(): Observable<Loan[]> {
    return this.http.get<Loan[]>(`${this.apiUrl}/loans/pending`);
  }

  approveLoan(id: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/loans/${id}/approve`, {});
  }

  rejectLoan(id: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/loans/${id}/reject`, {});
  }
}
