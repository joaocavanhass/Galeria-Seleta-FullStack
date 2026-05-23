import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminAuthService } from '../services/admin-auth.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent {
  email = '';
  senha = '';
  loading = signal(false);
  erro = signal('');
  mostrarSenha = signal(false);

  constructor(private auth: AdminAuthService, private router: Router) {}

  login() {
    if (!this.email || !this.senha) {
      this.erro.set('Preencha todos os campos.');
      return;
    }
    this.loading.set(true);
    this.erro.set('');

    setTimeout(() => {
      const result = this.auth.login(this.email, this.senha);
      this.loading.set(false);
      if (result.success) {
        this.router.navigate(['/admin/dashboard']);
      } else {
        this.erro.set(result.message);
      }
    }, 800);
  }
}
