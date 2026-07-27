import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
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
  private cdr = inject(ChangeDetectorRef);
  cards: Card[] = [];
  flippedCards: Set<string> = new Set();
  isRequesting = false;
  successMessage: string | null = null;

  ngOnInit() {
    this.loadCards();
  }

  loadCards() {
    this.cardService.getMyCards().subscribe({
      next: (res) => {
        this.cards = res.filter(c => c.status === 'ACTIVE');
        this.cdr.detectChanges();
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
        this.isRequesting = false;
        this.successMessage = 'Sanal kart başvurunuz yönetici onayına gönderildi. Onaylandıktan sonra burada görünecektir.';
        this.loadCards();
        setTimeout(() => {
          this.successMessage = null;
          this.cdr.detectChanges();
        }, 5000);
      },
      error: (err) => {
        console.error(err);
        this.isRequesting = false;
        this.cdr.detectChanges();
      }
    });
  }

  formatCardNumber(num: string): string {
    return num.replace(/(.{4})/g, '$1 ').trim();
  }
}
