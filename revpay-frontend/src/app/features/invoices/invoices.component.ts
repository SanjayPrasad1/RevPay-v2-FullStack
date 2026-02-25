import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormArray, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { InvoiceApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-invoices',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatFormFieldModule, MatInputModule, MatSnackBarModule, MatTableModule, MatTabsModule,
    MatIconModule, CurrencyPipe, DatePipe],
    templateUrl: './invoices.html',
    styleUrl: './invoices.css',
})
export class InvoicesComponent implements OnInit {
accountType: any;
pay(_t312: any) {
throw new Error('Method not implemented.');
}
  invoices: any[] = [];
  cols = ['number', 'customer', 'amount', 'status', 'due', 'actions'];
  invoiceForm: FormGroup;
  receivedInvoices: any[] = []

  constructor(
    private invoiceApi: InvoiceApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.invoiceForm = this.fb.group({
      customerName: ['', Validators.required],
      customerEmail: ['', Validators.required],
      customerAddress: [''],
      paymentTerms: [''],
      dueDate: [''],
      notes: [''],
      lineItems: this.fb.array([this.newLineItem()])
    });
  }

  get lineItems(): FormArray { return this.invoiceForm.get('lineItems') as FormArray; }

  newLineItem(): FormGroup {
    return this.fb.group({ description: ['', Validators.required], quantity: [1, Validators.required], unitPrice: [0, Validators.required], taxRate: [0] });
  }

  addLineItem(): void { this.lineItems.push(this.newLineItem()); }
  removeLineItem(i: number): void { if (this.lineItems.length > 1) this.lineItems.removeAt(i); }

  ngOnInit(): void { this.load(); 
    this.loadReceived();
  }

  load(): void {
    this.invoiceApi.getAll().subscribe(res => this.invoices = res.data?.content || []);
  }
  loadReceived(): void {
    this.invoiceApi.getReceived().subscribe(res => {
      this.receivedInvoices = res.data?.content || [];
    });
  }

  createInvoice(): void {
    if (this.invoiceForm.invalid) return;
    this.invoiceApi.create(this.invoiceForm.value).subscribe({
      next: () => { this.snackBar.open('Invoice created!', 'Close', { duration: 3000, panelClass: 'success-snack' }); this.load(); },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
    });
  }

  sendInvoice(id: number): void {
    this.invoiceApi.send(id).subscribe({ next: () => { this.snackBar.open('Invoice sent!', 'Close', { duration: 2000 }); this.load(); } });
  }

  markPaid(id: number): void {
    this.invoiceApi.markAsPaid(id).subscribe({ next: () => { this.snackBar.open('Marked as paid!', 'Close', { duration: 2000 }); this.load(); } });
  }

  logout(): void { this.authService.logout(); }
}
