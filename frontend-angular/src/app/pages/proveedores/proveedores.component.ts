import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProveedorService } from '../../services/proveedor.service';
import { Proveedor } from '../../models/proveedor.model';

@Component({
  selector: 'app-proveedores',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './proveedores.component.html',
  styleUrl: './proveedores.component.css'
})
export class ProveedoresComponent implements OnInit {
  proveedores = signal<Proveedor[]>([]);
  modalAbierto = signal(false);
  editandoFlag = signal(false);
  form: any = { razonSocial: '', ruc: '', telefono: '' };
  private editId = 0;
  constructor(private proveedorService: ProveedorService) {}
  ngOnInit(): void { this.cargar(); }
  cargar(): void { this.proveedorService.listar().subscribe((proveedores) => this.proveedores.set(proveedores)); }
  abrirModal(): void { this.editandoFlag.set(false); this.form = { razonSocial: '', ruc: '', telefono: '' }; this.modalAbierto.set(true); }
  editar(p: Proveedor): void { this.editandoFlag.set(true); this.editId = p.idProveedor; this.form = { razonSocial: p.razonSocial, ruc: p.ruc, telefono: p.telefono }; this.modalAbierto.set(true); }
  cerrarModal(): void { this.modalAbierto.set(false); }
  guardar(): void {
    if (this.editandoFlag()) {
      this.proveedorService.actualizar({ ...this.form, idProveedor: this.editId }).subscribe(() => {
        this.cerrarModal();
        this.cargar();
      });
    } else {
      this.proveedorService.crear(this.form).subscribe(() => {
        this.cerrarModal();
        this.cargar();
      });
    }
  }
  eliminar(id: number): void { if (confirm('¿Eliminar proveedor?')) { this.proveedorService.eliminar(id).subscribe(() => this.cargar()); } }
}
