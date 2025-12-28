import { Component } from '@angular/core';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import {FormsModule} from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    FormsModule,
    FontAwesomeModule
  ],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent {
  email = '';
  password = '';
  isLoading = false;
  errorMessage: string | null = null;
  showResendVerification = false;

  // FontAwesome icons and UI state
  faEye = faEye;
  faEyeSlash = faEyeSlash;
  showPassword = false;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}
