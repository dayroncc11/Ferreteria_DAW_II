import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UpperCasePipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { ProductoService } from '../../services/producto.service';
import { ProveedorService } from '../../services/proveedor.service';
import { Producto } from '../../models/producto.model';
import { Proveedor } from '../../models/proveedor.model';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [FormsModule, UpperCasePipe],
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.css'
})
export class ProductosComponent implements OnInit {
  productos = signal<Producto[]>([]);
  productosFiltrados = signal<Producto[]>([]);
  proveedores = signal<Proveedor[]>([]);
  categorias = signal<string[]>([]);
  modalAbierto = signal(false);
  editando = signal(false);

  busqueda = '';
  filtroCategoria = '';
  nuevaCategoria = '';
  form: any = { nombre: '', categoria: '', precio: 0, stock: 0, idProveedor: 0 };

  private editId = 0;
  private proveedoresMap = new Map<number, string>();

  constructor(
    private productoService: ProductoService,
    private proveedorService: ProveedorService
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    forkJoin({
      productos: this.productoService.listarProductos(),
      proveedores: this.proveedorService.listar(),
      categorias: this.productoService.obtenerCategorias()
    }).subscribe(({ productos, proveedores, categorias }) => {
      this.proveedoresMap.clear();
      proveedores.forEach(p => this.proveedoresMap.set(p.idProveedor, p.razonSocial));
      this.productos.set(productos);
      this.proveedores.set(proveedores);
      this.categorias.set(categorias);
      this.filtrar();
    });
  }

  filtrar(): void {
    let result = this.productos();
    if (this.busqueda) {
      const q = this.busqueda.toLowerCase();
      result = result.filter(p => p.nombre.toLowerCase().includes(q));
    }
    if (this.filtroCategoria) {
      result = result.filter(p => p.categoria === this.filtroCategoria);
    }
    this.productosFiltrados.set(result);
  }

  getProveedorNombre(id: number): string {
    return this.proveedoresMap.get(id) || 'N/A';
  }

  abrirModal(): void {
    this.editando.set(false);
    this.nuevaCategoria = '';
    this.form = { nombre: '', categoria: '', precio: 0, stock: 0, idProveedor: 0 };
    this.modalAbierto.set(true);
  }

  editar(p: Producto): void {
    this.editando.set(true);
    this.editId = p.idProducto;
    this.nuevaCategoria = '';
    this.form = { nombre: p.nombre, categoria: p.categoria, precio: p.precio, stock: p.stock, idProveedor: p.idProveedor };
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  guardar(): void {
    const data = { ...this.form };

    // If user selected "new category", use the typed value
    if (data.categoria === '__nueva__') {
      data.categoria = this.nuevaCategoria.trim();
      if (!data.categoria) return;
    }

    if (this.editando()) {
      this.productoService.actualizar({ ...data, idProducto: this.editId }).subscribe(() => {
        this.cerrarModal();
        this.cargar();
      });
    } else {
      this.productoService.crear(data).subscribe(() => {
        this.cerrarModal();
        this.cargar();
      });
    }
  }

  eliminar(id: number): void {
    if (confirm('¿Eliminar este producto?')) {
      this.productoService.eliminar(id).subscribe(() => this.cargar());
    }
  }
}
