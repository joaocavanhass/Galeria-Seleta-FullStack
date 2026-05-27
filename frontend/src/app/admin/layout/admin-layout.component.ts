// ============================================================
// ARQUIVO: admin-layout.component.ts
// FUNÇÃO: Componente de layout compartilhado do painel administrativo.
//
// CONCEITO — Layout Shell:
// Este componente serve como "casca" para todas as páginas do admin.
// Ele exibe a sidebar (menu lateral) e o header do painel.
// O conteúdo de cada página (dashboard, produtos, etc.) é renderizado
// dentro do <router-outlet> deste componente.
//
// RASTREAMENTO DE ROTA ATIVA:
// O componente escuta o evento NavigationEnd do Router para saber
// qual rota está ativa e realçar o item correto no menu.
//
// filter(e => e instanceof NavigationEnd):
//   Filtra os eventos do router — só reage quando a navegação termina.
//   O Router emite vários eventos por navegação (start, end, etc.)
//
// INTERFACE NavItem:
// Define a estrutura de cada item do menu lateral.
// ============================================================

import { Component, signal } from '@angular/core';
// CommonModule: *ngIf, *ngFor, [ngClass]
import { CommonModule } from '@angular/common';
// RouterModule: <router-outlet> e routerLink
import { RouterModule, Router, NavigationEnd } from '@angular/router';
// filter: operador RxJS que filtra eventos do Observable
import { filter } from 'rxjs/operators';
// AdminAuthService: fornece dados do admin logado e método de logout
import { AdminAuthService } from '../services/admin-auth.service';

// Interface que define a estrutura de cada item do menu lateral
interface NavItem {
  label: string; // Texto exibido no menu (ex: "Dashboard")
  icon: string;  // Identificador do ícone (usado no template CSS/SVG)
  route: string; // Rota para navegar ao clicar (ex: "/admin/dashboard")
}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css']
})
export class AdminLayoutComponent {

  // Signals de estado da UI
  sidebarOpen  = signal(true);   // Sidebar expandida ou recolhida
  activeRoute  = signal('');     // URL da rota ativa (para realçar o item correto no menu)
  showUserMenu = signal(false);  // Dropdown do menu do usuário (foto + nome)

  // Itens do menu lateral
  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/admin/dashboard' },
    { label: 'Usuários',  icon: 'users',     route: '/admin/usuarios'  },
    { label: 'Produtos',  icon: 'products',  route: '/admin/produtos'  },
    { label: 'Pedidos',   icon: 'orders',    route: '/admin/pedidos'   },
  ];

  // Getter: retorna os dados do admin logado (UsuarioLogado ou null)
  get admin() {
    return this.auth.currentAdmin();
  }

  constructor(public auth: AdminAuthService, private router: Router) {
    // Escuta os eventos de navegação do Router
    this.router.events
      // filter: passa apenas os eventos de tipo NavigationEnd (navegação concluída)
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        // e.urlAfterRedirects: a URL final após qualquer redirecionamento
        this.activeRoute.set(e.urlAfterRedirects);
      });

    // Define a rota ativa inicial (caso o componente seja criado em uma rota específica)
    this.activeRoute.set(this.router.url);
  }

  // Retorna true se a rota fornecida é o início da rota ativa
  // .startsWith(): "/admin/produtos" começa com "/admin/produtos"
  // Isso realça o item correto mesmo em sub-rotas
  isActive(route: string): boolean {
    return this.activeRoute().startsWith(route);
  }

  // Delega o logout para o AdminAuthService (que limpa tokens e redireciona)
  logout(): void {
    this.auth.logout();
  }
}
