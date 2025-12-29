import { Injectable } from '@angular/core';
import { TokenStorageService } from './token-storage.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/users/login';

  constructor(private tokenStorage: TokenStorageService, private http: HttpClient) { }

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
    // Implement token refresh logic here
    console.log('Refreshing token...');
  }

  signOut(): void {
    this.tokenStorage.clearTokens();
    // Additional sign out logic (e.g., redirect to login)
  }
}
