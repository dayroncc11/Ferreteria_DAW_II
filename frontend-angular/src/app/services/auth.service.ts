import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from '../../environments/environment';

/**
 * Respuesta del backend al hacer login.
 * Incluye el token JWT y los datos del usuario.
 */
interface LoginResponse {
  token: string;
  idUsuario: number;
  nombre: string;
  correo: string;
  rol: string;
  foto?: string;
}

/**
 * Datos del usuario que se guardan en el estado local de la app.
 */
interface UsuarioSesion {
  idUsuario: number;
  nombre: string;
  correo: string;
  rol: string;
  foto?: string;
}

/**
 * Servicio de autenticación con JWT.
 *
 * Responsabilidades:
 *   - Hacer POST /api/auth/login y recibir el token JWT
 *   - Guardar el token en localStorage (lo usa el interceptor)
 *   - Guardar los datos del usuario en localStorage y en el signal
 *   - Exponer signals reactivos: isLoggedIn, user, isAdmin
 *   - Limpiar todo al hacer logout
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  // Signal con los datos del usuario actual (null si no está autenticado)
  private currentUser = signal<UsuarioSesion | null>(null);

  // Signals computados — el componente los puede usar directamente
  readonly isLoggedIn = computed(() => this.currentUser() !== null);
  readonly user = computed(() => this.currentUser());
  readonly isAdmin = computed(() => this.currentUser()?.rol === 'admin');

  constructor(private http: HttpClient) {
    // Restaurar sesión desde localStorage al iniciar la app
    this.restoreSession();
  }

  /**
   * Envía las credenciales al backend.
   * Si son correctas, guarda el token y los datos del usuario.
   * @returns Observable<boolean> — true si login exitoso, false si falló
   */
  login(correo: string, contrasena: string): Observable<string | null> {
    return this.http.post<any>(`${this.apiUrl}/login`, { correo, contrasena }).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        // decodificar token y setear info
        const payload = JSON.parse(atob(res.token.split('.')[1]));
        const sesion = {
          idUsuario: res.idUsuario || 0,
          nombre: res.nombre || '',
          correo: res.correo || '',
          rol: res.rol || payload.rol || '',
          foto: res.foto
        };
        localStorage.setItem('currentUser', JSON.stringify(sesion));
        this.currentUser.set(sesion);
      }),
      map(() => null),
      catchError((err: any) => {
        console.error('Login Error:', err);
        return of(err.message + ' (Status: ' + err.status + ')');
      })
    );
  }

  /**
   * Limpia la sesión — elimina token y datos del usuario.
   */
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUser.set(null);
  }

  /**
   * Restaura la sesión desde localStorage al recargar la página.
   * Solo restaura los datos del usuario (el token lo maneja el interceptor).
   */
  private restoreSession(): void {
    const stored = localStorage.getItem('currentUser');
    const token = localStorage.getItem('token');

    if (stored && token) {
      this.currentUser.set(JSON.parse(stored));
    }
  }
}
