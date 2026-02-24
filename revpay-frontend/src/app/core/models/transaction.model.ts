export interface Transaction {
  id: number;
  transactionId: string;
  senderName?: string;
  senderUsername?: string;
  receiverName?: string;
  receiverUsername?: string;
  amount: number;
  type: 'SEND' | 'RECEIVE' | 'ADD_FUNDS' | 'WITHDRAWAL' | 'LOAN_DISBURSEMENT' | 'LOAN_REPAYMENT' | 'INVOICE_PAYMENT';
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  note?: string;
  description?: string;
  createdAt: string;
  completedAt?: string;
}

export interface SendMoneyRequest {
  recipientIdentifier: string;
  amount: number;
  note?: string;
  transactionPin?: string;
}

export interface WalletOperationRequest {
  amount: number;
  paymentMethodId?: number;
  transactionPin?: string;
}

export interface TransactionFilter {
  type?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
  page?: number;
  size?: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
