export interface InvoiceLineItem {
  id?: number;
  description: string;
  quantity: number;
  unitPrice: number;
  taxRate?: number;
  lineTotal?: number;
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  businessName?: string;
  customerName: string;
  customerEmail: string;
  customerAddress?: string;
  lineItems: InvoiceLineItem[];
  subtotal: number;
  taxAmount: number;
  totalAmount: number;
  paymentTerms?: string;
  dueDate?: string;
  notes?: string;
  status: 'DRAFT' | 'SENT' | 'PAID' | 'OVERDUE' | 'CANCELLED';
  createdAt: string;
  paidAt?: string;
}

export interface CreateInvoiceRequest {
  customerName: string;
  customerEmail: string;
  customerAddress?: string;
  lineItems: {description: string; quantity: number; unitPrice: number; taxRate?: number}[];
  paymentTerms?: string;
  dueDate?: string;
  notes?: string;
}
