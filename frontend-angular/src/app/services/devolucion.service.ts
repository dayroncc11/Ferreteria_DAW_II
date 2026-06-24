import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Devolucion } from '../models/devolucion.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DevolucionService {
  private readonly apiUrl = `${environment.apiUrl}/devoluciones`;

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
