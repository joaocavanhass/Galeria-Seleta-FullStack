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
  codigo: string[] = ['', '', '', '', '', ''];
  contagemReenvio = 0;
  private timerReenvio: ReturnType<typeof setInterval> | null = null;

  @ViewChildren('digitoRef') digitosRef!: QueryList<ElementRef<HTMLInputElement>>;

  /* ── Etapa 3: nova senha ── */
  novaSenha = '';
  confirmarSenha = '';
  mostrarSenha = false;
  mostrarConfirmar = false;
  senhaTocada = false;
  confirmarTocada = false;

  /* ── Estado global ── */
  carregando = false;
  mensagemErro = '';

  /* ══════════════════════════════════════════════
     VALIDAÇÕES
  ══════════════════════════════════════════════ */

  get emailValido(): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email);
  }

  get codigoIncompleto(): boolean {
    return this.codigo.some(d => d === '');
  }

  get codigoCompleto(): string {
    return this.codigo.join('');
  }

  get senhaValida(): boolean {
    return this.novaSenha.length >= 8;
  }

  get senhasIguais(): boolean {
    return this.novaSenha === this.confirmarSenha && this.confirmarSenha.length > 0;
  }

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

  /* ══════════════════════════════════════════════
     ETAPA 1 — Enviar código
  ══════════════════════════════════════════════ */

  enviarCodigo(): void {
    this.emailTocado = true;
    this.mensagemErro = '';

    if (!this.emailValido) return;

    this.carregando = true;

    /* Substituir pela chamada real à API */
    setTimeout(() => {
      this.carregando = false;
      this.etapa = 2;
      this.iniciarContagemReenvio();

      /* Foca no primeiro dígito após renderização */
      setTimeout(() => this.focarDigito(0), 100);
    }, 1500);
  }

  /* ══════════════════════════════════════════════
     ETAPA 2 — Código de verificação
  ══════════════════════════════════════════════ */

  aoDigitar(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const valor = input.value.replace(/\D/g, '').slice(-1);
    this.codigo[index] = valor;
    input.value = valor;

    if (valor && index < 5) {
      this.focarDigito(index + 1);
    }
  }

  aoApagar(event: KeyboardEvent, index: number): void {
    if (event.key === 'Backspace' && !this.codigo[index] && index > 0) {
      this.focarDigito(index - 1);
    }
  }

  aoColar(event: ClipboardEvent): void {
    event.preventDefault();
    const texto = event.clipboardData?.getData('text') ?? '';
    const digitos = texto.replace(/\D/g, '').slice(0, 6).split('');
    digitos.forEach((d, i) => { this.codigo[i] = d; });
    const proximo = Math.min(digitos.length, 5);
    setTimeout(() => this.focarDigito(proximo), 0);
  }

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

    /* Substituir pela validação real do código */
    setTimeout(() => {
      this.carregando = false;

      /* Simulação: código correto = '123456' */
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
    if (this.contagemReenvio > 0) return;
    this.mensagemErro = '';
    this.codigo = ['', '', '', '', '', ''];

    /* Substituir pela chamada real */
    setTimeout(() => this.focarDigito(0), 100);
    this.iniciarContagemReenvio();
  }

  private iniciarContagemReenvio(): void {
    this.contagemReenvio = 60;
    this.timerReenvio = setInterval(() => {
      this.contagemReenvio--;
      if (this.contagemReenvio <= 0 && this.timerReenvio) {
        clearInterval(this.timerReenvio);
        this.timerReenvio = null;
      }
    }, 1000);
  }

  /* ══════════════════════════════════════════════
     ETAPA 3 — Redefinir senha
  ══════════════════════════════════════════════ */

  redefinirSenha(): void {
    this.senhaTocada = true;
    this.confirmarTocada = true;
    this.mensagemErro = '';

    if (!this.senhaValida || !this.senhasIguais) return;

    this.carregando = true;

    /* Substituir pela chamada real à API */
    setTimeout(() => {
      this.carregando = false;
      this.etapa = 4;
    }, 1500);
  }

  /* ══════════════════════════════════════════════
     LIFECYCLE
  ══════════════════════════════════════════════ */

  ngOnDestroy(): void {
    if (this.timerReenvio) clearInterval(this.timerReenvio);
  }
}