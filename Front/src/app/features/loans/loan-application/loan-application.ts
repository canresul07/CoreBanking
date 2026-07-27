import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoanService } from '../../../core/services/loan.service';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-loan-application',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './loan-application.html',
  styleUrl: './loan-application.css',
})
export class LoanApplication implements OnInit {
  private fb = inject(FormBuilder);
  private loanService = inject(LoanService);
  private accountService = inject(AccountService);

  loanForm: FormGroup;
  accountId: string | null = null;
  successMessage = '';
  errorMessage = '';

  constructor() {
    this.loanForm = this.fb.group({
      amount: [50000, [Validators.required, Validators.min(1000)]],
      term: ['12 Ay', Validators.required]
    });
  }

  ngOnInit() {
    this.accountService.getAccounts().subscribe({
      next: (accounts) => {
        if (accounts.length > 0) {
          this.accountId = accounts[0].id;
        }
      }
    });
  }

  get monthlyInstallment(): number {
    const amount = this.loanForm.get('amount')?.value || 0;
    const termStr = this.loanForm.get('term')?.value || '12 Ay';
    const months = parseInt(termStr, 10) || 12;
    const rate = 0.0399; // 3.99% monthly
    
    if (amount <= 0) return 0;
    
    // Simple PMT formula: P * r * (1 + r)^n / ((1 + r)^n - 1)
    const factor = Math.pow(1 + rate, months);
    const pmt = (amount * rate * factor) / (factor - 1);
    return pmt;
  }

  onSubmit() {
    if (this.loanForm.invalid || !this.accountId) {
      this.errorMessage = 'Lütfen geçerli bir tutar girin ve hesabınızın olduğundan emin olun.';
      return;
    }

    const amount = this.loanForm.get('amount')?.value;

    this.loanService.applyForLoan({
      accountId: this.accountId,
      amount: amount,
      interestRate: 3.99
    }).subscribe({
      next: (res) => {
        this.successMessage = 'Kredi başvurunuz başarıyla alındı. Onay bekliyor.';
        this.errorMessage = '';
        this.loanForm.reset({ amount: 50000, term: '12 Ay' });
      },
      error: (err) => {
        this.errorMessage = 'Başvuru sırasında bir hata oluştu.';
        this.successMessage = '';
        console.error(err);
      }
    });
  }
}
