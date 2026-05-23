import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AdminAuthService } from '../admin/services/admin-auth.service';

interface FormLogin {
  email: string;
  senha: string;
  lembrar: boolean;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  form: FormLogin = { email: '', senha: '', lembrar: false };

  mostrarSenha = false;
  emailTocado  = false;
  senhaTocada  = false;
  carregando   = false;
  mensagemSucesso = '';
  mensagemErro    = '';

  constructor(
    private router: Router,
    private adminAuth: AdminAuthService
  ) {}

  get emailValido(): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email);
  }

  get senhaValida(): boolean {
    return this.form.senha.length > 0;
  }

  get formularioValido(): boolean {
    return this.emailValido && this.senhaValida;
  }

  onSubmit(): void {
    this.emailTocado = true;
    this.senhaTocada = true;
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (!this.formularioValido) {
      this.mensagemErro = 'Preencha os campos corretamente.';
      return;
    }

    this.carregando = true;

    setTimeout(() => {
      this.carregando = false;

      // Verifica se é admin → redireciona para o painel
      const isAdmin = this.adminAuth.tryAdminLogin(this.form.email, this.form.senha);
      if (isAdmin) {
        this.router.navigate(['/admin/dashboard']);
        return;
      }

      // Usuário comum → substituir pela autenticação real
      this.mensagemSucesso = 'Login realizado com sucesso!';
      this.router.navigate(['/']);
    }, 1000);
  }
}