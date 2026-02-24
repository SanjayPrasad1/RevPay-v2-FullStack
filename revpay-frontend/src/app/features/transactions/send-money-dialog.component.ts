import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TransactionApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-send-money-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatSnackBarModule],
  template: `
    <h2 mat-dialog-title>Send Money</h2>
    <mat-dialog-content>
      <form [formGroup]="form" id="sendForm">
        <mat-form-field class="full-width mb-16">
          <mat-label>Recipient (email, username, or phone)</mat-label>
          <input matInput formControlName="recipientIdentifier">
          <mat-error *ngIf="form.get('recipientIdentifier')?.hasError('required')">Required</mat-error>
        </mat-form-field>
        <mat-form-field class="full-width mb-16">
          <mat-label>Amount</mat-label>
          <input matInput type="number" formControlName="amount" min="0.01">
          <mat-error *ngIf="form.get('amount')?.hasError('required')">Required</mat-error>
          <mat-error *ngIf="form.get('amount')?.hasError('min')">Must be greater than 0</mat-error>
        </mat-form-field>
        <mat-form-field class="full-width mb-16">
          <mat-label>Note (optional)</mat-label>
          <input matInput formControlName="note">
        </mat-form-field>
        <mat-form-field class="full-width">
          <mat-label>Transaction PIN (if set)</mat-label>
          <input matInput type="password" formControlName="transactionPin" maxlength="6">
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" (click)="submit()" [disabled]="loading">
        {{ loading ? 'Sending...' : 'Send' }}
      </button>
    </mat-dialog-actions>
  `
})
export class SendMoneyDialogComponent {
  form: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private txApi: TransactionApiService,
    private dialogRef: MatDialogRef<SendMoneyDialogComponent>,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      recipientIdentifier: ['', Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      note: [''],
      transactionPin: ['']
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.txApi.sendMoney(this.form.value).subscribe({
      next: () => {
        this.snackBar.open('Money sent successfully!', 'Close', { duration: 3000, panelClass: 'success-snack' });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Failed to send money', 'Close', { duration: 3000, panelClass: 'error-snack' });
        this.loading = false;
      }
    });
  }
}
