import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardData {
  username: string;
  role?: string;
  totalBalance?: number;
  recentTransactions?: any[];
  totalUsers?: number;
  pendingVirtualCards?: number;
  pendingLoans?: number;
  totalTransactions?: number;
  systemStats?: { [key: string]: number };
  accountStats?: { accountName: string, balance: number }[];
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/dashboard';

  getDashboardData(): Observable<DashboardData> {
    return this.http.get<DashboardData>(this.apiUrl);
  }
}
