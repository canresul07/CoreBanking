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
  physicalCards: Card[] = [];
  virtualCards: Card[] = [];
  flippedCards: Set<string> = new Set();
  isRequesting = false;
  successMessage: string | null = null;
  hasPendingCard = false;

  ngOnInit() {
    this.loadCards();
  }

  loadCards() {
    this.cardService.getMyCards().subscribe({
      next: (res) => {
        this.cards = res.filter(c => c.status === 'ACTIVE');
        this.physicalCards = this.cards.filter(c => c.cardType === 'PHYSICAL');
        this.virtualCards = this.cards.filter(c => c.cardType === 'VIRTUAL');
        this.hasPendingCard = res.some(c => c.status === 'PENDING' && c.cardType === 'VIRTUAL');
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
    if (this.hasPendingCard) {
      alert('Zaten onay bekleyen bir sanal kart başvurunuz bulunuyor.');
      return;
    }
    
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

  deleteVirtualCard(cardId: string) {
    if (confirm('Bu sanal kartı silmek istediğinize emin misiniz?')) {
      this.cardService.deleteCard(cardId).subscribe({
        next: () => {
          this.successMessage = 'Sanal kart başarıyla silindi.';
          this.loadCards();
          setTimeout(() => {
            this.successMessage = null;
            this.cdr.detectChanges();
          }, 3000);
        },
        error: (err) => {
          console.error(err);
          alert('Kart silinirken bir hata oluştu.');
        }
      });
    }
  }

  formatCardNumber(num: string): string {
    return num.replace(/(.{4})/g, '$1 ').trim();
  }
}
