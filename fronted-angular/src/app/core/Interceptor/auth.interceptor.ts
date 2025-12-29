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

  // Verificar si el token ya expiró antes de enviar la petición
  if (accessToken && tokenStorage.isTokenExpired()) {
    console.warn('⚠️ [INTERCEPTOR] El token ya ha expirado. Intentando refresh antes de la petición...');
    // Aquí podríamos intentar un refresh proactivo
  }

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
    // console.log('[INTERCEPTOR] Header Authorization final:', authHeader?.substring(0, 50) + '...');

    // Log específico para incidentes con validaciones de formato
    if (req.url.includes('api/incidents')) {
      const bearerPrefix = 'Bearer ';
      const hasCorrectPrefix = authHeader?.startsWith(bearerPrefix);
      const tokenPart = authHeader?.substring(bearerPrefix.length);

      console.log('🔍 [INTERCEPTOR-INCIDENTS] Validación del header Authorization:', {
        method: req.method,
        url: req.url,
        fullHeader: authHeader,
        hasCorrectPrefix: hasCorrectPrefix,
        headerLength: authHeader?.length,
        tokenPartLength: tokenPart?.length,
        expectedTokenLength: accessToken.length,
        tokenMatches: tokenPart === accessToken.trim(),
        hasExtraSpaces: authHeader?.includes('  '), // doble espacio
        contentType: req.headers.get('Content-Type'),
        bodyType: req.body instanceof FormData ? 'FormData' : typeof req.body
      });
    }
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Log específico para errores en incidentes
      if (req.url.includes('api/incidents')) {
        console.error('🚨 [INTERCEPTOR-INCIDENTS] Error en petición:', {
          method: req.method,
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
        // Solo cerrar sesión si realmente hay un error de refresh o si no hay token de refresh
        // Pero por ahora, vamos a registrar más info para ver por qué sucede
        console.warn('⚠️ [INTERCEPTOR] 401 Detectado. Intentando refresh o cerrando sesión...');
        return handle401Error(authReq, next, authService, tokenStorage);
      }

      // NO llamar a signOut si no es 401, a menos que sea un error específico que lo requiera

      // Global error handling for other statuses
      console.error(`[INTERCEPTOR] Global Error [${error.status}]:`, error.message);

      if (error.status >= 500) {
        // Log more info for 500 errors
        console.error('[INTERCEPTOR] 500 Server Error Details:', {
          url: error.url,
          error: error.error,
          message: error.message
        });
        console.error('[INTERCEPTOR] Server Error (5xx). Please contact support.');
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

  // Si no hay refresh token, verificamos si es una petición que debería haber tenido token
  // Si no tenía token y devolvió 401, es normal (aunque no debería pasar si el interceptor funciona)
  // Pero si tenía un token que ya no sirve, intentamos refresh o cerramos sesión.

  if (!refreshToken) {
    const hadToken = req.headers.has('Authorization');
    const isUpload = req.body instanceof FormData || req.url.includes('/attachment/');

    if (hadToken) {
      if (isUpload) {
         console.error('❌ [INTERCEPTOR] Error 401 en Upload. No cerraremos sesión automáticamente para permitir diagnóstico.');
         return throwError(() => new Error('Upload unauthorized (401)'));
      }
      console.error('❌ [INTERCEPTOR] Token expirado y no hay refresh token disponible. Cerrando sesión.');
      authService.signOut();
      return throwError(() => new Error('Session expired'));
    } else {
      // Si ni siquiera tenía token, redirigir a login sin limpiar nada extra
      console.warn('⚠️ [INTERCEPTOR] Petición sin token devolvió 401. Redirigiendo a login.');
      authService.signOut();
      return throwError(() => new Error('Unauthorized'));
    }
  }

  console.log('[INTERCEPTOR] Intentando refrescar token...');
  return from(authService.refreshToken()).pipe(
    switchMap(() => {
      const newToken = tokenStorage.getAccessToken();
      console.log('[INTERCEPTOR] Token refrescado. Reintentando petición...');
      const newReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${newToken}`)
      });
      return next(newReq);
    }),
    catchError((error) => {
      console.error('❌ [INTERCEPTOR] Error al refrescar token o en reintento. Cerrando sesión.', error);
      authService.signOut();
      return throwError(() => error);
    })
  );
}
