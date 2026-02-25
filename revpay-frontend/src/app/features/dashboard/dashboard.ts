import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserApiService, TransactionApiService } from '../../core/services/api.service';
import { InvoiceApiService } from '../../core/services/api.service';
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatButtonModule, MatIconModule,
    MatDividerModule, MatProgressSpinnerModule, MatSnackBarModule, MatDialogModule,
    MatFormFieldModule, MatInputModule, ReactiveFormsModule, CurrencyPipe],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.css',
})
export class DashboardComponent implements OnInit {
  dashboard: any = null;
  user: any = null;
  loading = true;
  sendForm: FormGroup;
  isBusiness = false;
  receivedInvoices: any[] = []

  constructor(
    private userApi: UserApiService,
    private txApi: TransactionApiService,
    private invoiceApi: InvoiceApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.isBusiness = authService.isBusinessUser;
    this.sendForm = this.fb.group({
      recipientIdentifier: ['', Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      note: ['']
    });
  }

  ngOnInit(): void {
    this.userApi.getProfile().subscribe(res => { this.user = res.data; });
    this.userApi.getDashboard().subscribe({ next: res => { this.dashboard = res.data; this.loading = false; }, error: () => this.loading = false });
    this.invoiceApi.getReceived(0,5).subscribe(res =>{
      this.receivedInvoices = res.data.content;
    })
  }

  logout(): void { this.authService.logout(); }

  openSendMoney(): void {
    this.snackBar.open('Use the Transactions page to send money', 'OK', { duration: 3000 });
  }

  openRequestMoney(): void {
    this.snackBar.open('Use the Money Requests page to request money', 'OK', { duration: 3000 });
  }

  openAddFunds(): void {
    this.snackBar.open('Use the Transactions page to add funds', 'OK', { duration: 3000 });
  }

  openWithdraw(): void {
    this.snackBar.open('Use the Transactions page to withdraw funds', 'OK', { duration: 3000 });
  }

  submitSend(): void {
    if (this.sendForm.invalid) return;
    this.txApi.sendMoney(this.sendForm.value).subscribe({
      next: () => {
        this.snackBar.open('Money sent!', 'Close', { duration: 3000, panelClass: 'success-snack' });
        this.dialog.closeAll();
        this.ngOnInit();
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
    });
  }
}
