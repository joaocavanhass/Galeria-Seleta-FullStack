import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CarrinhoService } from '../core/services/carrinho.service';
import { PedidoService, PedidoApi } from '../core/services/pedido.service';
import { UsuarioService, EnderecoApi } from '../core/services/usuario.service';
import { AuthService } from '../core/services/auth.service';
import { FreteService, FreteApi } from '../core/services/frete.service';

export type PagtoId = 'cartao' | 'pix' | 'dinheiro' | 'boleto';

export interface PagtoOpcao {
  id: PagtoId;
  label: string;
  badge?: string;
  obs?: string;
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css',
})
export class CheckoutComponent implements OnInit {

  private router         = inject(Router);
  private auth           = inject(AuthService);
  private pedidoService  = inject(PedidoService);
  private usuarioService = inject(UsuarioService);
  private freteService   = inject(FreteService);
  readonly carrinho      = inject(CarrinhoService);

  enderecos           = signal<EnderecoApi[]>([]);
  enderecoSelecionado = signal<EnderecoApi | null>(null);
  carregandoEnderecos = signal(true);
  erroCheckout        = signal('');
  pedidoCriado        = signal<PedidoApi | null>(null);

  // Fretes como array simples (não signal) para compatibilidade com @for no template
  freteOpcoes: FreteApi[] = [];
  freteSelecionado = signal<FreteApi | null>(null);
  freteRetirada    = signal(false);

  readonly pagtoOpcoes: PagtoOpcao[] = [
    { id: 'cartao',   label: 'Cartão' },
    { id: 'pix',      label: 'PIX',      badge: '10% OFF' },
    { id: 'dinheiro', label: 'Dinheiro', obs: 'Apenas para retirada na loja' },
    { id: 'boleto',   label: 'Boleto' },
  ];

  pagtoSelecionado = signal<PagtoId>('pix');
  step             = signal<1 | 2 | 3>(1);
  copiado          = signal<boolean>(false);

  readonly codigoPix = 'DN4QSND1Z91H2S3HY84TH5JAD4J8NQDBU923O9BHJ3DC4JE3XLANSFS7PAHHDF29B3S4UHAUSD1PJ3HDJBFA2PB3DR023WJADN4XHSL8NKJES71JEB23Q3XKSB3U4HUGJ3KL3DJ';

  ngOnInit(): void {
    // Carrega endereços
    this.usuarioService.listarEnderecos().subscribe({
      next: (lista) => {
        this.enderecos.set(lista);
        const principal = lista.find(e => e.principal) ?? lista[0] ?? null;
        this.enderecoSelecionado.set(principal);
        this.carregandoEnderecos.set(false);
      },
      error: () => { this.carregandoEnderecos.set(false); }
    });

    // Carrega opções de frete da API
    this.freteService.listar().subscribe({
      next: (lista) => {
        this.freteOpcoes = lista;
        if (lista.length > 0) this.freteSelecionado.set(lista[0]);
      },
      error: () => {
        // Fallback com valores padrão caso API falhe
        this.freteOpcoes = [
          { id: 1, nome: 'Padrão',   prazoMinimo: 5, prazoMaximo: 7, preco: 29.90 },
          { id: 2, nome: 'Expresso', prazoMinimo: 1, prazoMaximo: 3, preco: 54.90 },
        ];
        this.freteSelecionado.set(this.freteOpcoes[0]);
      }
    });
  }

  get usuario()    { return this.auth.currentUser(); }
  get subtotal()   { return this.carrinho.subtotal(); }
  get desconto()   { return this.carrinho.desconto(); }
  get totalItens() { return this.carrinho.totalItens(); }
  get itens()      { return this.carrinho.itens(); }

  get freteValor(): number {
    if (this.freteRetirada()) return 0;
    return this.freteSelecionado()?.preco ?? 0;
  }

  get descontoPix(): number {
    return this.pagtoSelecionado() === 'pix' ? this.subtotal * 0.1 : 0;
  }

  get totalFinal(): number {
    return this.subtotal + this.freteValor - this.desconto - this.descontoPix;
  }

  get descontoAtivo(): boolean { return this.descontoPix > 0; }

  get labelPagto(): string {
    const map: Record<PagtoId, string> = {
      pix:      'PIX débito on-line',
      cartao:   'Cartão de crédito',
      dinheiro: 'Dinheiro na retirada',
      boleto:   'Boleto bancário',
    };
    return map[this.pagtoSelecionado()];
  }

  selecionarFrete(f: FreteApi)       { this.freteSelecionado.set(f); this.freteRetirada.set(false); }
  selecionarRetirada()               { this.freteRetirada.set(true); this.freteSelecionado.set(null); }
  selecionarPagto(id: PagtoId)       { this.pagtoSelecionado.set(id); }
  selecionarEndereco(e: EnderecoApi) { this.enderecoSelecionado.set(e); }

  prazoLabel(f: FreteApi): string {
    return `${f.prazoMinimo} – ${f.prazoMaximo} dias`;
  }

  confirmar(): void {
    const usuario  = this.auth.currentUser();
    const endereco = this.enderecoSelecionado();

    if (!usuario || !endereco) {
      this.erroCheckout.set('Selecione um endereço de entrega.');
      return;
    }

    if (this.itens.length === 0) {
      this.erroCheckout.set('Seu carrinho está vazio.');
      return;
    }

    const produtoIds = this.itens.flatMap(item =>
      Array(item.quantidade).fill(item.id) as number[]
    );

    const freteId  = this.freteRetirada() ? undefined : (this.freteSelecionado()?.id ?? undefined);
    const cupom    = this.carrinho.cupom() || undefined;

    this.erroCheckout.set('');

    this.pedidoService.criar({
      usuarioId:   usuario.id,
      enderecoId:  endereco.id,
      produtoIds,
      freteId,
      codigoCupom: cupom
    }).subscribe({
      next: (pedido) => {
        this.pedidoCriado.set(pedido);
        this.step.set(2);
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: (err) => {
        this.erroCheckout.set(err.error?.erro ?? 'Erro ao criar pedido. Tente novamente.');
      }
    });
  }

  finalizarPagamento(): void {
    this.step.set(3);
    this.carrinho.limparCarrinho();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  voltarParaCheckout(): void {
    this.step.set(1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  numeroPedido(): number { return this.pedidoCriado()?.id ?? 0; }

  get usuarioNome(): string     { return this.auth.currentUser()?.nome ?? ''; }
  get usuarioTelefone(): string { return this.auth.currentUser()?.telefone ?? ''; }
  get enderecoFormatado(): string {
    const e = this.enderecoSelecionado();
    if (!e) return 'Nenhum endereço cadastrado. Adicione um em seu perfil.';
    return `${e.rua}, ${e.cidade} – ${e.estado} – ${e.cep}`;
  }

  irParaHome(): void    { this.router.navigate(['/']); }
  irParaPedidos(): void { this.router.navigate(['/meus-pedidos']); }

  copiarCodigo(): void {
    navigator.clipboard.writeText(this.codigoPix).then(() => {
      this.copiado.set(true);
      setTimeout(() => this.copiado.set(false), 2500);
    });
  }
}