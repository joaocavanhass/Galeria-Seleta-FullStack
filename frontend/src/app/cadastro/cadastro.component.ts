import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

interface FormCadastro {
  nome: string;
  sobrenome: string;
  email: string;
  cpf: string;
  telefone: string;
  senha: string;
  confirmarSenha: string;
  aceitaTermos: boolean;
}

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.css'
})
export class CadastroComponent {

  /* ── Dados do formulário ── */
  form: FormCadastro = {
    nome: '',
    sobrenome: '',
    email: '',
    cpf: '',
    telefone: '',
    senha: '',
    confirmarSenha: '',
    aceitaTermos: false
  };

  /* ── Visibilidade das senhas ── */
  mostrarSenha = false;
  mostrarConfirmar = false;

  /* ── Campos tocados (validação on blur) ── */
  nomeTocado = false;
  sobrenomeTocado = false;
  emailTocado = false;
  senhaTocada = false;
  confirmarTocada = false;

  /* ── Estado de envio ── */
  carregando = false;
  mensagemSucesso = '';
  mensagemErro = '';

  /* ══════════════════════════════════════════════
     GETTERS DE VALIDAÇÃO
  ══════════════════════════════════════════════ */

  get nomeValido(): boolean {
    return this.form.nome.trim().length > 0;
  }

  get sobrenomeValido(): boolean {
    return this.form.sobrenome.trim().length > 0;
  }

  get emailValido(): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email);
  }

  get senhaValida(): boolean {
    return this.form.senha.length >= 8;
  }

  get senhasIguais(): boolean {
    return this.form.senha === this.form.confirmarSenha && this.form.confirmarSenha.length > 0;
  }

  get forcaSenha(): number {
    const s = this.form.senha;
    let pts = 0;
    if (s.length >= 8)                           pts++;
    if (/[A-Z]/.test(s) && /[a-z]/.test(s))     pts++;
    if (/\d/.test(s) && /[^a-zA-Z0-9]/.test(s)) pts++;
    return pts;
  }

  get labelForca(): string {
    return ['', 'Fraca', 'Média', 'Forte'][this.forcaSenha] ?? '';
  }

  get formularioValido(): boolean {
    return (
      this.nomeValido &&
      this.sobrenomeValido &&
      this.emailValido &&
      this.senhaValida &&
      this.senhasIguais &&
      this.form.aceitaTermos
    );
  }

  /* ══════════════════════════════════════════════
     MÁSCARAS
  ══════════════════════════════════════════════ */

  formatarCpf(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 11);
    if (v.length > 9)      v = v.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
    else if (v.length > 6) v = v.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
    else if (v.length > 3) v = v.replace(/(\d{3})(\d{1,3})/, '$1.$2');
    this.form.cpf = v;
    input.value = v;
  }

  formatarTelefone(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 11);
    if (v.length > 10)     v = v.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    else if (v.length > 6) v = v.replace(/(\d{2})(\d{4,5})(\d{0,4})/, '($1) $2-$3');
    else if (v.length > 2) v = v.replace(/(\d{2})(\d+)/, '($1) $2');
    this.form.telefone = v;
    input.value = v;
  }

  /* ══════════════════════════════════════════════
     ENVIO
  ══════════════════════════════════════════════ */

  onSubmit(): void {
    this.nomeTocado = true;
    this.sobrenomeTocado = true;
    this.emailTocado = true;
    this.senhaTocada = true;
    this.confirmarTocada = true;
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (!this.formularioValido) {
      this.mensagemErro = 'Corrija os campos destacados antes de continuar.';
      return;
    }

    this.carregando = true;

    /* Substituir pelo serviço real de autenticação */
    setTimeout(() => {
      this.carregando = false;
      this.mensagemSucesso = 'Conta criada com sucesso! Bem-vindo(a) à Galeria Seleta.';
      /* Em produção: this.router.navigate(['/']) */
    }, 1800);
  }
}