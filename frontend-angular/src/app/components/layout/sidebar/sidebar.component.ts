import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-icon">
          <span class="material-symbols-outlined">precision_manufacturing</span>
        </div>
        <div>
          <h2 class="brand-title">SEC-01 // INV</h2>
          <p class="brand-subtitle">Control de Inventario</p>
        </div>
      </div>

      <nav class="sidebar-nav">
        <a routerLink="/dashboard" routerLinkActive="active" class="nav-link">
          <span class="material-symbols-outlined">dashboard</span>
          <span class="nav-text">Dashboard</span>
        </a>
        <a routerLink="/productos" routerLinkActive="active" class="nav-link">
          <span class="material-symbols-outlined">inventory_2</span>
          <span class="nav-text">Productos</span>
        </a>
        <a routerLink="/proveedores" routerLinkActive="active" class="nav-link">
          <span class="material-symbols-outlined">local_shipping</span>
          <span class="nav-text">Proveedores</span>
        </a>
        <a routerLink="/clientes" routerLinkActive="active" class="nav-link">
          <span class="material-symbols-outlined">groups</span>
          <span class="nav-text">Clientes</span>
        </a>
        <a routerLink="/ventas" routerLinkActive="active" class="nav-link">
          <span class="material-symbols-outlined">point_of_sale</span>
          <span class="nav-text">Ventas</span>
        </a>
        <a routerLink="/devoluciones" routerLinkActive="active" class="nav-link">
          <span class="material-symbols-outlined">assignment_return</span>
          <span class="nav-text">Devoluciones</span>
        </a>
        @if (authService.isAdmin()) {
          <a routerLink="/usuarios" routerLinkActive="active" class="nav-link">
            <span class="material-symbols-outlined">shield_person</span>
            <span class="nav-text">Usuarios</span>
          </a>
        }
      </nav>

      <div class="sidebar-footer">
        <a href="#" class="footer-link">
          <span class="material-symbols-outlined">terminal</span>
          <span>Registros del Sistema</span>
        </a>
        <a href="#" class="footer-link">
          <span class="material-symbols-outlined">contact_support</span>
          <span>Soporte</span>
        </a>
        <button class="btn-logout" (click)="onLogout()">
          <span class="material-symbols-outlined">logout</span>
          CERRAR SESIÓN
        </button>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      position: fixed; left: 0; top: 0; bottom: 0;
      width: 256px; z-index: 45;
      background: #0c0e11;
      border-right: 1px solid rgba(90, 65, 54, 0.15);
      display: flex; flex-direction: column;
      padding-top: 5.5rem; padding-bottom: 1.5rem;
    }
    .sidebar-brand {
      display: flex; align-items: center; gap: 0.75rem;
      padding: 0 1.5rem; margin-bottom: 2rem;
    }
    .brand-icon {
      width: 40px; height: 40px;
      background: #ff6b00;
      display: flex; align-items: center; justify-content: center;
      border-radius: 2px;
    }
    .brand-icon .material-symbols-outlined {
      color: #572000; font-size: 1.25rem;
      font-variation-settings: 'FILL' 1;
    }
    .brand-title {
      font-family: 'Space Grotesk', sans-serif;
      font-size: 1.15rem; font-weight: 900;
      color: #ff6b00; line-height: 1; margin: 0;
    }
    .brand-subtitle {
      font-family: 'Space Grotesk', sans-serif;
      font-size: 0.55rem; text-transform: uppercase;
      letter-spacing: 0.2em;
      color: rgba(226, 226, 230, 0.35); margin: 3px 0 0;
    }
    .sidebar-nav {
      flex: 1; display: flex; flex-direction: column;
      gap: 1px; padding: 0 0.75rem;
    }
    .nav-link {
      display: flex; align-items: center; gap: 1rem;
      padding: 0.75rem;
      color: rgba(226, 226, 230, 0.45);
      text-decoration: none;
      font-family: 'Space Grotesk', sans-serif;
      font-size: 0.7rem; text-transform: uppercase;
      letter-spacing: 0.15em; font-weight: 500;
      transition: all 0.2s ease;
    }
    .nav-link:hover {
      background: #1a1c1f; color: #ffb693;
      transform: translateX(4px);
    }
    .nav-link .material-symbols-outlined {
      font-size: 1.15rem;
    }
    .nav-link.active {
      background: #1a1c1f; color: #ff6b00;
      border-left: 4px solid #ff6b00;
      font-weight: 700;
    }
    .sidebar-footer {
      padding: 1rem 0.75rem 0;
      border-top: 1px solid rgba(90, 65, 54, 0.1);
      display: flex; flex-direction: column; gap: 0.25rem;
    }
    .footer-link {
      display: flex; align-items: center; gap: 1rem;
      padding: 0.5rem 0.75rem;
      color: rgba(226, 226, 230, 0.25);
      text-decoration: none;
      font-family: 'Space Grotesk', sans-serif;
      font-size: 0.55rem; text-transform: uppercase;
      letter-spacing: 0.15em;
      transition: color 0.2s;
    }
    .footer-link .material-symbols-outlined { font-size: 0.9rem; }
    .footer-link:hover { color: #e2e2e6; }
    .btn-logout {
      display: flex; align-items: center; justify-content: center;
      gap: 0.5rem; margin-top: 0.75rem;
      padding: 0.75rem;
      background: rgba(255, 107, 0, 0.08);
      border: 1px solid rgba(255, 107, 0, 0.15);
      color: #ffb693; cursor: pointer;
      font-family: 'Space Grotesk', sans-serif;
      font-size: 0.6rem; font-weight: 700;
      text-transform: uppercase; letter-spacing: 0.15em;
      transition: all 0.2s;
    }
    .btn-logout:hover {
      background: rgba(255, 107, 0, 0.15);
      border-color: rgba(255, 107, 0, 0.3);
    }
    .btn-logout .material-symbols-outlined { font-size: 0.9rem; }
    @media (max-width: 768px) {
      .sidebar { width: 60px; padding-top: 5rem; }
      .sidebar-brand { padding: 0; justify-content: center; margin-bottom: 1.5rem; }
      .brand-title, .brand-subtitle, .nav-text, .footer-link span:not(.material-symbols-outlined) { display: none; }
      .nav-link { justify-content: center; padding: 0.75rem 0; }
      .nav-link .material-symbols-outlined { font-size: 1.3rem; margin: 0; }
      .footer-link { justify-content: center; padding: 0.75rem 0; }
      .footer-link .material-symbols-outlined { font-size: 1.2rem; }
      .btn-logout { font-size: 0; padding: 0.75rem 0; justify-content: center; color: transparent; }
      .btn-logout .material-symbols-outlined { margin: 0; color: #ffb693; }
    }
  `]
})
export class SidebarComponent {
  constructor(public authService: AuthService, private router: Router) {}

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
