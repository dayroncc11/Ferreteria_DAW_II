import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  correo = '';
  contrasena = '';
  error = signal('');
  showPass = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  iniciarSesion(): void {
    this.error.set('');
    this.authService.login(this.correo, this.contrasena).subscribe((errMsg: string | null) => {
      if (errMsg === null) {
        this.router.navigate(['/dashboard']);
      } else {
        this.error.set('ERROR: ' + errMsg);
      }
    });
  }
}
