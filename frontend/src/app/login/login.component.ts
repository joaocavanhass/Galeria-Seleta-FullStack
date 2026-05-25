import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

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

  mostrarSenha    = false;
  emailTocado     = false;
  senhaTocada     = false;
  carregando      = false;
  mensagemSucesso = '';
  mensagemErro    = '';

  constructor(
    private router: Router,
    private auth: AuthService
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
    this.mensagemErro    = '';

    if (!this.formularioValido) {
      this.mensagemErro = 'Preencha os campos corretamente.';
      return;
    }

    this.carregando = true;

    this.auth.login({ email: this.form.email, senha: this.form.senha }).subscribe({
      next: (res) => {
        this.carregando = false;
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
