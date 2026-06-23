import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Venta } from '../models/venta.model';
import { DetalleVenta } from '../models/detalle-venta.model';

export interface ItemCarrito {
  idProducto: number;
  nombre: string;
  precio: number;
  cantidad: number;
  subtotal: number;
}

@Injectable({ providedIn: 'root' })
export class VentaService {
  private readonly apiUrl = 'http://localhost:8080/api/ventas';

  constructor(private http: HttpClient) {}

  listarVentas(): Observable<Venta[]> {
    return this.http.get<Venta[]>(this.apiUrl);
  }

  listarDetalles(idVenta: number): Observable<DetalleVenta[]> {
    return this.http.get<DetalleVenta[]>(`${this.apiUrl}/${idVenta}/detalles`);
  }

  obtenerVentaPorId(id: number): Observable<Venta> {
    return this.http.get<Venta>(`${this.apiUrl}/${id}`);
  }

  obtenerDetallesPorVenta(idVenta: number): Observable<DetalleVenta[]> {
    return this.http.get<DetalleVenta[]>(`${this.apiUrl}/${idVenta}/detalles`);
  }

  registrarVenta(idCliente: number, idUsuario: number, items: ItemCarrito[]): Observable<Venta> {
    return this.http.post<Venta>(this.apiUrl, { idCliente, idUsuario, items });
  }

  obtenerVentasHoy(): Observable<Venta[]> {
    return this.http.get<Venta[]>(`${this.apiUrl}/hoy`);
  }

  obtenerTotalVentasHoy(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/hoy/total`);
  }

  obtenerResumenDashboard(): Observable<{
    totalProductos: number;
    totalClientes: number;
    ventasHoy: number;
    ingresoHoy: number;
    productosStockBajo: any[];
    ultimasVentas: Venta[];
  }> {
    return this.http.get<{
      totalProductos: number;
      totalClientes: number;
      ventasHoy: number;
      ingresoHoy: number;
      productosStockBajo: any[];
      ultimasVentas: Venta[];
    }>(`${this.apiUrl}/dashboard/resumen`);
  }
}
