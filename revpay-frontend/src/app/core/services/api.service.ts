import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserApiService {
  private apiUrl = `${environment.apiUrl}/users`;
  constructor(private http: HttpClient) {}

  getProfile(): Observable<any> { return this.http.get<any>(`${this.apiUrl}/profile`); }
  updateProfile(data: any): Observable<any> { return this.http.put<any>(`${this.apiUrl}/profile`, data); }
  getDashboard(): Observable<any> { return this.http.get<any>(`${this.apiUrl}/dashboard`); }
}

@Injectable({ providedIn: 'root' })
export class TransactionApiService {
  private apiUrl = `${environment.apiUrl}/transactions`;
  constructor(private http: HttpClient) {}

  sendMoney(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/send`, data); }
  addFunds(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/add-funds`, data); }
  withdraw(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/withdraw`, data); }
  getTransactions(params: any = {}): Observable<any> {
    let httpParams = new HttpParams();
    Object.keys(params).forEach(k => { if (params[k] !== undefined && params[k] !== null) httpParams = httpParams.set(k, params[k]); });
    return this.http.get<any>(this.apiUrl, { params: httpParams });
  }
  search(query: string): Observable<any> { return this.http.get<any>(`${this.apiUrl}/search`, { params: { query } }); }
  getTransaction(id: string): Observable<any> { return this.http.get<any>(`${this.apiUrl}/${id}`); }
  exportCsv(): Observable<Blob> { return this.http.get(`${this.apiUrl}/export/csv`, { responseType: 'blob' }); }
}

@Injectable({ providedIn: 'root' })
export class PaymentMethodApiService {
  private apiUrl = `${environment.apiUrl}/payment-methods`;
  constructor(private http: HttpClient) {}

  add(data: any): Observable<any> { return this.http.post<any>(this.apiUrl, data); }
  getAll(): Observable<any> { return this.http.get<any>(this.apiUrl); }
  setDefault(id: number): Observable<any> { return this.http.put<any>(`${this.apiUrl}/${id}/default`, {}); }
  delete(id: number): Observable<any> { return this.http.delete<any>(`${this.apiUrl}/${id}`); }
}

@Injectable({ providedIn: 'root' })
export class MoneyRequestApiService {
  private apiUrl = `${environment.apiUrl}/money-requests`;
  constructor(private http: HttpClient) {}

  create(data: any): Observable<any> { return this.http.post<any>(this.apiUrl, data); }
  getIncoming(): Observable<any> { return this.http.get<any>(`${this.apiUrl}/incoming`); }
  getOutgoing(): Observable<any> { return this.http.get<any>(`${this.apiUrl}/outgoing`); }
  accept(id: number, pin?: string): Observable<any> {
    let params = new HttpParams();
    if (pin) params = params.set('pin', pin);
    return this.http.post<any>(`${this.apiUrl}/${id}/accept`, null, { params });
  }
  decline(id: number): Observable<any> { return this.http.post<any>(`${this.apiUrl}/${id}/decline`, {}); }
  cancel(id: number): Observable<any> { return this.http.post<any>(`${this.apiUrl}/${id}/cancel`, {}); }
}

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  private apiUrl = `${environment.apiUrl}/notifications`;
  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 20): Observable<any> { return this.http.get<any>(this.apiUrl, { params: { page, size } }); }
  getUnreadCount(): Observable<any> { return this.http.get<any>(`${this.apiUrl}/unread-count`); }
  markAsRead(id: number): Observable<any> { return this.http.put<any>(`${this.apiUrl}/${id}/read`, {}); }
  markAllAsRead(): Observable<any> { return this.http.put<any>(`${this.apiUrl}/mark-all-read`, {}); }
  getPreferences(): Observable<any> { return this.http.get<any>(`${this.apiUrl}/preferences`); }
  updatePreference(type: string, enabled: boolean): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/preferences/${type}`, null, { params: { enabled } });
  }
}

@Injectable({ providedIn: 'root' })
export class InvoiceApiService {
  private apiUrl = `${environment.apiUrl}/invoices`;
  constructor(private http: HttpClient) {}

  create(data: any): Observable<any> { return this.http.post<any>(this.apiUrl, data); }
  getAll(page = 0, size = 20): Observable<any> { return this.http.get<any>(this.apiUrl, { params: { page, size } }); }
  getById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/${id}`); }
  updateStatus(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/status`, null, { params: { status } });
  }
  markAsPaid(id: number): Observable<any> { return this.http.post<any>(`${this.apiUrl}/${id}/mark-paid`, {}); }
  send(id: number): Observable<any> { return this.http.post<any>(`${this.apiUrl}/${id}/send`, {}); }
  getReceived(page=0, size=20): Observable<any>{
    return this.http.get<any>(`${this.apiUrl}/received`,{
      params: { page, size }
    });
  }
  payInvoice(id: number, paymentDetails?: any): Observable<any> {
  // If your backend uses the mark-paid endpoint for the actual transfer:
  return this.http.post<any>(`${this.apiUrl}/${id}/mark-paid`, paymentDetails || {});
}
}

@Injectable({ providedIn: 'root' })
export class LoanApiService {
  private apiUrl = `${environment.apiUrl}/loans`;
  constructor(private http: HttpClient) {}

  apply(data: any): Observable<any> { return this.http.post<any>(`${this.apiUrl}/apply`, data); }
  getAll(): Observable<any> { return this.http.get<any>(this.apiUrl); }
  getById(id: number): Observable<any> { return this.http.get<any>(`${this.apiUrl}/${id}`); }
  repay(id: number, amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/repay`, null, { params: { amount } });
  }
}
