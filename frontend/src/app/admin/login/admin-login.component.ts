import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  email        = '';
  senha        = '';
  loading      = signal(false);
  erro         = signal('');
  mostrarSenha = signal(false);

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  login() {
    if (!this.email || !this.senha) {
      this.erro.set('Preencha todos os campos.');
      return;
    }
    this.loading.set(true);
    this.erro.set('');

    this.auth.login({ email: this.email, senha: this.senha }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/admin/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.erro.set(err.error?.erro ?? 'Email ou senha incorretos.');
      }
    });
  }
}
