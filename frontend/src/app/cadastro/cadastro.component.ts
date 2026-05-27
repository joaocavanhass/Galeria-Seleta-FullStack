// ============================================================
// ARQUIVO: cadastro.component.ts
// FUNÇÃO: Componente da página de cadastro de nova conta (/cadastro).
//
// RESPONSABILIDADES:
// - Formulário com nome, sobrenome, email, CPF, telefone, senha
// - Validações em tempo real (getters)
// - Formatação de CPF e telefone com máscaras
// - Indicador de força da senha (fraca/média/forte)
// - Envio dos dados ao AuthService
//
// CONCEITOS USADOS:
//
// Máscara de CPF: formata enquanto o usuário digita
//   "123.456.789-01" — usando regex com grupos de captura ($1, $2, etc.)
//
// Máscara de telefone: formata para "(11) 99999-9999"
//
// Força da senha: pontuação de 0 a 3
//   1 ponto: 8+ caracteres
//   2 pontos: maiúsculas + minúsculas
//   3 pontos: números + caracteres especiais
//
// CAMPOS TOCADOS: flag para evitar mostrar erros antes do usuário
// interagir. Marcados como true ao submeter o formulário.
// ============================================================

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

// Interface da estrutura do formulário de cadastro
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

  // Objeto ligado ao formulário HTML via [(ngModel)]
  form: FormCadastro = {
    nome: '', sobrenome: '', email: '', cpf: '',
    telefone: '', senha: '', confirmarSenha: '', aceitaTermos: false
  };

  mostrarSenha     = false; // Alterna visibilidade da senha
  mostrarConfirmar = false; // Alterna visibilidade da confirmação

  // Flags "tocado": ativadas ao submeter para mostrar erros de validação
  nomeTocado = false; sobrenomeTocado = false; emailTocado = false;
  senhaTocada = false; confirmarTocada = false;

  carregando      = false;
  mensagemSucesso = '';
  mensagemErro    = '';

  constructor(
    private router: Router,
    private auth: AuthService
  ) {}

  // Validações — usadas no template para mostrar mensagens de erro
  get nomeValido(): boolean     { return this.form.nome.trim().length > 0; }
  get sobrenomeValido(): boolean { return this.form.sobrenome.trim().length > 0; }
  get emailValido(): boolean    { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email); }
  get senhaValida(): boolean    { return this.form.senha.length >= 8; } // Mínimo 8 caracteres
  get senhasIguais(): boolean   { return this.form.senha === this.form.confirmarSenha && this.form.confirmarSenha.length > 0; }

  // -------------------------------------------------------
  // forcaSenha: retorna 0 (sem senha), 1 (fraca), 2 (média), 3 (forte)
  // Cada critério adiciona 1 ponto:
  //   1: comprimento >= 8
  //   2: tem letras maiúsculas e minúsculas
  //   3: tem dígitos E caracteres especiais
  // -------------------------------------------------------
  get forcaSenha(): number {
    const s = this.form.senha;
    let pts = 0;
    if (s.length >= 8)                           pts++;
    if (/[A-Z]/.test(s) && /[a-z]/.test(s))     pts++;
    if (/\d/.test(s) && /[^a-zA-Z0-9]/.test(s)) pts++;
    return pts;
  }

  // Converte a pontuação em rótulo legível
  get labelForca(): string {
    return ['', 'Fraca', 'Média', 'Forte'][this.forcaSenha] ?? '';
  }

  get formularioValido(): boolean {
    return this.nomeValido && this.sobrenomeValido && this.emailValido &&
           this.senhaValida && this.senhasIguais && this.form.aceitaTermos;
  }

  // -------------------------------------------------------
  // formatarCpf(): aplica máscara CPF enquanto o usuário digita
  // Remove tudo que não for dígito e reformata progressivamente:
  //   "123"       → "123"
  //   "123456"    → "123.456"
  //   "123456789" → "123.456.789"
  //   "12345678901" → "123.456.789-01"
  // -------------------------------------------------------
  formatarCpf(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 11); // Remove não-dígitos, limita a 11
    if (v.length > 9)      v = v.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
    else if (v.length > 6) v = v.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
    else if (v.length > 3) v = v.replace(/(\d{3})(\d{1,3})/, '$1.$2');
    this.form.cpf = v;
    input.value = v;
  }

  // -------------------------------------------------------
  // formatarTelefone(): aplica máscara (11) 99999-9999
  // -------------------------------------------------------
  formatarTelefone(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 11); // Máximo 11 dígitos
    if (v.length > 10)     v = v.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    else if (v.length > 6) v = v.replace(/(\d{2})(\d{4,5})(\d{0,4})/, '($1) $2-$3');
    else if (v.length > 2) v = v.replace(/(\d{2})(\d+)/, '($1) $2');
    this.form.telefone = v;
    input.value = v;
  }

  onSubmit(): void {
    // Ativa validação visual em todos os campos
    this.nomeTocado = true; this.sobrenomeTocado = true;
    this.emailTocado = true; this.senhaTocada = true; this.confirmarTocada = true;
    this.mensagemSucesso = ''; this.mensagemErro = '';

    if (!this.formularioValido) {
      this.mensagemErro = 'Corrija os campos destacados antes de continuar.';
      return;
    }

    this.carregando = true;

    // Combina nome e sobrenome em um único campo "nome" para a API
    const nomeCompleto = `${this.form.nome.trim()} ${this.form.sobrenome.trim()}`;

    this.auth.registrar({
      nome: nomeCompleto,
      email: this.form.email,
      senha: this.form.senha,
      telefone: this.form.telefone || undefined, // undefined = não envia o campo se vazio
      cpf: this.form.cpf || undefined
    }).subscribe({
      next: () => {
        this.carregando = false;
        this.router.navigate(['/']); // Redireciona para home após cadastro
      },
      error: (err) => {
        this.carregando = false;
        this.mensagemErro = err.error?.erro ?? 'Erro ao criar conta. Tente novamente.';
      }
    });
  }
}
