import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MoneyRequestApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-money-requests',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatTabsModule, MatFormFieldModule, MatInputModule, MatSnackBarModule, MatTableModule,
    CurrencyPipe, DatePipe],
    templateUrl: './money-request.html',
    styleUrl: './money-request.css',
})
export class MoneyRequestsComponent implements OnInit {
  incoming: any[] = [];
  outgoing: any[] = [];
  requestForm: FormGroup;

  constructor(
    private mrApi: MoneyRequestApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.requestForm = this.fb.group({
      recipientIdentifier: ['', Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      purpose: ['']
    });
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.mrApi.getIncoming().subscribe(res => this.incoming = res.data || []);
    this.mrApi.getOutgoing().subscribe(res => this.outgoing = res.data || []);
  }

  createRequest(): void {
    if (this.requestForm.invalid) return;
    this.mrApi.create(this.requestForm.value).subscribe({
      next: () => { this.snackBar.open('Request sent!', 'Close', { duration: 3000, panelClass: 'success-snack' }); this.requestForm.reset(); this.load(); },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
    });
  }

  accept(id: number): void {
    this.mrApi.accept(id).subscribe({ next: () => { this.snackBar.open('Request accepted!', 'Close', { duration: 2000, panelClass: 'success-snack' }); this.load(); }, error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' }) });
  }

  decline(id: number): void {
    this.mrApi.decline(id).subscribe({ next: () => this.load() });
  }

  cancel(id: number): void {
    this.mrApi.cancel(id).subscribe({ next: () => this.load() });
  }

  logout(): void { this.authService.logout(); }
}
