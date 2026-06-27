import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './sidebar/sidebar.component';
import { HeaderComponent } from './header/header.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, HeaderComponent],
  template: `
    <app-header />
    <div class="layout-flex">
      <app-sidebar />
      <main class="main-content">
        <router-outlet />
      </main>
    </div>
    <!-- L-Bracket decorations -->
    <div class="corner-tr"></div>
    <div class="corner-bl"></div>
  `,
  styles: [`
    .layout-flex {
      display: flex;
      min-height: calc(100vh - 4rem);
      margin-top: 4rem;
    }
    .main-content {
      flex: 1;
      margin-left: 256px;
      padding: 2rem;
      overflow-y: auto;
    }
    .corner-tr {
      position: fixed; top: 5rem; right: 1rem;
      width: 16px; height: 16px;
      border-top: 2px solid rgba(255, 107, 0, 0.4);
      border-right: 2px solid rgba(255, 107, 0, 0.4);
      pointer-events: none; z-index: 60;
    }
    .corner-bl {
      position: fixed; bottom: 1rem; left: 272px;
      width: 16px; height: 16px;
      border-bottom: 2px solid rgba(255, 107, 0, 0.4);
      border-left: 2px solid rgba(255, 107, 0, 0.4);
      pointer-events: none; z-index: 60;
    }
    @media (max-width: 768px) {
      .main-content { margin-left: 60px; padding: 1rem; }
      .corner-bl { left: 76px; }
      .corner-tr { right: 0.5rem; top: 4.5rem; }
    }
  `]
})
export class LayoutComponent {}
