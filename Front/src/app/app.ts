import { RouterModule } from '@angular/router';
import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/services/auth.service';
import { TokenStorageService } from './core/services/token-storage.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private router = inject(Router);
  private authService = inject(AuthService);
  private tokenStorage = inject(TokenStorageService);

  public showSidebar = signal(false);

  constructor() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      // Login ve register sayfalarında sidebar'ı gizle
      const isAuthPage = event.url.includes('/login') || event.url.includes('/register');
      this.showSidebar.set(!isAuthPage);
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
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
