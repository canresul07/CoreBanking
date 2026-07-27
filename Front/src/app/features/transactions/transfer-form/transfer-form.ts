import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-transfer-form',
  imports: [ReactiveFormsModule],
  templateUrl: './transfer-form.html',
  styleUrl: './transfer-form.css',
})
export class TransferForm {

  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/transfers`;

  public errorMessage = '';
  public successMessage = '';
  public isLoading = false;

  public transferForm = this.fb.group({
    fromAccountId: ['', Validators.required],
    toAccountId: ['', Validators.required],
    amount: ['', [Validators.required, Validators.min(0.01)]]
  });

  submitTransfer() {
    if (this.transferForm.invalid || this.isLoading) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Benzersiz bir Idempotency Key üretiyoruz (çift çekimleri önlemek için)
    const idempotencyKey = crypto.randomUUID();
    const payload = {
      ...this.transferForm.value,
      idempotencyKey
    };

    this.http.post(this.apiUrl, payload).pipe(
      finalize(() => this.isLoading = false) // İstek bitince butonu tekrar aktif yap
    ).subscribe({
      next: (res) => {
        this.successMessage = 'Transfer başarıyla gerçekleşti!';
        this.transferForm.reset();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Transfer sırasında bir hata oluştu.';
      }
    });
  }

}
