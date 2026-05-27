import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CarrinhoService } from '../core/services/carrinho.service';
import { PedidoService, PedidoApi } from '../core/services/pedido.service';
import { UsuarioService, EnderecoApi } from '../core/services/usuario.service';
import { AuthService } from '../core/services/auth.service';
import { FreteService, FreteApi } from '../core/services/frete.service';

interface ViaCepResponse {
  logradouro?: string;
  localidade?: string;
  uf?: string;
  erro?: boolean;
}

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
  private http           = inject(HttpClient);
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

  mostrarFormEndereco = signal(false);
  salvandoEndereco    = signal(false);
  buscandoCep         = signal(false);
  erroEndereco        = signal('');
  erroCep             = signal('');
  enderecoEditandoId  = signal<number | null>(null);
  novoEndereco        = { rua: '', cidade: '', estado: '', cep: '' };

  editandoTelefone  = signal(false);
  salvandoTelefone  = signal(false);
  novoTelefone      = '';

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
        if (lista.length === 0) this.mostrarFormEndereco.set(true);
      },
      error: () => { this.carregandoEnderecos.set(false); this.mostrarFormEndereco.set(true); }
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

  salvarEndereco(): void {
    const { rua, cidade, cep } = this.novoEndereco;
    const estado = this.novoEndereco.estado.toUpperCase();
    if (!rua || !cidade || !estado || !cep) {
      this.erroEndereco.set('Preencha todos os campos do endereço.');
      return;
    }
    this.erroEndereco.set('');
    this.salvandoEndereco.set(true);

    const editandoId = this.enderecoEditandoId();
    if (editandoId !== null) {
      const antigo = this.enderecos().find(e => e.id === editandoId);
      const ePrincipal = antigo?.principal ?? false;
      this.usuarioService.removerEndereco(editandoId).subscribe({
        next: () => {
          this.enderecos.update(l => l.filter(e => e.id !== editandoId));
          this.criarEndereco(rua, cidade, estado, cep, ePrincipal);
        },
        error: () => {
          this.erroEndereco.set('Erro ao atualizar endereço. Tente novamente.');
          this.salvandoEndereco.set(false);
        }
      });
    } else {
      const ePrincipal = this.enderecos().length === 0;
      this.criarEndereco(rua, cidade, estado, cep, ePrincipal);
    }
  }

  private criarEndereco(rua: string, cidade: string, estado: string, cep: string, principal: boolean): void {
    this.usuarioService.adicionarEndereco({ rua, cidade, estado, cep, principal }).subscribe({
      next: (end) => {
        this.enderecos.update(l => [...l, end]);
        this.enderecoSelecionado.set(end);
        this.mostrarFormEndereco.set(false);
        this.salvandoEndereco.set(false);
        this.enderecoEditandoId.set(null);
        this.novoEndereco = { rua: '', cidade: '', estado: '', cep: '' };
      },
      error: () => {
        this.erroEndereco.set('Erro ao salvar endereço. Tente novamente.');
        this.salvandoEndereco.set(false);
      }
    });
  }

  editarEndereco(e: EnderecoApi): void {
    this.novoEndereco = { rua: e.rua, cidade: e.cidade, estado: e.estado, cep: e.cep };
    this.enderecoEditandoId.set(e.id);
    this.mostrarFormEndereco.set(true);
    this.erroEndereco.set('');
    this.erroCep.set('');
  }

  cancelarNovoEndereco(): void {
    this.mostrarFormEndereco.set(false);
    this.erroEndereco.set('');
    this.erroCep.set('');
    this.enderecoEditandoId.set(null);
    this.novoEndereco = { rua: '', cidade: '', estado: '', cep: '' };
  }

  iniciarEditarTelefone(): void {
    this.novoTelefone = this.auth.currentUser()?.telefone ?? '';
    this.editandoTelefone.set(true);
  }

  cancelarEditarTelefone(): void {
    this.editandoTelefone.set(false);
    this.novoTelefone = '';
  }

  salvarTelefone(): void {
    const tel = this.novoTelefone.trim();
    if (!tel) return;
    this.salvandoTelefone.set(true);
    this.usuarioService.atualizar({ telefone: tel }).subscribe({
      next: () => {
        this.auth.patchUsuario({ telefone: tel });
        this.editandoTelefone.set(false);
        this.salvandoTelefone.set(false);
        this.novoTelefone = '';
      },
      error: () => { this.salvandoTelefone.set(false); }
    });
  }

  formatarCep(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 8);
    if (v.length > 5) v = `${v.slice(0, 5)}-${v.slice(5)}`;
    this.novoEndereco.cep = v;
    input.value = v;
    this.erroCep.set('');
  }

  buscarCep(): void {
    const cep = this.novoEndereco.cep.replace(/\D/g, '');
    if (cep.length !== 8) return;
    this.buscandoCep.set(true);
    this.erroCep.set('');
    this.http.get<ViaCepResponse>(`https://viacep.com.br/ws/${cep}/json/`).subscribe({
      next: (dados) => {
        this.buscandoCep.set(false);
        if (dados.erro) {
          this.erroCep.set('CEP não encontrado.');
          return;
        }
        if (dados.logradouro) this.novoEndereco.rua    = dados.logradouro;
        if (dados.localidade) this.novoEndereco.cidade = dados.localidade;
        if (dados.uf)         this.novoEndereco.estado = dados.uf;
      },
      error: () => {
        this.buscandoCep.set(false);
        this.erroCep.set('Erro ao buscar CEP. Verifique sua conexão.');
      }
    });
  }

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