export interface RegisterRequest {
  username: string;
  fullName: string;
  email: string;
  phoneNumber?: string;
  password: string;
  securityQuestion?: string;
  securityAnswer?: string;
  accountType: 'PERSONAL' | 'BUSINESS';
  businessName?: string;
  businessType?: string;
  taxId?: string;
  businessAddress?: string;
}

export interface LoginRequest {
  emailOrPhone: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  fullName: string;
  accountType: 'PERSONAL' | 'BUSINESS';
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}
