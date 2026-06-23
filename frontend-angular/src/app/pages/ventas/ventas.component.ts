import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UpperCasePipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { VentaService, ItemCarrito } from '../../services/venta.service';
import { ProductoService } from '../../services/producto.service';
import { ClienteService } from '../../services/cliente.service';
import { AuthService } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
import { Producto } from '../../models/producto.model';
import { Cliente } from '../../models/cliente.model';
import { Venta } from '../../models/venta.model';

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [FormsModule, UpperCasePipe],
  templateUrl: './ventas.component.html',
  styleUrl: './ventas.component.css'
})
export class VentasComponent implements OnInit {
  vista = signal<'pos' | 'historial'>('pos');
  productosDisponibles = signal<Producto[]>([]);
  clientes = signal<Cliente[]>([]);
  carrito = signal<ItemCarrito[]>([]);
  totalCarrito = signal(0);
  ventas = signal<Venta[]>([]);
  categorias = signal<string[]>([]);
  detalleVentaId = signal(0);
  detallesVenta = signal<any[]>([]);
  ventaExitosa = signal(false);

  busquedaProducto = '';
  clienteSeleccionado = 0;
  filtroCat = '';

  private todosProductos: Producto[] = [];
  private clientesMap = new Map<number, string>();
  private usuariosMap = new Map<number, string>();
  private productosMap = new Map<number, string>();

  constructor(
    private ventaService: VentaService,
    private productoService: ProductoService,
    private clienteService: ClienteService,
    private usuarioService: UsuarioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    forkJoin({
      productos: this.productoService.listarProductos(),
      categorias: this.productoService.obtenerCategorias(),
      clientes: this.clienteService.listarClientes(),
      usuarios: this.usuarioService.listar(),
      ventas: this.ventaService.listarVentas()
    }).subscribe(({ productos, categorias, clientes, usuarios, ventas }) => {
      this.todosProductos = productos;
      this.productosDisponibles.set(productos);
      this.categorias.set(categorias);

      this.clientesMap.clear();
      this.usuariosMap.clear();
      this.productosMap.clear();

      this.clientes.set(clientes);
      clientes.forEach(c => this.clientesMap.set(c.idCliente, c.nombre));
      productos.forEach(p => this.productosMap.set(p.idProducto, p.nombre));
      usuarios.forEach(u => this.usuariosMap.set(u.idUsuario, u.nombre));
      this.ventas.set([...ventas].reverse());
    });
  }

  buscarProductos(): void {
    let result = this.todosProductos;
    if (this.busquedaProducto) {
      const q = this.busquedaProducto.toLowerCase();
      result = result.filter(p => p.nombre.toLowerCase().includes(q));
    }
    if (this.filtroCat) {
      result = result.filter(p => p.categoria === this.filtroCat);
    }
    this.productosDisponibles.set(result);
  }

  agregarAlCarrito(p: Producto): void {
    const items = [...this.carrito()];
    const existing = items.find(i => i.idProducto === p.idProducto);
    if (existing) {
      if (existing.cantidad < p.stock) {
        existing.cantidad++;
        existing.subtotal = existing.cantidad * existing.precio;
      }
    } else {
      items.push({ idProducto: p.idProducto, nombre: p.nombre, precio: p.precio, cantidad: 1, subtotal: p.precio });
    }
    this.carrito.set(items);
    this.recalcularTotal();
  }

  cambiarCantidad(item: ItemCarrito, delta: number): void {
    const items = [...this.carrito()];
    const found = items.find(i => i.idProducto === item.idProducto);
    if (found) {
      const prod = this.todosProductos.find(p => p.idProducto === item.idProducto);
      const newQty = found.cantidad + delta;
      if (newQty >= 1 && newQty <= (prod?.stock || 999)) {
        found.cantidad = newQty;
        found.subtotal = found.cantidad * found.precio;
      }
    }
    this.carrito.set(items);
    this.recalcularTotal();
  }

  quitarDelCarrito(id: number): void {
    this.carrito.set(this.carrito().filter(i => i.idProducto !== id));
    this.recalcularTotal();
  }

  confirmarVenta(): void {
    const userId = this.authService.user()?.idUsuario || 1;
    this.ventaService.registrarVenta(this.clienteSeleccionado, userId, this.carrito()).subscribe(() => {
      this.carrito.set([]);
      this.totalCarrito.set(0);
      this.clienteSeleccionado = 0;
      this.ventaExitosa.set(true);
      setTimeout(() => this.ventaExitosa.set(false), 3000);
      this.cargarDatos();
    });
  }

  verDetalle(id: number): void {
    this.detalleVentaId.set(id);
    this.ventaService.obtenerDetallesPorVenta(id).subscribe(detalles => this.detallesVenta.set(detalles));
  }

  getClienteNombre(id: number): string { return this.clientesMap.get(id) || 'N/A'; }
  getUsuarioNombre(id: number): string { return this.usuariosMap.get(id) || 'N/A'; }
  getProductoNombre(id: number): string { return this.productosMap.get(id) || 'N/A'; }

  private recalcularTotal(): void {
    this.totalCarrito.set(this.carrito().reduce((s, i) => s + i.subtotal, 0));
  }
}
