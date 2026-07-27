import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardService, Card } from '../../core/services/card.service';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cards.html',
  styleUrl: './cards.css'
})
export class Cards implements OnInit {
  private cardService = inject(CardService);
  cards: Card[] = [];
  flippedCards: Set<string> = new Set();
  isRequesting = false;

  ngOnInit() {
    this.loadCards();
  }

  loadCards() {
    this.cardService.getMyCards().subscribe({
      next: (res) => {
        this.cards = res;
      },
      error: (err) => console.error(err)
    });
  }

  flipCard(id: string) {
    if (this.flippedCards.has(id)) {
      this.flippedCards.delete(id);
    } else {
      this.flippedCards.add(id);
    }
  }

  requestVirtualCard() {
    this.isRequesting = true;
    this.cardService.requestVirtualCard().subscribe({
      next: (res) => {
        alert(res.message || 'Kart başarıyla talep edildi');
        this.isRequesting = false;
        this.loadCards();
      },
      error: (err) => {
        console.error(err);
        this.isRequesting = false;
        alert('Kart talebi başarısız oldu.');
      }
    });
  }

  formatCardNumber(num: string): string {
    return num.replace(/(.{4})/g, '$1 ').trim();
  }
}
