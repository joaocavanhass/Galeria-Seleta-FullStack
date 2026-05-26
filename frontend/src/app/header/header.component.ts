import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CarrinhoService } from '../core/services/carrinho.service';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  termoBusca    = '';
  menuAberto    = signal(false);

  readonly carrinho = inject(CarrinhoService);
  readonly auth     = inject(AuthService);
  private  router   = inject(Router);

  get totalItens()   { return this.carrinho.totalItens(); }
  get usuarioLogado(){ return this.auth.currentUser(); }
  get primeiroNome() {
    return this.auth.currentUser()?.nome?.split(' ')[0] ?? '';
  }

  toggleMenu()  { this.menuAberto.update(v => !v); }
  fecharMenu()  { this.menuAberto.set(false); }

  logout() {
    this.auth.logout();
    this.fecharMenu();
    this.router.navigate(['/']);
  }

  irParaMeusPedidos() {
    this.fecharMenu();
    this.router.navigate(['/meus-pedidos']);
  }
}