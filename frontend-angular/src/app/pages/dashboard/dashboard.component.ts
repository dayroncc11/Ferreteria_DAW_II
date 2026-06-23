import { Component, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { ProductoService } from '../../services/producto.service';
import { VentaService } from '../../services/venta.service';
import { ClienteService } from '../../services/cliente.service';
import { DevolucionService } from '../../services/devolucion.service';
import { AuthService } from '../../services/auth.service';
import { Venta } from '../../models/venta.model';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  totalProductos = signal(0);
  totalClientes = signal(0);
  ventasHoy = signal(0);
  ingresoHoy = signal(0);
  productosStockBajo = signal<Producto[]>([]);
  ultimasVentas = signal<Venta[]>([]);
  private clientesMap = new Map<number, string>();

  constructor(
    private productoService: ProductoService,
    private ventaService: VentaService,
    private clienteService: ClienteService,
    private devolucionService: DevolucionService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    forkJoin({
      resumen: this.ventaService.obtenerResumenDashboard(),
      clientes: this.clienteService.listarClientes()
    }).subscribe(({ resumen, clientes }) => {
      this.clientesMap.clear();
      clientes.forEach(c => this.clientesMap.set(c.idCliente, c.nombre));
      this.totalProductos.set(resumen.totalProductos);
      this.totalClientes.set(resumen.totalClientes);
      this.ventasHoy.set(resumen.ventasHoy);
      this.ingresoHoy.set(resumen.ingresoHoy);
      this.productosStockBajo.set(resumen.productosStockBajo as Producto[]);
      this.ultimasVentas.set(resumen.ultimasVentas);
    });
  }

  getClienteNombre(id: number): string {
    return this.clientesMap.get(id) || 'N/A';
  }
}
