import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { ProductoService } from '../../../services/producto.service';
import { Producto } from '../../../models/producto.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FormsModule],
  template: `
    <header class="top-header">
      <div class="header-left">
        <span class="brand">FERRETERÍA MOLINA // CORE</span>
        <div class="global-search" [class.has-results]="resultados().length > 0">
          <span class="material-symbols-outlined search-icon">search</span>
          <input type="text" placeholder="Buscar producto por nombre o SKU..."
                 class="search-input"
                 [(ngModel)]="busqueda"
                 (input)="buscar()"
                 (focus)="buscar()"
                 (blur)="cerrarResultados()" />
          @if (resultados().length > 0) {
            <div class="search-results">
              @for (p of resultados(); track p.idProducto) {
                <div class="result-item" (mousedown)="irAProducto(p)">
                  <div class="result-info">
                    <span class="result-name">{{ p.nombre }}</span>
                    <span class="result-sku">SKU: PRD-{{ p.idProducto }} · {{ p.categoria }}</span>
                  </div>
                  <div class="result-right">
                    <span class="result-price">S/ {{ p.precio.toFixed(2) }}</span>
                    <span class="result-stock" [class.low]="p.stock <= 5">{{ p.stock }} uds</span>
                  </div>
                </div>
              }
            </div>
          }
        </div>
      </div>
      <div class="header-right">
        <button class="header-btn">
          <span class="material-symbols-outlined">settings</span>
        </button>
        <button class="header-btn">
          <span class="material-symbols-outlined">help_outline</span>
        </button>
        <button class="header-btn notif-btn">
          <span class="material-symbols-outlined">notifications</span>
          <span class="notif-dot"></span>
        </button>
        <div class="header-divider"></div>
        <div class="user-block">
          <div class="user-info-text">
            <span class="user-name">{{ authService.user()?.nombre || 'OPERADOR' }}</span>
            <span class="user-level">{{ authService.isAdmin() ? 'ADMINISTRADOR' : 'EMPLEADO' }}</span>
          </div>
          @if (authService.user()?.foto) {
            <img class="user-avatar-img" [src]="authService.user()?.foto" alt="Avatar" />
          } @else {
            <div class="user-avatar">{{ authService.user()?.nombre?.charAt(0) || 'U' }}</div>
          }
        </div>
      </div>
    </header>
  `,
  styles: [`
    .top-header {
      position: fixed; top: 0; left: 0; right: 0;
      height: 4rem; z-index: 50;
      background: #111316;
      border-bottom: 1px solid rgba(90, 65, 54, 0.15);
      display: flex; justify-content: space-between; align-items: center;
      padding: 0 1.5rem;
    }
    .header-left { display: flex; align-items: center; gap: 2rem; }
    .brand {
      font-family: 'Space Grotesk', sans-serif;
      font-size: 1.05rem; font-weight: 700;
      letter-spacing: -0.03em;
      color: #ffb693;
      text-transform: uppercase;
    }
    .global-search {
      display: flex; align-items: center; gap: 0.5rem;
      background: #1e2023; border: 1px solid rgba(90, 65, 54, 0.1);
      border-radius: 4px; padding: 0.4rem 0.75rem;
      position: relative;
      transition: border-color 0.2s;
    }
    .global-search:focus-within { border-color: rgba(255, 107, 0, 0.3); }
    .search-icon { font-size: 1rem; color: rgba(226, 226, 230, 0.4); }
    .search-input {
      background: transparent; border: none; outline: none;
      font-family: 'Space Grotesk', sans-serif;
      font-size: 0.7rem; letter-spacing: 0.05em;
      color: #e2e2e6; width: 280px;
    }
    .search-input::placeholder { color: rgba(226, 226, 230, 0.25); }

    /* Search Results Dropdown */
    .search-results {
      position: absolute; top: calc(100% + 4px);
      left: 0; right: 0; width: 420px;
      background: #1e2023; border: 1px solid rgba(90, 65, 54, 0.15);
      box-shadow: 0 12px 40px rgba(0,0,0,0.5);
      max-height: 320px; overflow-y: auto; z-index: 100;
    }
    .result-item {
      display: flex; justify-content: space-between; align-items: center;
      padding: 0.65rem 0.85rem; cursor: pointer;
      border-bottom: 1px solid rgba(90, 65, 54, 0.06);
      transition: background 0.15s;
    }
    .result-item:hover { background: #282a2d; }
    .result-info { display: flex; flex-direction: column; }
    .result-name { font-size: 0.75rem; font-weight: 600; color: #e2e2e6; }
    .result-sku { font-size: 0.5rem; color: rgba(226, 226, 230, 0.35); letter-spacing: 0.1em; margin-top: 2px; }
    .result-right { display: flex; flex-direction: column; align-items: flex-end; }
    .result-price { font-family: 'Space Grotesk'; font-size: 0.7rem; font-weight: 700; color: #ffb693; }
    .result-stock { font-size: 0.5rem; color: rgba(226, 226, 230, 0.4); }
    .result-stock.low { color: #ffb4ab; }

    .header-right { display: flex; align-items: center; gap: 0.25rem; }
    .header-btn {
      padding: 0.5rem; border: none; background: transparent;
      color: rgba(226, 226, 230, 0.5); cursor: pointer;
      transition: all 0.2s;
    }
    .header-btn:hover { color: #e2e2e6; background: #333538; }
    .notif-btn { position: relative; }
    .notif-dot {
      position: absolute; top: 8px; right: 8px;
      width: 6px; height: 6px;
      background: #ff6b00; border-radius: 50%;
      border: 1px solid #111316;
    }
    .header-divider {
      width: 1px; height: 24px;
      background: rgba(90, 65, 54, 0.2);
      margin: 0 0.75rem;
    }
    .user-block { display: flex; align-items: center; gap: 0.75rem; }
    .user-info-text { display: flex; flex-direction: column; align-items: flex-end; }
    .user-name {
      font-size: 0.7rem; font-weight: 700; color: #e2e2e6;
      font-family: 'Space Grotesk', sans-serif;
    }
    .user-level {
      font-size: 0.55rem; color: #ff6b00;
      text-transform: uppercase; letter-spacing: 0.05em;
    }
    .user-avatar {
      width: 32px; height: 32px; border-radius: 50%;
      background: #282a2d; border: 1px solid rgba(255, 182, 147, 0.3);
      display: flex; align-items: center; justify-content: center;
      color: #ffb693; font-weight: 700; font-size: 0.75rem;
      font-family: 'Space Grotesk', sans-serif;
    }
    .user-avatar-img {
      width: 32px; height: 32px; border-radius: 50%; object-fit: cover;
      border: 1px solid rgba(255, 182, 147, 0.3);
    }
  `]
})
export class HeaderComponent {
  busqueda = '';
  resultados = signal<Producto[]>([]);
  private timeoutId: any;

  constructor(
    public authService: AuthService,
    private productoService: ProductoService,
    private router: Router
  ) {}

  buscar(): void {
    if (!this.busqueda || this.busqueda.trim().length < 2) {
      this.resultados.set([]);
      return;
    }
    const q = this.busqueda.trim().toLowerCase();
    this.productoService.buscarPorNombre(this.busqueda.trim()).subscribe((productos) => {
      const found = productos.filter(p =>
        p.nombre.toLowerCase().includes(q) ||
        `prd-${p.idProducto}`.includes(q) ||
        p.categoria.toLowerCase().includes(q)
      ).slice(0, 8);
      this.resultados.set(found);
    });
  }

  irAProducto(p: Producto): void {
    this.busqueda = '';
    this.resultados.set([]);
    this.router.navigate(['/productos']);
  }

  cerrarResultados(): void {
    this.timeoutId = setTimeout(() => this.resultados.set([]), 200);
  }
}
