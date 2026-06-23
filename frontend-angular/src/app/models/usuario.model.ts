export interface Usuario {
  idUsuario: number;
  nombre: string;
  correo: string;
  contrasena: string;
  rol: 'admin' | 'empleado';
  foto?: string;
}
