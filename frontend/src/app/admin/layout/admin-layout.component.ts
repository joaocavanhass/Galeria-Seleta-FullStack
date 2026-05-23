import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AdminAuthService } from '../services/admin-auth.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css']
})
export class AdminLayoutComponent {
  sidebarOpen = signal(true);
  activeRoute = signal('');
  showUserMenu = signal(false);

  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/admin/dashboard' },
    { label: 'Usuários',  icon: 'users',     route: '/admin/usuarios'  },
    { label: 'Produtos',  icon: 'products',  route: '/admin/produtos'  },
    { label: 'Pedidos',   icon: 'orders',    route: '/admin/pedidos'   },
  ];

  get admin() {
    return this.auth.currentAdmin();
  }

  constructor(public auth: AdminAuthService, private router: Router) {
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        this.activeRoute.set(e.urlAfterRedirects);
      });
    this.activeRoute.set(this.router.url);
  }

  isActive(route: string): boolean {
    return this.activeRoute().startsWith(route);
  }

  logout(): void {
    this.auth.logout();
  }
}