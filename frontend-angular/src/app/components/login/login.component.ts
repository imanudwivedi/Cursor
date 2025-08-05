import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  mobileForm: FormGroup;
  verificationForm: FormGroup;
  isLoading = false;
  showVerification = false;
  mobileNumber = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.mobileForm = this.fb.group({
      mobileNumber: ['', [Validators.required, Validators.pattern(/^\+?[1-9]\d{1,14}$/)]]
    });

    this.verificationForm = this.fb.group({
      verificationCode: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });
  }

  ngOnInit(): void {
    // Check if already logged in
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onMobileSubmit(): void {
    if (this.mobileForm.valid) {
      this.isLoading = true;
      this.mobileNumber = this.mobileForm.value.mobileNumber;
      
      // For demo, we'll just show verification step
      setTimeout(() => {
        this.isLoading = false;
        this.showVerification = true;
        this.showMessage('Verification code sent to your mobile number', 'success');
      }, 1500);
    }
  }

  onVerificationSubmit(): void {
    if (this.verificationForm.valid) {
      this.isLoading = true;
      const verificationCode = this.verificationForm.value.verificationCode;
      
      this.authService.verifyCode(this.mobileNumber, verificationCode).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.showMessage('Login successful!', 'success');
            this.router.navigate(['/dashboard']);
          } else {
            this.showMessage(response.message, 'error');
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.showMessage('Invalid verification code. Please try again.', 'error');
        }
      });
    }
  }

  onBackToMobile(): void {
    this.showVerification = false;
    this.verificationForm.reset();
  }

  private showMessage(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 4000,
      panelClass: type === 'success' ? 'success-snack' : 'error-snack'
    });
  }

  // Demo helper - auto-fill verification code
  fillDemoCode(): void {
    this.verificationForm.patchValue({ verificationCode: '123456' });
  }
} 