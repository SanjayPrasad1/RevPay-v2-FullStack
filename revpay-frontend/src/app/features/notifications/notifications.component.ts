import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NotificationApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatButtonModule, MatListModule,
    MatSnackBarModule, MatProgressSpinnerModule, DatePipe],
    templateUrl: './notifications.html',
    styleUrl: './notifications.css',
})
export class NotificationsComponent implements OnInit {
  notifications: any[] = [];
  loading = false;

  constructor(
    private notifApi: NotificationApiService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.notifApi.getAll().subscribe({ next: res => { this.notifications = res.data?.content || []; this.loading = false; }, error: () => this.loading = false });
  }

  markRead(n: any): void {
    if (!n.isRead) {
      this.notifApi.markAsRead(n.id).subscribe(() => n.isRead = true);
    }
  }

  markAllRead(): void {
    this.notifApi.markAllAsRead().subscribe(() => { this.notifications.forEach(n => n.isRead = true); });
  }

  getIcon(type: string): string {
    const icons: Record<string, string> = { TRANSACTION: 'swap_horiz', MONEY_REQUEST: 'request_page', CARD_CHANGE: 'credit_card', LOW_BALANCE: 'warning', INVOICE: 'receipt', LOAN: 'account_balance', GENERAL: 'notifications' };
    return icons[type] || 'notifications';
  }

  getColor(type: string): string {
    const colors: Record<string, string> = { TRANSACTION: '#3f51b5', MONEY_REQUEST: '#ff9800', CARD_CHANGE: '#9c27b0', LOW_BALANCE: '#f44336', INVOICE: '#2196f3', LOAN: '#4caf50', GENERAL: '#607d8b' };
    return colors[type] || '#607d8b';
  }

  logout(): void { this.authService.logout(); }
}
