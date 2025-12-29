import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError, from } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth-service.service';
import { TokenStorageService } from '../services/token-storage.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  const tokenStorage = inject(TokenStorageService);

  // No agregar token a solicitudes de autenticación
  if (isAuthRequest(req.url)) {
    if (req.url.includes('/auth/refresh')) {
      // Log para depuración de refresh
      console.log('[INTERCEPTOR][REFRESH] URL:', req.url);
      const headersObj: Record<string, string | null> = {};
      req.headers.keys().forEach(key => { headersObj[key] = req.headers.get(key); });
      console.log('[INTERCEPTOR][REFRESH] Headers:', headersObj);
    }
    return next(req);
  }

  const accessToken = tokenStorage.getAccessToken();

  // Log detallado del token para debugging
  if (accessToken) {
    const logData = {
      length: accessToken.length,
      starts: accessToken.substring(0, 20) + '...',
      ends: '...' + accessToken.substring(accessToken.length - 20),
      hasInvalidChars: /[^A-Za-z0-9\-_.]/.test(accessToken),
      url: req.url.substring(req.url.lastIndexOf('/') + 1) // solo el endpoint
    };

    console.log('[INTERCEPTOR] Token encontrado:', logData);

    // Log específico para getGuests
    if (req.url.includes('api/users') && req.method === 'GET') {
      // Verificar caracteres problemáticos en el token
      const problematicChars = accessToken.match(/[^A-Za-z0-9\-_.]/g);
      const hasNullBytes = accessToken.includes('\0');
      const hasNewlines = accessToken.includes('\n') || accessToken.includes('\r');

      console.log('🔍 [INTERCEPTOR-GETGUESTS] Análisis detallado del token:', {
        fullToken: accessToken,
        tokenLength: accessToken.length,
        isValidJWT: /^[A-Za-z0-9\-_]+\.[A-Za-z0-9\-_]+\.[A-Za-z0-9\-_]+$/.test(accessToken),
        problematicChars: problematicChars,
        hasNullBytes: hasNullBytes,
        hasNewlines: hasNewlines,
        firstChar: accessToken.charCodeAt(0),
        lastChar: accessToken.charCodeAt(accessToken.length - 1),
        url: req.url
      });
    }
  } else {
    console.log('[INTERCEPTOR] No hay token disponible');
  }

  const authReq = accessToken
    ? req.clone({
        headers: req.headers.set('Authorization', `Bearer ${accessToken.trim()}`)
      })
    : req;

  // Log del header final con validaciones adicionales
  if (accessToken) {
    const authHeader = authReq.headers.get('Authorization');
    console.log('[INTERCEPTOR] Header Authorization final:', authHeader?.substring(0, 50) + '...');

    // Log específico para getGuests con validaciones de formato
    if (req.url.includes('payments/start') && req.method === 'GET') {
      const bearerPrefix = 'Bearer ';
      const hasCorrectPrefix = authHeader?.startsWith(bearerPrefix);
      const tokenPart = authHeader?.substring(bearerPrefix.length);

      console.log('🔍 [INTERCEPTOR-GETGUESTS] Validación del header Authorization:', {
        fullHeader: authHeader,
        hasCorrectPrefix: hasCorrectPrefix,
        headerLength: authHeader?.length,
        tokenPartLength: tokenPart?.length,
        expectedTokenLength: accessToken.length,
        tokenMatches: tokenPart === accessToken.trim(),
        hasExtraSpaces: authHeader?.includes('  ') // doble espacio
      });
    }
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Log específico para errores en getGuests
      if (req.url.includes('payments/start') && req.method === 'GET') {
        console.error('🚨 [INTERCEPTOR-GETGUESTS] Error en petición:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          errorBody: error.error,
          errorBodyString: JSON.stringify(error.error),
          url: error.url,
          headers: error.headers,
          requestHeaders: authReq.headers.keys().map(key => ({ [key]: authReq.headers.get(key) }))
        });
      }

      if (error.status === 401) {
        return handle401Error(authReq, next, authService, tokenStorage);
      }
      return throwError(() => error);
    })
  );
};

function isAuthRequest(url: string): boolean {
  const authPaths = [
    '/auth/register',
    '/auth/signin',
    '/auth/confirm-signup',
    '/auth/resend-confirmation',
    '/auth/refresh' // <-- excluir refresh
  ];
  return authPaths.some(path => url.includes(path));
}

function handle401Error(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  tokenStorage: TokenStorageService
): Observable<HttpEvent<unknown>> {
  const refreshToken = tokenStorage.getRefreshToken();

  if (!refreshToken) {
    authService.signOut();
    return throwError(() => new Error('Session expired'));
  }

  return from(authService.refreshToken()).pipe(
    switchMap(() => {
      const newToken = tokenStorage.getAccessToken();
      const newReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${newToken}`)
      });
      return next(newReq);
    }),
    catchError((error) => {
      authService.signOut();
      return throwError(() => error);
    })
  );
}
