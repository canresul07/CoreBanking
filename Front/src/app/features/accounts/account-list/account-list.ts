import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AccountService, AccountResponse } from '../../../core/services/account.service';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './account-list.html',
  styleUrl: './account-list.css',
})
export class AccountList implements OnInit {
  private accountService = inject(AccountService);
  private cdr = inject(ChangeDetectorRef);
  accounts: AccountResponse[] = [];
  loading = true;

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.loading = true;
    this.accountService.getAccounts().subscribe({
      next: (res) => {
        this.accounts = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load accounts', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  createAccount() {
    this.accountService.createAccount({ currency: 'TRY' }).subscribe({
      next: (res) => {
        this.loadAccounts();
      },
      error: (err) => {
        console.error('Failed to create account', err);
      }
    });
  }
}
