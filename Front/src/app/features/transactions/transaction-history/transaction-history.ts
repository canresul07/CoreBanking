import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../../core/services/account.service';
import { HistoryService, TransactionEvent } from '../../../core/services/history.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transaction-history.html',
  styleUrl: './transaction-history.css'
})
export class TransactionHistory implements OnInit {
  private accountService = inject(AccountService);
  private historyService = inject(HistoryService);
  private cdr = inject(ChangeDetectorRef);

  transactions: TransactionEvent[] = [];
  loading = true;

  ngOnInit() {
    this.loadHistory();
  }

  loadHistory() {
    this.loading = true;
    this.accountService.getAccounts().pipe(
      switchMap(accounts => {
        if (accounts.length === 0) {
          return of([]);
        }
        const requests = accounts.map(acc => this.historyService.getHistory(acc.id));
        return forkJoin(requests).pipe(
          map(results => {
            let allTxs: TransactionEvent[] = [];
            results.forEach(txs => allTxs.push(...txs));
            // Deduplicate by ID
            const unique = new Map<string, TransactionEvent>();
            allTxs.forEach(t => unique.set(t.id, t));
            return Array.from(unique.values()).sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
          })
        );
      }),
      catchError(err => {
        console.error(err);
        return of([]);
      })
    ).subscribe(txs => {
      this.transactions = txs;
      this.loading = false;
      this.cdr.detectChanges();
    });
  }
}
