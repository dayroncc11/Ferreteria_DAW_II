import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.css'
})
export class ClientesComponent implements OnInit {
  clientes = signal<Cliente[]>([]);
  clientesFiltrados = signal<Cliente[]>([]);
  clientesActivos = signal(0);
  modalAbierto = signal(false);
  editandoFlag = signal(false);
  toast = signal('');
  toastType = signal<'success' | 'error' | 'warning'>('success');
  toastIcon = signal('check_circle');
  busqueda = '';
  filtroEstado = 'todos';
  submitted = false;
  form: any = { nombre: '', dni: '', direccion: '', telefono: '' };
  private editId = 0;

  constructor(private clienteService: ClienteService) {}
  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.clienteService.listarClientes().subscribe((all) => {
      this.clientes.set(all);
      this.clientesActivos.set(all.filter(c => c.activo).length);
      this.filtrar();
    });
  }

  filtrar(): void {
    let result = this.clientes();
    const q = this.busqueda.toLowerCase();
    if (q) { result = result.filter(c => c.nombre.toLowerCase().includes(q) || c.dni.includes(q)); }
    if (this.filtroEstado === 'activos') { result = result.filter(c => c.activo); }
    else if (this.filtroEstado === 'inactivos') { result = result.filter(c => !c.activo); }
    this.clientesFiltrados.set(result);
  }

  abrirModal(): void {
    this.editandoFlag.set(false);
    this.submitted = false;
    this.form = { nombre: '', dni: '', direccion: '', telefono: '' };
    this.modalAbierto.set(true);
  }

  editar(c: Cliente): void {
    this.editandoFlag.set(true);
    this.submitted = false;
    this.editId = c.idCliente;
    this.form = { nombre: c.nombre, dni: c.dni, direccion: c.direccion, telefono: c.telefono };
    this.modalAbierto.set(true);
  }

  cerrarModal(): void { this.modalAbierto.set(false); this.submitted = false; }

  guardar(): void {
    this.submitted = true;

    // Validate all fields
    if (!this.form.nombre.trim() || !this.form.dni.trim() || !this.form.direccion.trim() || !this.form.telefono.trim()) {
      return; // Don't save if any field is empty
    }
    if (this.form.dni.trim().length !== 8) {
      return; // DNI must be 8 digits
    }

    if (this.editandoFlag()) {
      this.clienteService.obtenerPorId(this.editId).subscribe((cliente) => {
        this.clienteService.actualizar({
          ...this.form,
          idCliente: this.editId,
          activo: cliente?.activo ?? true
        }).subscribe(() => {
          this.showToast('Cliente actualizado correctamente', 'success', 'check_circle');
          this.cerrarModal();
          this.cargar();
        });
      });
    } else {
      this.clienteService.registrarCliente(this.form).subscribe(() => {
        this.showToast('Cliente registrado correctamente', 'success', 'person_add');
        this.cerrarModal();
        this.cargar();
      });
    }
  }

  eliminar(id: number): void {
    if (confirm('¿Está seguro de ELIMINAR este cliente permanentemente? Esta acción no se puede deshacer.')) {
      this.clienteService.eliminar(id).subscribe(() => {
        this.showToast('Cliente eliminado permanentemente', 'error', 'delete');
        this.cargar();
      });
    }
  }

  toggleEstado(c: Cliente): void {
    if (c.activo) {
      this.clienteService.desactivar(c.idCliente).subscribe(() => {
        this.showToast(`Cliente "${c.nombre}" desactivado`, 'warning', 'person_off');
        this.cargar();
      });
    } else {
      this.clienteService.activar(c.idCliente).subscribe(() => {
        this.showToast(`Cliente "${c.nombre}" activado`, 'success', 'person');
        this.cargar();
      });
    }
  }

  private showToast(msg: string, type: 'success' | 'error' | 'warning', icon: string): void {
    this.toast.set(msg);
    this.toastType.set(type);
    this.toastIcon.set(icon);
    setTimeout(() => this.toast.set(''), 3000);
  }
}
