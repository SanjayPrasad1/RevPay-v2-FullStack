export interface UserProfile {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phoneNumber?: string;
  accountType: 'PERSONAL' | 'BUSINESS';
  walletBalance: number;
  verified: boolean;
  businessName?: string;
  businessType?: string;
  taxId?: string;
  businessAddress?: string;
  createdAt: string;
}

export interface DashboardData {
  walletBalance: number;
  recentTransactions: any[];
  pendingRequests: number;
  unreadNotifications: number;
  totalReceived?: number;
  totalSent?: number;
  pendingInvoicesAmount?: number;
  paidInvoicesAmount?: number;
  totalInvoices?: number;
  activeLoans?: number;
}

export interface UpdateProfileRequest {
  fullName?: string;
  phoneNumber?: string;
  businessName?: string;
  businessType?: string;
  businessAddress?: string;
}
