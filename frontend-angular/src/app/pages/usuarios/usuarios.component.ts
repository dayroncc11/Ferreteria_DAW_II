import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css'
})
export class UsuariosComponent implements OnInit {
  usuarios = signal<Usuario[]>([]);
  vistaRegistro = signal(false);
  editandoFlag = signal(false);
  showPassword = signal(false);
  fotoPreview = signal<string | null>(null);
  guardando = signal(false);
  errorMessage = signal<string | null>(null);

  form: any = { nombre: '', correo: '', clave: '', rol: 'empleado', foto: '' };
  private editId = 0;

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.usuarioService.listar().subscribe({
      next: (usuarios) => this.usuarios.set(usuarios),
      error: (err) => this.errorMessage.set('Error al cargar usuarios: ' + (err.error?.message || err.message))
    });
  }

  abrirRegistro(): void {
    this.editandoFlag.set(false);
    this.errorMessage.set(null);
    this.form = { nombre: '', correo: '', clave: '', rol: 'empleado', foto: '' };
    this.fotoPreview.set(null);
    this.vistaRegistro.set(true);
  }

  editar(u: Usuario): void {
    this.editandoFlag.set(true);
    this.errorMessage.set(null);
    this.editId = u.idUsuario;
    this.form = { nombre: u.nombre, correo: u.correo, clave: '', rol: u.rol, foto: u.foto || '' };
    this.fotoPreview.set(u.foto || null);
    this.vistaRegistro.set(true);
  }

  cancelar(): void {
    this.vistaRegistro.set(false);
    this.errorMessage.set(null);
    this.fotoPreview.set(null);
    this.showPassword.set(false);
  }

  togglePassword(): void {
    this.showPassword.set(!this.showPassword());
  }

  onFotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.size > 2 * 1024 * 1024) {
        alert('La imagen no debe superar los 2MB');
        return;
      }
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = reader.result as string;
        this.fotoPreview.set(base64);
        this.form.foto = base64;
      };
      reader.readAsDataURL(file);
    }
  }

  removeFoto(): void {
    this.fotoPreview.set(null);
    this.form.foto = '';
  }

  guardar(): void {
    if (this.guardando()) return;
    this.guardando.set(true);
    this.errorMessage.set(null);

    const payload = {
      nombre: this.form.nombre,
      correo: this.form.correo,
      clave: this.form.clave,
      rol: this.form.rol,
      foto: this.form.foto || null
    };

    const handleSuccess = () => {
      this.guardando.set(false);
      this.cancelar();
      this.cargar();
    };

    const handleError = (err: any) => {
      this.guardando.set(false);
      if (err.error && err.error.details && Object.keys(err.error.details).length > 0) {
        this.errorMessage.set(Object.values(err.error.details).join(' | '));
      } else {
        this.errorMessage.set(err.error?.message || 'Error en la operación');
      }
    };

    if (this.editandoFlag()) {
      this.usuarioService.actualizar(this.editId, payload).subscribe({
        next: handleSuccess,
        error: handleError
      });
    } else {
      this.usuarioService.crear(payload).subscribe({
        next: handleSuccess,
        error: handleError
      });
    }
  }

  eliminar(id: number): void {
    if (confirm('¿Eliminar usuario?')) {
      this.usuarioService.eliminar(id).subscribe(() => this.cargar());
    }
  }
}
