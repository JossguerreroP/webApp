import { Injectable } from '@angular/core';
import { TokenStorageService } from './token-storage.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/users/login';

  constructor(
    private tokenStorage: TokenStorageService,
    private http: HttpClient,
    private router: Router
  ) { }

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(this.apiUrl, credentials).pipe(
      tap(response => {
        if (response && response.token) {
          this.tokenStorage.saveAccessToken(response.token);
        }
      })
    );
  }

  async refreshToken(): Promise<void> {
    console.warn('Token refresh not implemented');
    throw new Error('Token refresh not implemented');
  }

  signOut(): void {
    console.log('[AUTH] Signing out...');
    this.tokenStorage.clearTokens();
    this.router.navigate(['/login']);
  }
}
