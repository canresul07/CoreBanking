import { RouterModule } from '@angular/router';
import { Component, inject, signal, ChangeDetectorRef, HostListener, ElementRef, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/services/auth.service';
import { TokenStorageService } from './core/services/token-storage.service';
import { NotificationService, Notification } from './core/services/notification.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);
  private tokenStorage = inject(TokenStorageService);
  private cdr = inject(ChangeDetectorRef);
  private eRef = inject(ElementRef);
  private notificationService = inject(NotificationService);

  public showSidebar = signal(false);
  public isProfileDropdownOpen = false;
  public isNotificationOpen = false;
  public notifications: Notification[] = [];
  public unreadCount = 0;

  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    if(!this.eRef.nativeElement.contains(event.target)) {
      this.isProfileDropdownOpen = false;
      this.isNotificationOpen = false;
      this.cdr.detectChanges();
    }
  }

  ngOnInit() {
    this.authService.isLoggedIn$.subscribe(loggedIn => {
      if (loggedIn) {
        this.loadNotifications();
      } else {
        this.notifications = [];
        this.unreadCount = 0;
      }
      this.cdr.detectChanges();
    });
  }

  loadNotifications() {
    this.notificationService.getNotifications().subscribe({
      next: (res) => {
        this.notifications = res;
        this.unreadCount = this.notifications.filter(n => !n.read).length;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  markAsRead(id: string) {
    this.notificationService.markAsRead(id).subscribe(() => {
      this.loadNotifications();
    });
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead().subscribe(() => {
      this.loadNotifications();
    });
  }

  toggleProfileDropdown(event: Event) {
    event.stopPropagation();
    this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
    this.isNotificationOpen = false;
    this.cdr.detectChanges();
  }

  toggleNotification(event: Event) {
    event.stopPropagation();
    this.isNotificationOpen = !this.isNotificationOpen;
    this.isProfileDropdownOpen = false;
    this.cdr.detectChanges();
  }

  constructor() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const url = event.urlAfterRedirects || event.url;
      const isHiddenPage = url.includes('/login') || url.includes('/register') || url.includes('/atm');
      this.showSidebar.set(!isHiddenPage);
      this.cdr.detectChanges();
    });
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  getInitials(): string {
    const name = this.authService.getUsername();
    if (name && name.length >= 2) {
      return name.substring(0, 2).toUpperCase();
    }
    return 'KR';
  }

  logout() {
    this.isProfileDropdownOpen = false;
    this.cdr.detectChanges();
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
