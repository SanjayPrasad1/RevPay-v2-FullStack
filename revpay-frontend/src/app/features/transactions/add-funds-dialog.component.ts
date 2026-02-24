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
  selector: 'app-add-funds-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatButtonModule, MatSnackBarModule],
  template: `
    <h2 mat-dialog-title>Add Funds to Wallet</h2>
    <mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field class="full-width mb-16">
          <mat-label>Amount</mat-label>
          <input matInput type="number" formControlName="amount" min="0.01">
          <mat-error *ngIf="form.get('amount')?.hasError('required')">Required</mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" (click)="submit()" [disabled]="loading">
        {{ loading ? 'Processing...' : 'Add Funds' }}
      </button>
    </mat-dialog-actions>
  `
})
export class AddFundsDialogComponent {
  form: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private txApi: TransactionApiService,
    private dialogRef: MatDialogRef<AddFundsDialogComponent>,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({ amount: [null, [Validators.required, Validators.min(0.01)]] });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.txApi.addFunds(this.form.value).subscribe({
      next: () => {
        this.snackBar.open('Funds added!', 'Close', { duration: 3000, panelClass: 'success-snack' });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' });
        this.loading = false;
      }
    });
  }
}
