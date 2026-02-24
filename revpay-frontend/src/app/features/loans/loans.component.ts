import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { LoanApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatFormFieldModule, MatInputModule, MatSnackBarModule, MatTabsModule, MatProgressBarModule,
    CurrencyPipe, DatePipe],
    templateUrl: './loans.html',
    styleUrl: './loans.css',
})
export class LoansComponent implements OnInit {
  loans: any[] = [];
  loanForm: FormGroup;

  constructor(
    private loanApi: LoanApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.loanForm = this.fb.group({
      requestedAmount: [null, [Validators.required, Validators.min(1000)]],
      purpose: ['', Validators.required],
      tenure: [12, [Validators.required, Validators.min(1), Validators.max(360)]],
      businessRevenue: [''],
      businessDescription: ['']
    });
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loanApi.getAll().subscribe(res => this.loans = res.data || []);
  }

  applyForLoan(): void {
    if (this.loanForm.invalid) return;
    this.loanApi.apply(this.loanForm.value).subscribe({
      next: () => { this.snackBar.open('Application submitted!', 'Close', { duration: 3000, panelClass: 'success-snack' }); this.loanForm.reset({ tenure: 12 }); this.load(); },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
    });
  }

  repay(loan: any): void {
    const amount = parseFloat(prompt(`EMI amount: ${loan.emi}. Enter repayment amount:`) || '0');
    if (amount > 0) {
      this.loanApi.repay(loan.id, amount).subscribe({
        next: () => { this.snackBar.open('Repayment successful!', 'Close', { duration: 3000, panelClass: 'success-snack' }); this.load(); },
        error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
      });
    }
  }

  getProgress(loan: any): number {
    if (!loan.approvedAmount) return 0;
    return Math.min(100, (loan.repaidAmount / (loan.approvedAmount * 1.12)) * 100);
  }

  logout(): void { this.authService.logout(); }
}
