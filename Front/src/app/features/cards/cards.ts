import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardService, Card } from '../../core/services/card.service';
import { ModalComponent } from '../../shared/components/modal/modal';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule, ModalComponent],
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

  isDeleteModalOpen = false;
  cardToDelete: string | null = null;

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

  confirmDelete(cardId: string) {
    this.cardToDelete = cardId;
    this.isDeleteModalOpen = true;
  }

  deleteVirtualCard() {
    if (this.cardToDelete) {
      this.cardService.deleteCard(this.cardToDelete).subscribe({
        next: () => {
          this.successMessage = 'Sanal kart başarıyla silindi.';
          this.isDeleteModalOpen = false;
          this.cardToDelete = null;
          this.loadCards();
          setTimeout(() => {
            this.successMessage = null;
            this.cdr.detectChanges();
          }, 3000);
        },
        error: (err) => {
          console.error(err);
          alert('Kart silinirken bir hata oluştu.');
          this.isDeleteModalOpen = false;
        }
      });
    }
  }

  formatCardNumber(num: string): string {
    return num.replace(/(.{4})/g, '$1 ').trim();
  }
}
