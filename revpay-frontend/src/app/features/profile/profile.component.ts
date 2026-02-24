import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { UserApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FormsModule, MatCardModule, MatButtonModule,
    MatFormFieldModule, MatInputModule, MatSnackBarModule, MatTabsModule],
    templateUrl: './profile.html',
    styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit {
  profile: any = null;
  profileForm: FormGroup;
  passwordForm: FormGroup;
  newPin = '';

  constructor(
    private userApi: UserApiService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.profileForm = this.fb.group({ fullName: [''], phoneNumber: [''] });
    this.passwordForm = this.fb.group({ currentPassword: [''], newPassword: [''] });
  }

  ngOnInit(): void {
    this.userApi.getProfile().subscribe(res => {
      this.profile = res.data;
      this.profileForm.patchValue({ fullName: res.data.fullName, phoneNumber: res.data.phoneNumber });
    });
  }

  updateProfile(): void {
    this.userApi.updateProfile(this.profileForm.value).subscribe({
      next: () => this.snackBar.open('Profile updated!', 'Close', { duration: 2000, panelClass: 'success-snack' }),
      error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
    });
  }

  changePassword(): void {
    this.authService.changePassword(this.passwordForm.value).subscribe({
      next: () => { this.snackBar.open('Password changed!', 'Close', { duration: 2000, panelClass: 'success-snack' }); this.passwordForm.reset(); },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
    });
  }

  setPin(): void {
    this.authService.setPin(this.newPin).subscribe({
      next: () => { this.snackBar.open('PIN set!', 'Close', { duration: 2000, panelClass: 'success-snack' }); this.newPin = ''; },
      error: (err) => this.snackBar.open(err.error?.message || 'Failed', 'Close', { duration: 3000, panelClass: 'error-snack' })
    });
  }

  logout(): void { this.authService.logout(); }
}
