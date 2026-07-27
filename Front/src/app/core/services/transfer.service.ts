import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TransferRequestDTO {
  fromAccountNumber: string;
  toAccountNumber: string;
  amount: number;
}

export interface TransferResponseDTO {
  id: string;
  idempotencyKey: string;
  amount: number;
  status: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  private http = inject(HttpClient);
  private apiUrl = '/api/transfers';

  executeTransfer(request: TransferRequestDTO, idempotencyKey: string): Observable<TransferResponseDTO> {
    return this.http.post<TransferResponseDTO>(this.apiUrl, request, {
      headers: {
        'Idempotency-Key': idempotencyKey
      }
    });
  }
}
