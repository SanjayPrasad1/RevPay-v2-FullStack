import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { TransactionApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { SendMoneyDialogComponent } from './send-money-dialog.component';
import { AddFundsDialogComponent } from './add-funds-dialog.component';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatTableModule, MatPaginatorModule,
    MatSnackBarModule, MatProgressSpinnerModule, MatDialogModule, CurrencyPipe, DatePipe],
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent implements OnInit {
  transactions: any[] = [];
  loading = false;
  isBusiness = false;
  displayedColumns = ['id', 'type', 'party', 'amount', 'status', 'date'];
  filterForm: FormGroup;
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;

  constructor(
    private txApi: TransactionApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.isBusiness = authService.isBusinessUser;
    this.filterForm = this.fb.group({ type: [''], status: [''] });
  }

  ngOnInit(): void { this.loadTransactions(); }

  loadTransactions(): void {
    this.loading = true;
    const params: any = { page: this.currentPage, size: this.pageSize };
    const v = this.filterForm.value;
    if (v['type']) params['type'] = v['type'];
    if (v['status']) params['status'] = v['status'];
    this.txApi.getTransactions(params).subscribe({
      next: res => { this.transactions = res.data?.content || []; this.totalElements = res.data?.totalElements || 0; this.loading = false; },
      error: () => this.loading = false
    });
  }

  applyFilters(): void { this.currentPage = 0; this.loadTransactions(); }
  resetFilters(): void { this.filterForm.reset({ type: '', status: '' }); this.applyFilters(); }
  onPageChange(e: any): void { this.currentPage = e.pageIndex; this.pageSize = e.pageSize; this.loadTransactions(); }

  openSend(): void {
    this.dialog.open(SendMoneyDialogComponent, { width: '400px' }).afterClosed().subscribe(result => {
      if (result) this.loadTransactions();
    });
  }

  openAddFunds(): void {
    this.dialog.open(AddFundsDialogComponent, { width: '400px' }).afterClosed().subscribe(result => {
      if (result) this.loadTransactions();
    });
  }

  exportCsv(): void {
    this.txApi.exportCsv().subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url; a.download = 'transactions.csv'; a.click();
    });
  }

  logout(): void { this.authService.logout(); }
}
