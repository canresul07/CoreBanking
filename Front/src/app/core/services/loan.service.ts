import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoanRequestDTO {
  accountId: string;
  amount: number;
  interestRate: number;
}

export interface LoanResponseDTO {
  id: string;
  accountId: string;
  amount: number;
  interestRate: number;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private http = inject(HttpClient);
  private apiUrl = '/api/loans';

  applyForLoan(request: LoanRequestDTO): Observable<LoanResponseDTO> {
    return this.http.post<LoanResponseDTO>(`${this.apiUrl}/apply`, request);
  }
}
