export interface Loan {
  id: number;
  loanNumber: string;
  requestedAmount: number;
  approvedAmount?: number;
  purpose: string;
  tenure: number;
  interestRate?: number;
  emi?: number;
  repaidAmount: number;
  outstandingAmount?: number;
  businessRevenue?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ACTIVE' | 'CLOSED';
  rejectionReason?: string;
  createdAt: string;
  approvedAt?: string;
}

export interface LoanApplicationRequest {
  requestedAmount: number;
  purpose: string;
  tenure: number;
  businessRevenue?: string;
  businessDescription?: string;
}
