import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Card {
  id: string;
  cardNumber: string;
  cardholderName: string;
  expirationDate: string;
  cvv: string;
  cardType: string;
  status: string;
  limitAmount: number;
}

@Injectable({
  providedIn: 'root'
})
export class CardService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/cards';

  getMyCards(): Observable<Card[]> {
    return this.http.get<Card[]>(`${this.apiUrl}/my`);
  }

  requestVirtualCard(): Observable<{message: string}> {
    return this.http.post<{message: string}>(`${this.apiUrl}/virtual`, {});
  }

  deleteCard(cardId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${cardId}`);
  }
}
