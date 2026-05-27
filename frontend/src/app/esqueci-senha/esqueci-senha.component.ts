// ============================================================
// ARQUIVO: esqueci-senha.component.ts
// FUNÇÃO: Componente da página de recuperação de senha (/esqueci-senha).
//
// FLUXO EM 4 ETAPAS:
// Etapa 1 → Usuário informa o email
// Etapa 2 → Usuário digita o código de 6 dígitos recebido
// Etapa 3 → Usuário define a nova senha
// Etapa 4 → Tela de sucesso
//
// NOTA SOBRE INTEGRAÇÃO:
// As etapas 1, 2 e 3 usam setTimeout() para simular chamadas de API.
// Em produção, devem ser substituídas por chamadas ao AuthService.
//
// @ViewChildren('digitoRef') + QueryList:
// Referência aos 6 inputs de dígito do código de verificação.
// Permite focar automaticamente no próximo input ao digitar.
// QueryList: lista que o Angular atualiza quando os elementos são
// adicionados/removidos do DOM.
//
// CONTAGEM REGRESSIVA DE REENVIO:
// Timer de 60 segundos antes de permitir reenviar o código.
// Limpo no ngOnDestroy() para evitar memory leak.
//
// FORÇA DA SENHA:
// Mesma lógica do cadastro.component.ts (0-3 pontos).
// ============================================================

// ViewChildren: acessa múltiplos elementos com a mesma referência
// QueryList: lista dos elementos encontrados
// ElementRef: referência a um elemento DOM nativo
// OnDestroy: interface para cleanup ao destruir o componente
import { Component, ViewChildren, QueryList, ElementRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-esqueci-senha',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './esqueci-senha.component.html',
  styleUrl: './esqueci-senha.component.css'
})
export class EsqueciSenhaComponent implements OnDestroy {

  /* ── Controle de etapas ── */
  etapa = 1; // 1 = email | 2 = código | 3 = nova senha | 4 = sucesso

  /* ── Etapa 1: e-mail ── */
  email = '';
  emailTocado = false;

  /* ── Etapa 2: código ── */
  // Array de 6 strings, uma por dígito — ligado aos inputs via [(ngModel)]
  codigo: string[] = ['', '', '', '', '', ''];
  contagemReenvio = 0; // Contador regressivo (0 = pode reenviar)
  private timerReenvio: ReturnType<typeof setInterval> | null = null;

  // @ViewChildren: acessa todos os elementos marcados com #digitoRef no template
  // QueryList<ElementRef<HTMLInputElement>>: lista de referências para os inputs
  @ViewChildren('digitoRef') digitosRef!: QueryList<ElementRef<HTMLInputElement>>;

  /* ── Etapa 3: nova senha ── */
  novaSenha = '';
  confirmarSenha = '';
  mostrarSenha     = false;
  mostrarConfirmar = false;
  senhaTocada      = false;
  confirmarTocada  = false;

  /* ── Estado global ── */
  carregando   = false;
  mensagemErro = '';

  /* ══════════ VALIDAÇÕES ══════════ */

  get emailValido(): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email);
  }

  // some(): retorna true se pelo menos um elemento satisfaz a condição
  get codigoIncompleto(): boolean {
    return this.codigo.some(d => d === '');
  }

  // join(''): une todos os dígitos em uma string (ex: ['1','2','3','4','5','6'] → '123456')
  get codigoCompleto(): string {
    return this.codigo.join('');
  }

  get senhaValida(): boolean { return this.novaSenha.length >= 8; }
  get senhasIguais(): boolean { return this.novaSenha === this.confirmarSenha && this.confirmarSenha.length > 0; }

  get forcaSenha(): number {
    const s = this.novaSenha;
    let pts = 0;
    if (s.length >= 8)                            pts++;
    if (/[A-Z]/.test(s) && /[a-z]/.test(s))      pts++;
    if (/\d/.test(s) && /[^a-zA-Z0-9]/.test(s))  pts++;
    return pts;
  }

  get labelForca(): string {
    return ['', 'Fraca', 'Média', 'Forte'][this.forcaSenha] ?? '';
  }

  /* ══════════ ETAPA 1 — Enviar código ══════════ */

  enviarCodigo(): void {
    this.emailTocado = true;
    this.mensagemErro = '';
    if (!this.emailValido) return;

    this.carregando = true;

    // TODO: substituir pelo chamada real: this.auth.esqueceuSenha(this.email)
    setTimeout(() => {
      this.carregando = false;
      this.etapa = 2;
      this.iniciarContagemReenvio();
      // Foca no primeiro dígito após o template renderizar (delay de 100ms)
      setTimeout(() => this.focarDigito(0), 100);
    }, 1500);
  }

  /* ══════════ ETAPA 2 — Código de verificação ══════════ */

  // Chamado quando o usuário digita em um dos inputs de dígito
  aoDigitar(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    // Remove não-dígitos e pega apenas o último caractere digitado
    const valor = input.value.replace(/\D/g, '').slice(-1);
    this.codigo[index] = valor;
    input.value = valor;

    // Avança para o próximo input automaticamente ao digitar
    if (valor && index < 5) {
      this.focarDigito(index + 1);
    }
  }

  // Retrocede para o input anterior ao pressionar Backspace em campo vazio
  aoApagar(event: KeyboardEvent, index: number): void {
    if (event.key === 'Backspace' && !this.codigo[index] && index > 0) {
      this.focarDigito(index - 1);
    }
  }

  // Ao colar um código, distribui os dígitos pelos inputs automaticamente
  aoColar(event: ClipboardEvent): void {
    event.preventDefault();
    const texto = event.clipboardData?.getData('text') ?? '';
    const digitos = texto.replace(/\D/g, '').slice(0, 6).split('');
    digitos.forEach((d, i) => { this.codigo[i] = d; });
    const proximo = Math.min(digitos.length, 5);
    setTimeout(() => this.focarDigito(proximo), 0);
  }

  // Foca em um input de dígito pelo índice
  private focarDigito(index: number): void {
    const els = this.digitosRef?.toArray();
    if (els?.[index]) {
      els[index].nativeElement.focus();
    }
  }

  verificarCodigo(): void {
    this.mensagemErro = '';
    if (this.codigoIncompleto) return;

    this.carregando = true;

    // TODO: substituir pela validação real do código no backend
    setTimeout(() => {
      this.carregando = false;
      // Simulação: código correto = '123456'
      if (this.codigoCompleto === '123456') {
        this.etapa = 3;
      } else {
        this.mensagemErro = 'Código incorreto. Verifique e tente novamente.';
        this.codigo = ['', '', '', '', '', ''];
        setTimeout(() => this.focarDigito(0), 100);
      }
    }, 1200);
  }

  reenviarCodigo(): void {
    if (this.contagemReenvio > 0) return; // Bloqueado durante a contagem
    this.mensagemErro = '';
    this.codigo = ['', '', '', '', '', ''];
    setTimeout(() => this.focarDigito(0), 100);
    this.iniciarContagemReenvio();
  }

  // Inicia o timer de 60 segundos antes de permitir reenviar
  private iniciarContagemReenvio(): void {
    this.contagemReenvio = 60;
    this.timerReenvio = setInterval(() => {
      this.contagemReenvio--;
      if (this.contagemReenvio <= 0 && this.timerReenvio) {
        clearInterval(this.timerReenvio);
        this.timerReenvio = null;
      }
    }, 1000); // Decrementa a cada 1 segundo
  }

  /* ══════════ ETAPA 3 — Redefinir senha ══════════ */

  redefinirSenha(): void {
    this.senhaTocada = true;
    this.confirmarTocada = true;
    this.mensagemErro = '';
    if (!this.senhaValida || !this.senhasIguais) return;

    this.carregando = true;

    // TODO: substituir pela chamada real: this.auth.redefinirSenha(token, novaSenha)
    setTimeout(() => {
      this.carregando = false;
      this.etapa = 4; // Tela de sucesso
    }, 1500);
  }

  /* ══════════ LIFECYCLE ══════════ */

  // Limpa o timer ao destruir o componente para evitar memory leak
  ngOnDestroy(): void {
    if (this.timerReenvio) clearInterval(this.timerReenvio);
  }
}
