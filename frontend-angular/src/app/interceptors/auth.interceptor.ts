import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor JWT — agrega el token a cada petición HTTP saliente.
 *
 * Flujo:
 *   1. Lee el token JWT guardado en localStorage
 *   2. Si existe, clona la request y agrega el header Authorization
 *   3. Si no hay token (login page), deja pasar la request sin modificar
 *
 * Se registra en app.config.ts con provideHttpClient(withInterceptors([authInterceptor]))
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // Solo agregar el header si hay token disponible
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(authReq);
  }

  // Sin token — pasar la request sin modificar (ej: POST /api/auth/login)
  return next(req);
};
