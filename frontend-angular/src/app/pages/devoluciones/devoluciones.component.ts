import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { DevolucionService } from '../../services/devolucion.service';
import { VentaService } from '../../services/venta.service';
import { ClienteService } from '../../services/cliente.service';
import { Devolucion } from '../../models/devolucion.model';
import { Venta } from '../../models/venta.model';

@Component({
  selector: 'app-devoluciones',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './devoluciones.component.html',
  styleUrl: './devoluciones.component.css'
})
export class DevolucionesComponent implements OnInit {
  devoluciones = signal<Devolucion[]>([]);
  ventasDisponibles = signal<Venta[]>([]);
  modalAbierto = signal(false);
  exitoso = signal(false);
  ventaSeleccionada = 0;
  motivo = '';
  tipoMotivo = 'Defectuoso / No funcional';
  private ventasMap = new Map<number, Venta>();
  private clientesMap = new Map<number, string>();

  constructor(private devolucionService: DevolucionService, private ventaService: VentaService, private clienteService: ClienteService) {}
  ngOnInit(): void { this.cargar(); }
  cargar(): void {
    forkJoin({
      devoluciones: this.devolucionService.listarDevoluciones(),
      ventas: this.ventaService.listarVentas(),
      clientes: this.clienteService.listarClientes()
    }).subscribe(({ devoluciones, ventas, clientes }) => {
      this.ventasMap.clear();
      this.clientesMap.clear();
      this.devoluciones.set([...devoluciones].reverse());
      ventas.forEach(v => this.ventasMap.set(v.idVenta, v));
      clientes.forEach(c => this.clientesMap.set(c.idCliente, c.nombre));
      const ventasDevueltas = new Set(devoluciones.map(d => d.idVenta));
      this.ventasDisponibles.set(ventas.filter(v => !ventasDevueltas.has(v.idVenta)));
    });
  }
  getClienteDeVenta(idVenta: number): string { const v = this.ventasMap.get(idVenta); return v ? (this.clientesMap.get(v.idCliente) || 'N/A') : 'N/A'; }
  getMontoVenta(idVenta: number): number { return this.ventasMap.get(idVenta)?.total || 0; }
  abrirModal(): void { this.ventaSeleccionada = 0; this.motivo = ''; this.tipoMotivo = 'Defectuoso / No funcional'; this.modalAbierto.set(true); }
  cerrarModal(): void { this.modalAbierto.set(false); }
  guardar(): void {
    const motivoFull = `[${this.tipoMotivo}] ${this.motivo}`;
    this.devolucionService.registrarDevolucion(this.ventaSeleccionada, motivoFull).subscribe(() => {
      this.cerrarModal(); this.exitoso.set(true); setTimeout(() => this.exitoso.set(false), 3000); this.cargar();
    });
  }
}
