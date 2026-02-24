import { Routes } from '@angular/router';
import { authGuard, businessGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'auth',
    canActivate: [guestGuard],
    children: [
      { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
      { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'transactions',
    canActivate: [authGuard],
    loadComponent: () => import('./features/transactions/transactions.component').then(m => m.TransactionsComponent)
  },
  {
    path: 'payment-methods',
    canActivate: [authGuard],
    loadComponent: () => import('./features/payment-methods/payment-methods.component').then(m => m.PaymentMethodsComponent)
  },
  {
    path: 'money-requests',
    canActivate: [authGuard],
    loadComponent: () => import('./features/money-requests/money-requests.component').then(m => m.MoneyRequestsComponent)
  },
  {
    path: 'notifications',
    canActivate: [authGuard],
    loadComponent: () => import('./features/notifications/notifications.component').then(m => m.NotificationsComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'invoices',
    canActivate: [authGuard, businessGuard],
    loadComponent: () => import('./features/invoices/invoices.component').then(m => m.InvoicesComponent)
  },
  {
    path: 'loans',
    canActivate: [authGuard, businessGuard],
    loadComponent: () => import('./features/loans/loans.component').then(m => m.LoansComponent)
  },
  { path: '**', redirectTo: '/dashboard' }
];
