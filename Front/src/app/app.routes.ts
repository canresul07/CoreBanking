import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Dashboard } from './features/dashboard/dashboard';
import { AccountList } from './features/accounts/account-list/account-list';
import { AccountDetail } from './features/accounts/account-detail/account-detail';
import { LoanApplication } from './features/loans/loan-application/loan-application';
import { LoanStatus } from './features/loans/loan-status/loan-status';
import { TransactionHistory } from './features/transactions/transaction-history/transaction-history';
import { TransferForm } from './features/transactions/transfer-form/transfer-form';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { 
    path: 'dashboard', 
    component: Dashboard, 
    canActivate: [authGuard] 
  },
  { 
    path: 'accounts', 
    component: AccountList, 
    canActivate: [authGuard] 
  },
  { 
    path: 'accounts/:id', 
    component: AccountDetail, 
    canActivate: [authGuard] 
  },
  { 
    path: 'loans/apply', 
    component: LoanApplication, 
    canActivate: [authGuard] 
  },
  { 
    path: 'loans/status', 
    component: LoanStatus, 
    canActivate: [authGuard] 
  },
  { 
    path: 'transactions/history', 
    component: TransactionHistory, 
    canActivate: [authGuard] 
  },
  { 
    path: 'transactions/transfer', 
    component: TransferForm, 
    canActivate: [authGuard] 
  },
  { 
    path: 'cards', 
    loadComponent: () => import('./features/cards/cards').then(m => m.Cards),
    canActivate: [authGuard] 
  },
  { 
    path: 'admin', 
    loadComponent: () => import('./features/admin-panel/admin-panel').then(m => m.AdminPanel),
    canActivate: [authGuard] 
  },
  { path: '**', redirectTo: 'login' }
];
