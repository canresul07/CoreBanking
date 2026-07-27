import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService, AccountResponse } from '../../../core/services/account.service';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './account-list.html',
  styleUrl: './account-list.css',
})
export class AccountList implements OnInit {
  private accountService = inject(AccountService);
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
      },
      error: (err) => {
        console.error('Failed to load accounts', err);
        this.loading = false;
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
