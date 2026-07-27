import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface TransactionEvent {
  id: string;
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  status: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/history`;

  getHistory(accountId: string): Observable<TransactionEvent[]> {
    return this.http.get<TransactionEvent[]>(`${this.apiUrl}/${accountId}`);
  }
}
