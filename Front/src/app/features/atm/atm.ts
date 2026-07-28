import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TokenStorageService } from '../../core/services/token-storage.service';
import { AtmService } from '../../core/services/atm.service';
import { AccountService, AccountResponse } from '../../core/services/account.service';

@Component({
  selector: 'app-atm',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './atm.html'
})
export class Atm implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private tokenStorage = inject(TokenStorageService);
  private atmService = inject(AtmService);
  private accountService = inject(AccountService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  isLoggedIn = false;
  loginForm: FormGroup;
  transactionForm: FormGroup;
  accounts: AccountResponse[] = [];

  errorMessage = '';
  successMessage = '';
  
  view: 'LOGIN' | 'MENU' | 'DEPOSIT' | 'WITHDRAW' = 'LOGIN';

  constructor() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.transactionForm = this.fb.group({
      accountId: ['', Validators.required],
      amount: [0, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit() {
    if (!!this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.view = 'MENU';
      this.loadAccounts();
    }
  }

  onLogin() {
    if (this.loginForm.invalid) return;

    this.errorMessage = '';
    const { username, password } = this.loginForm.value;

    this.authService.login({ username, password }).subscribe({
      next: (res) => {
        this.tokenStorage.saveToken(res.token);
        this.isLoggedIn = true;
        this.view = 'MENU';
        this.loadAccounts();
      },
      error: (err) => {
        this.errorMessage = 'Giriş başarısız. Lütfen bilgilerinizi kontrol edin.';
        this.cdr.detectChanges();
      }
    });
  }

  loadAccounts() {
    this.accountService.getAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        if (accounts.length > 0) {
          this.transactionForm.patchValue({ accountId: accounts[0].id });
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Hesaplar yüklenemedi.';
        this.cdr.detectChanges();
      }
    });
  }

  showDeposit() {
    this.successMessage = '';
    this.errorMessage = '';
    this.transactionForm.patchValue({ amount: 100 });
    this.view = 'DEPOSIT';
  }

  showWithdraw() {
    this.successMessage = '';
    this.errorMessage = '';
    this.transactionForm.patchValue({ amount: 100 });
    this.view = 'WITHDRAW';
  }

  backToMenu() {
    this.successMessage = '';
    this.errorMessage = '';
    this.view = 'MENU';
  }

  onDeposit() {
    if (this.transactionForm.invalid) return;

    const { accountId, amount } = this.transactionForm.value;
    this.atmService.deposit(accountId, amount).subscribe({
      next: (res) => {
        this.successMessage = res.message + ' Yeni Bakiye: ' + res.newBalance;
        this.loadAccounts(); // Update balance
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'İşlem başarısız.';
        this.cdr.detectChanges();
      }
    });
  }

  onWithdraw() {
    if (this.transactionForm.invalid) return;

    const { accountId, amount } = this.transactionForm.value;
    this.atmService.withdraw(accountId, amount).subscribe({
      next: (res) => {
        this.successMessage = res.message + ' Yeni Bakiye: ' + res.newBalance;
        this.loadAccounts(); // Update balance
        this.cdr.detectChanges();
      },
      error: (err) => {
        if (err.error && err.error.error) {
          this.errorMessage = err.error.error;
        } else {
          this.errorMessage = 'İşlem başarısız.';
        }
        this.cdr.detectChanges();
      }
    });
  }

  logout() {
    this.authService.logout();
    this.tokenStorage.clearToken();
    this.isLoggedIn = false;
    this.view = 'LOGIN';
    this.loginForm.reset();
  }

  quit() {
    this.router.navigate(['/dashboard']);
  }
}
