import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, Loan } from '../../core/services/admin.service';
import { Card } from '../../core/services/card.service';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css'
})
export class AdminPanel implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);
  
  pendingCards: Card[] = [];
  pendingLoans: Loan[] = [];
  
  activeTab: 'cards' | 'loans' = 'cards';

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.adminService.getPendingCards().subscribe({
      next: (res) => {
        this.pendingCards = res;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
    this.adminService.getPendingLoans().subscribe({
      next: (res) => {
        this.pendingLoans = res;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  approveCard(id: string) {
    this.adminService.approveCard(id).subscribe(() => this.loadData());
  }

  rejectCard(id: string) {
    this.adminService.rejectCard(id).subscribe(() => this.loadData());
  }

  approveLoan(id: string) {
    this.adminService.approveLoan(id).subscribe(() => this.loadData());
  }

  rejectLoan(id: string) {
    this.adminService.rejectLoan(id).subscribe(() => this.loadData());
  }
}
