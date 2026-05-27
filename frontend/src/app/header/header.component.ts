// ============================================================
// ARQUIVO: header.component.ts
// FUNÇÃO: Componente do cabeçalho da aplicação (barra de navegação).
//
// RESPONSABILIDADES:
// - Exibir o logo e o menu de navegação
// - Mostrar o contador de itens do carrinho
// - Exibir o nome do usuário logado (ou links de login/cadastro)
// - Gerenciar o menu hamburguer (mobile)
// - Fazer logout
//
// SIGNALS USADOS:
// menuAberto: controla se o menu mobile está aberto ou fechado.
//
// PROPRIEDADES COMPUTADAS (getters):
// totalItens: lê o Signal totalItens do CarrinhoService
// usuarioLogado: retorna o usuário atual do AuthService
// primeiroNome: pega só o primeiro nome para exibição compacta
//
// CONEXÕES: usa CarrinhoService e AuthService.
// ============================================================

// Component: decorador | inject: injeção funcional | signal: valor reativo
import { Component, inject, signal } from '@angular/core';
// CommonModule: diretivas como *ngIf, *ngFor
import { CommonModule } from '@angular/common';
// FormsModule: two-way binding com [(ngModel)]
import { FormsModule } from '@angular/forms';
// RouterLink: links de navegação | RouterLinkActive: adiciona classe CSS na rota ativa
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
// Services
import { CarrinhoService } from '../core/services/carrinho.service';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-header',   // Tag <app-header> no app.component.html
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  termoBusca = ''; // Texto digitado no campo de busca

  // Signal: controla se o menu mobile (hamburguer) está aberto
  menuAberto = signal(false);

  // inject(): forma moderna de obter serviços sem construtor
  readonly carrinho = inject(CarrinhoService); // Acesso ao carrinho
  readonly auth     = inject(AuthService);     // Acesso à autenticação
  private  router   = inject(Router);          // Para navegação programática

  // Getter: lê o Signal totalItens do carrinho (atualiza automaticamente no template)
  get totalItens()    { return this.carrinho.totalItens(); }

  // Getter: retorna o objeto do usuário logado (null se não logado)
  get usuarioLogado() { return this.auth.currentUser(); }

  // Getter: extrai apenas o primeiro nome do usuário para exibição compacta
  // .split(' ')[0]: divide pelo espaço e pega a primeira palavra
  // ?? '': retorna string vazia se nome for null/undefined
  get primeiroNome() {
    return this.auth.currentUser()?.nome?.split(' ')[0] ?? '';
  }

  // .update(): inverte o valor atual do Signal (true → false, false → true)
  toggleMenu()  { this.menuAberto.update(v => !v); }
  fecharMenu()  { this.menuAberto.set(false); }

  // Faz logout e redireciona para a home
  logout() {
    this.auth.logout();    // Limpa tokens e zera o usuário
    this.fecharMenu();     // Fecha o menu mobile se estiver aberto
    this.router.navigate(['/']); // Navega para a página inicial
  }

  // Navega para "Meus Pedidos" e fecha o menu mobile
  irParaMeusPedidos() {
    this.fecharMenu();
    this.router.navigate(['/meus-pedidos']);
  }
}
