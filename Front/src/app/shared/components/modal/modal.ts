import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <!-- Backdrop -->
      <div class="fixed inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" (click)="close()"></div>
      
      <!-- Modal Panel -->
      <div class="relative bg-white rounded-2xl shadow-xl max-w-lg w-full p-6 overflow-hidden animate-in fade-in zoom-in duration-200">
        
        <!-- Header -->
        <div class="flex items-center justify-between mb-5">
          <h3 class="text-xl font-semibold text-slate-800">{{ title }}</h3>
          <button (click)="close()" class="p-2 rounded-full hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Body / Content Projection -->
        <div class="text-slate-600 mb-6 max-h-[60vh] overflow-y-auto">
          <ng-content></ng-content>
        </div>

        <!-- Footer Actions -->
        <div *ngIf="showActions" class="flex items-center justify-end gap-3 pt-2">
          <button (click)="onCancel()" class="px-4 py-2 rounded-xl text-slate-600 bg-slate-100 hover:bg-slate-200 font-medium transition-colors">
            {{ cancelText }}
          </button>
          <button (click)="onConfirm()" [ngClass]="confirmBtnClass" class="px-4 py-2 rounded-xl text-white font-medium transition-colors shadow-sm">
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  `
})
export class ModalComponent {
  @Input() isOpen = false;
  @Input() title = '';
  @Input() showActions = true;
  @Input() confirmText = 'Onayla';
  @Input() cancelText = 'İptal';
  
  // E.g., 'bg-red-500 hover:bg-red-600' or 'bg-emerald-500 hover:bg-emerald-600'
  @Input() confirmBtnClass = 'bg-emerald-500 hover:bg-emerald-600'; 

  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();
  @Output() closed = new EventEmitter<void>();

  close() {
    this.isOpen = false;
    this.closed.emit();
  }

  onCancel() {
    this.cancelled.emit();
    this.close();
  }

  onConfirm() {
    this.confirmed.emit();
  }
}
