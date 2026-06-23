import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Devolucion } from '../models/devolucion.model';

@Injectable({ providedIn: 'root' })
export class DevolucionService {
  private readonly apiUrl = 'http://localhost:8080/api/devoluciones';

  constructor(private http: HttpClient) {}

  listarDevoluciones(): Observable<Devolucion[]> {
    return this.http.get<Devolucion[]>(this.apiUrl);
  }

  registrarDevolucion(idVenta: number, motivo: string): Observable<Devolucion> {
    return this.http.post<Devolucion>(this.apiUrl, { idVenta, motivo });
  }

  ventaTieneDevolucion(idVenta: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/venta/${idVenta}/existe`);
  }
}
