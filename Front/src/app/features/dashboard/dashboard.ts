import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService, DashboardData } from '../../core/services/dashboard.service';
import { ModalComponent } from '../../shared/components/modal/modal';

import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, ModalComponent, BaseChartDirective],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private dashboardService = inject(DashboardService);
  private cdr = inject(ChangeDetectorRef);
  
  data: DashboardData | null = null;
  loading = true;

  isTransactionModalOpen = false;
  selectedTransaction: any = null;

  // Chart configs
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: true, position: 'bottom' }
    }
  };
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: false }
    }
  };

  // User Chart Data
  public accountBalanceChartData: ChartData<'pie', number[], string | string[]> = {
    labels: [],
    datasets: [ { data: [], backgroundColor: ['#10b981', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6'] } ]
  };

  // Admin Chart Data
  public systemStatsChartData: ChartData<'bar', number[], string | string[]> = {
    labels: ['Kullanıcılar', 'Bekleyen Kartlar', 'Bekleyen Krediler'],
    datasets: [ { data: [], backgroundColor: ['#10b981', '#f59e0b', '#3b82f6'] } ]
  };

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.dashboardService.getDashboardData().subscribe({
      next: (res) => {
        this.data = res;
        this.loading = false;
        
        // Prepare User Charts
        if (this.data.accountStats) {
          this.accountBalanceChartData.labels = this.data.accountStats.map(s => s.accountName);
          this.accountBalanceChartData.datasets[0].data = this.data.accountStats.map(s => s.balance);
        }

        // Prepare Admin Charts
        if (this.data.systemStats) {
          this.systemStatsChartData.datasets[0].data = [
            this.data.systemStats['Kullanıcılar'] || 0,
            this.data.systemStats['Bekleyen Kartlar'] || 0,
            this.data.systemStats['Bekleyen Krediler'] || 0
          ];
        }

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Dashboard data load failed', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openTransactionDetails(transaction: any) {
    this.selectedTransaction = transaction;
    this.isTransactionModalOpen = true;
  }
}
