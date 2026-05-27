// ============================================================
// ARQUIVO: login.component.ts
// FUNÇÃO: Componente da página de login (/login).
//
// RESPONSABILIDADES:
// - Exibir o formulário de login (email + senha)
// - Validar os campos em tempo real (getters de validação)
// - Enviar as credenciais para o AuthService
// - Redirecionar: admin → /admin/dashboard | cliente → /
//
// PADRÃO DE VALIDAÇÃO:
// Os campos "tocado" (emailTocado, senhaTocada) são marcados como
// true quando o usuário submete o formulário. Isso evita mostrar
// erros de validação antes do usuário interagir com o campo.
//
// INTERFACE FormLogin:
// Define a estrutura do objeto "form" (tipagem forte).
// ============================================================

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
// FormsModule: habilita [(ngModel)] para two-way binding com os campos do formulário
import { FormsModule } from '@angular/forms';
// RouterLink: link para /cadastro | Router: navegação programática após login
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

// Interface com a estrutura dos dados do formulário de login
interface FormLogin {
  email: string;
  senha: string;
  lembrar: boolean; // Checkbox "Lembrar de mim" (não implementado no backend)
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  // Objeto ligado ao formulário via [(ngModel)] — sincronizado com os campos HTML
  form: FormLogin = { email: '', senha: '', lembrar: false };

  mostrarSenha    = false;  // Alterna entre mostrar/ocultar a senha
  emailTocado     = false;  // true após o usuário tentar submeter
  senhaTocada     = false;  // true após o usuário tentar submeter
  carregando      = false;  // true enquanto aguarda resposta da API
  mensagemSucesso = '';     // Exibida após operação bem-sucedida
  mensagemErro    = '';     // Exibida em caso de erro

  constructor(
    private router: Router,
    private auth: AuthService
  ) {}

  // -------------------------------------------------------
  // Getters de validação — usados no template para mostrar/ocultar erros
  //
  // Regex de email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  //   [^\s@]+: um ou mais caracteres que não sejam espaço ou @
  //   @: literal @
  //   [^\s@]+: domínio
  //   \.: ponto literal
  //   [^\s@]+: TLD (ex: com, br)
  // -------------------------------------------------------
  get emailValido(): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email);
  }

  get senhaValida(): boolean {
    return this.form.senha.length > 0; // Qualquer senha não vazia é aceita
  }

  get formularioValido(): boolean {
    return this.emailValido && this.senhaValida;
  }

  // -------------------------------------------------------
  // onSubmit(): chamado quando o formulário é submetido
  //
  // .subscribe({ next, error }): assina o Observable retornado por auth.login().
  //   next: executado quando a API responde com sucesso
  //   error: executado quando a API retorna erro
  //
  // err.error?.erro: tenta acessar a mensagem de erro retornada pelo backend.
  //   "?." (optional chaining): não lança erro se err.error for null.
  //   "?? 'fallback'": usa a mensagem padrão se não encontrar a mensagem do backend.
  // -------------------------------------------------------
  onSubmit(): void {
    // Marca os campos como "tocados" para ativar a validação visual
    this.emailTocado = true;
    this.senhaTocada = true;
    this.mensagemSucesso = '';
    this.mensagemErro    = '';

    if (!this.formularioValido) {
      this.mensagemErro = 'Preencha os campos corretamente.';
      return;
    }

    this.carregando = true;

    this.auth.login({ email: this.form.email, senha: this.form.senha }).subscribe({
      next: (res) => {
        this.carregando = false;
        // Redireciona baseado no papel do usuário
        const destino = res.usuario.papel === 'admin' ? '/admin/dashboard' : '/';
        this.router.navigate([destino]);
      },
      error: (err) => {
        this.carregando = false;
        this.mensagemErro = err.error?.erro ?? 'Email ou senha incorretos.';
      }
    });
  }
}
