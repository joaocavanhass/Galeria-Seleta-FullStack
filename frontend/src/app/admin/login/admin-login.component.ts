// ============================================================
// ARQUIVO: admin-login.component.ts
// FUNÇÃO: Componente da tela de login do painel administrativo (/admin/login).
//
// FUNCIONAMENTO:
// Usa o AuthService principal (o mesmo do site público) para autenticar.
// Após login bem-sucedido, redireciona para /admin/dashboard.
// Se as credenciais estiverem erradas, exibe a mensagem de erro da API.
//
// DIFERENÇA DO login.component.ts:
// O login do admin destina sempre para /admin/dashboard.
// O loginComponent do site pode ir para / ou /admin/dashboard dependendo do papel.
//
// SIGNALS USADOS:
// loading: exibe spinner enquanto aguarda a API
// erro: exibe mensagem de erro ao usuário
// mostrarSenha: alterna visibilidade do campo senha
// ============================================================

import { Component, signal } from '@angular/core';
// CommonModule: *ngIf para condicionar exibição de loading/erro
import { CommonModule } from '@angular/common';
// FormsModule: [(ngModel)] para ligar os campos de email e senha
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent {

  // Campos do formulário ligados ao template via [(ngModel)]
  email = '';
  senha = '';

  // Signals de estado da UI
  loading      = signal(false);  // true enquanto aguarda a resposta da API
  erro         = signal('');     // Mensagem de erro (vazia = sem erro)
  mostrarSenha = signal(false);  // Controla exibição da senha

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  // -------------------------------------------------------
  // login(): valida os campos e chama o AuthService
  //
  // .set(valor): atualiza o valor de um Signal
  // err.error?.erro: mensagem de erro retornada pelo backend
  // ?? 'fallback': usa mensagem padrão se o backend não enviou uma
  // -------------------------------------------------------
  login() {
    // Validação básica antes de chamar a API
    if (!this.email || !this.senha) {
      this.erro.set('Preencha todos os campos.');
      return;
    }

    this.loading.set(true);
    this.erro.set(''); // Limpa erro anterior

    this.auth.login({ email: this.email, senha: this.senha }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/admin/dashboard']); // Redireciona para o painel
      },
      error: (err) => {
        this.loading.set(false);
        // Exibe a mensagem de erro da API ou uma mensagem padrão
        this.erro.set(err.error?.erro ?? 'Email ou senha incorretos.');
      }
    });
  }
}
