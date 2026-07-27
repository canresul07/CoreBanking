import { RouterModule } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { finalize } from 'rxjs';
import { AccountService, AccountResponse } from '../../../core/services/account.service';

@Component({
  selector: 'app-transfer-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './transfer-form.html',
  styleUrl: './transfer-form.css',
})
export class TransferForm implements OnInit {

  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private accountService = inject(AccountService);
  private apiUrl = `${environment.apiUrl}/api/transfers`;

  public errorMessage = '';
  public successMessage = '';
  public isLoading = false;
  public myAccounts: AccountResponse[] = [];

  public transferForm = this.fb.group({
    fromAccountNumber: ['', Validators.required],
    toAccountNumber: ['', Validators.required],
    amount: ['', [Validators.required, Validators.min(0.01)]]
  });

  ngOnInit() {
    this.accountService.getAccounts().subscribe({
      next: (accounts) => {
        this.myAccounts = accounts;
        if (accounts.length > 0) {
          this.transferForm.patchValue({
            fromAccountNumber: accounts[0].accountNumber
          });
        }
      },
      error: (err) => console.error(err)
    });
  }

  submitTransfer() {
    if (this.transferForm.invalid || this.isLoading) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const idempotencyKey = crypto.randomUUID();
    const payload = this.transferForm.value;

    this.http.post(this.apiUrl, payload, {
      headers: {
        'Idempotency-Key': idempotencyKey
      }
    }).pipe(
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: (res) => {
        this.successMessage = 'Transfer başarıyla gerçekleşti!';
        this.transferForm.reset({ fromAccountNumber: this.myAccounts.length > 0 ? this.myAccounts[0].accountNumber : '' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Transfer sırasında bir hata oluştu.';
      }
    });
  }

}
