import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { PaymentMethodApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-payment-methods',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatSnackBarModule, MatDialogModule,
    MatListModule, MatCheckboxModule],
    templateUrl: './payments-method.html',
    styleUrl: './payment-methods.css',
})
export class PaymentMethodsComponent implements OnInit {
  paymentMethods: any[] = [];
  addForm: FormGroup;
  loading = false;

  constructor(
    private pmApi: PaymentMethodApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.addForm = this.fb.group({
      cardHolderName: ['', Validators.required],
      cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      expiryMonth: ['', Validators.required],
      expiryYear: ['', Validators.required],
      cvv: ['', Validators.required],
      cardType: ['CREDIT', Validators.required],
      billingAddress: [''],
      billingCountry: [''],
      isDefault: [false]
    });
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.pmApi.getAll().subscribe(res => this.paymentMethods = res.data || []);
  }

  addCard(): void {
    if (this.addForm.invalid) return;
    this.loading = true;
    this.pmApi.add(this.addForm.value).subscribe({
      next: () => {
        this.snackBar.open('Card added!', 'Close', { duration: 3000, panelClass: 'success-snack' });
        this.addForm.reset({ cardType: 'CREDIT', isDefault: false });
        this.load();
        this.loading = false;
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' });
        this.loading = false;
      }
    });
  }

  setDefault(id: number): void {
    this.pmApi.setDefault(id).subscribe({ next: () => this.load() });
  }

  delete(id: number): void {
    this.pmApi.delete(id).subscribe({ next: () => { this.load(); this.snackBar.open('Card removed', 'Close', { duration: 2000 }); } });
  }

  logout(): void { this.authService.logout(); }
}
