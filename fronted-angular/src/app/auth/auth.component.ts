import { Component } from '@angular/core';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import {FormsModule} from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {AuthService} from '../core/services/auth-service.service';
import { CommonModule } from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    FormsModule,
    FontAwesomeModule,
    CommonModule
  ],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent {
  username = '';
  password = '';
  isLoading = false;
  errorMessage: string | null = null;
  showResendVerification = false;

  // FontAwesome icons and UI state
  faEye = faEye;
  faEyeSlash = faEyeSlash;
  showPassword = false;

  constructor(private authService: AuthService, private router: Router) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = null;

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (response) => {
        this.isLoading = false;
        console.log('Login successful', response);
        this.router.navigate(['/incidents']);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Login failed. Please check your credentials.';
        console.error('Login error', error);
      }
    });
  }
}
