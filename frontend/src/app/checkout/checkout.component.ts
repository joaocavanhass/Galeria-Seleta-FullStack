// ============================================================
// ARQUIVO: checkout.component.ts
// FUNÇÃO: Componente da página de finalização de compra (/checkout).
//
// FLUXO EM 3 ETAPAS (controlado pelo Signal "step"):
// Etapa 1 → Seleção de endereço, frete e pagamento
// Etapa 2 → Confirmação do pagamento (exibe QR code PIX ou instruções)
// Etapa 3 → Tela de sucesso (pedido confirmado)
//
// RESPONSABILIDADES:
// - Carregar endereços do usuário (e mostrar formulário se não tiver)
// - Adicionar/editar endereços com busca automática de CEP (ViaCEP)
// - Carregar opções de frete da API
// - Calcular desconto de 10% para pagamento PIX
// - Criar o pedido no backend (método confirmar())
// - Limpar o carrinho após confirmação
//
// SIGNALS USADOS:
// enderecos, enderecoSelecionado, freteSelecionado, pagtoSelecionado,
// mostrarFormEndereco, step, etc.
//
// INTEGRAÇÃO COM ViaCEP:
// buscarCep() faz GET em https://viacep.com.br/ws/{cep}/json/
// Preenche automaticamente rua, cidade e estado.
// ViaCepResponse: interface com os campos retornados pela API.
//
// CÁLCULO DE TOTAL:
// totalFinal = subtotal + freteValor - descontoCupom - descontoPix
// ============================================================

import { Component, OnInit, inject, signal } from '@angular/core';
// CommonModule: *ngIf, *ngFor | CurrencyPipe: formato de moeda
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
// HttpClient: para chamar a API do ViaCEP
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CarrinhoService } from '../core/services/carrinho.service';
import { PedidoService, PedidoApi } from '../core/services/pedido.service';
import { UsuarioService, EnderecoApi } from '../core/services/usuario.service';
import { AuthService } from '../core/services/auth.service';
import { FreteService, FreteApi } from '../core/services/frete.service';

// Formato da resposta da API ViaCEP (consulta de CEP)
interface ViaCepResponse {
  logradouro?: string; // Rua/logradouro
  localidade?: string; // Cidade
  uf?: string;         // Estado (sigla)
  erro?: boolean;      // true se o CEP não foi encontrado
}

// Tipos de pagamento aceitos
export type PagtoId = 'cartao' | 'pix' | 'dinheiro' | 'boleto';

// Interface de uma opção de pagamento (para o template)
export interface PagtoOpcao {
  id: PagtoId;
  label: string;
  badge?: string; // Ex: "10% OFF" para PIX
  obs?: string;   // Observação adicional
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css',
})
export class CheckoutComponent implements OnInit {

  // Injeção dos serviços via inject()
  private router         = inject(Router);
  private http           = inject(HttpClient);       // Para chamar o ViaCEP
  private auth           = inject(AuthService);
  private pedidoService  = inject(PedidoService);
  private usuarioService = inject(UsuarioService);
  private freteService   = inject(FreteService);
  readonly carrinho      = inject(CarrinhoService);

  // Signals de estado dos endereços
  enderecos           = signal<EnderecoApi[]>([]);
  enderecoSelecionado = signal<EnderecoApi | null>(null);
  carregandoEnderecos = signal(true);
  erroCheckout        = signal('');      // Erro ao criar o pedido
  pedidoCriado        = signal<PedidoApi | null>(null); // Pedido criado com sucesso

  // Signals do formulário de endereço
  mostrarFormEndereco = signal(false);  // Exibe/oculta o formulário de adicionar endereço
  salvandoEndereco    = signal(false);
  buscandoCep         = signal(false);  // true enquanto consulta o ViaCEP
  erroEndereco        = signal('');
  erroCep             = signal('');
  enderecoEditandoId  = signal<number | null>(null); // ID do endereço sendo editado (null = novo)
  novoEndereco        = { rua: '', cidade: '', estado: '', cep: '' };

  // Signals do formulário de telefone (editável no checkout)
  editandoTelefone  = signal(false);
  salvandoTelefone  = signal(false);
  novoTelefone      = '';

  // Fretes como array simples (não Signal) para compatibilidade com o template
  freteOpcoes: FreteApi[] = [];
  freteSelecionado = signal<FreteApi | null>(null);
  freteRetirada    = signal(false); // true = retirada na loja (frete grátis)

  // Opções de pagamento disponíveis
  readonly pagtoOpcoes: PagtoOpcao[] = [
    { id: 'cartao',   label: 'Cartão' },
    { id: 'pix',      label: 'PIX',      badge: '10% OFF' },   // PIX tem desconto de 10%
    { id: 'dinheiro', label: 'Dinheiro', obs: 'Apenas para retirada na loja' },
    { id: 'boleto',   label: 'Boleto' },
  ];

  // Tipo de pagamento selecionado (padrão: PIX)
  pagtoSelecionado = signal<PagtoId>('pix');

  // Etapa atual do checkout (1, 2 ou 3)
  step   = signal<1 | 2 | 3>(1);
  copiado = signal<boolean>(false); // Feedback visual ao copiar código PIX

  // Código PIX fictício para demonstração
  readonly codigoPix = 'DN4QSND1Z91H2S3HY84TH5JAD4J8NQDBU923O9BHJ3DC4JE3XLANSFS7PAHHDF29B3S4UHAUSD1PJ3HDJBFA2PB3DR023WJADN4XHSL8NKJES71JEB23Q3XKSB3U4HUGJ3KL3DJ';

  ngOnInit(): void {
    // Carrega os endereços do usuário logado
    this.usuarioService.listarEnderecos().subscribe({
      next: (lista) => {
        this.enderecos.set(lista);
        // Pré-seleciona o endereço principal, ou o primeiro, ou null
        const principal = lista.find(e => e.principal) ?? lista[0] ?? null;
        this.enderecoSelecionado.set(principal);
        this.carregandoEnderecos.set(false);
        // Se não tem endereços, abre o formulário automaticamente
        if (lista.length === 0) this.mostrarFormEndereco.set(true);
      },
      error: () => { this.carregandoEnderecos.set(false); this.mostrarFormEndereco.set(true); }
    });

    // Carrega as opções de frete da API
    this.freteService.listar().subscribe({
      next: (lista) => {
        this.freteOpcoes = lista;
        if (lista.length > 0) this.freteSelecionado.set(lista[0]); // Pré-seleciona o primeiro
      },
      error: () => {
        // Fallback com valores padrão caso a API de frete falhe
        this.freteOpcoes = [
          { id: 1, nome: 'Padrão',   prazoMinimo: 5, prazoMaximo: 7, preco: 29.90 },
          { id: 2, nome: 'Expresso', prazoMinimo: 1, prazoMaximo: 3, preco: 54.90 },
        ];
        this.freteSelecionado.set(this.freteOpcoes[0]);
      }
    });
  }

  // Getters que expõem dados do usuário e do carrinho de forma legível
  get usuario()    { return this.auth.currentUser(); }
  get subtotal()   { return this.carrinho.subtotal(); }
  get desconto()   { return this.carrinho.desconto(); }   // Desconto do cupom
  get totalItens() { return this.carrinho.totalItens(); }
  get itens()      { return this.carrinho.itens(); }

  // Valor do frete: 0 se retirada na loja, senão o preço da opção selecionada
  get freteValor(): number {
    if (this.freteRetirada()) return 0;
    return this.freteSelecionado()?.preco ?? 0;
  }

  // Desconto de 10% para pagamento PIX
  get descontoPix(): number {
    return this.pagtoSelecionado() === 'pix' ? this.subtotal * 0.1 : 0;
  }

  // Total final da compra: soma todas as parcelas e subtrai todos os descontos
  get totalFinal(): number {
    return this.subtotal + this.freteValor - this.desconto - this.descontoPix;
  }

  get descontoAtivo(): boolean { return this.descontoPix > 0; }

  // Retorna o rótulo de exibição do pagamento selecionado
  get labelPagto(): string {
    const map: Record<PagtoId, string> = {
      pix: 'PIX débito on-line', cartao: 'Cartão de crédito',
      dinheiro: 'Dinheiro na retirada', boleto: 'Boleto bancário',
    };
    return map[this.pagtoSelecionado()];
  }

  // Ações de seleção
  selecionarFrete(f: FreteApi)       { this.freteSelecionado.set(f); this.freteRetirada.set(false); }
  selecionarRetirada()               { this.freteRetirada.set(true); this.freteSelecionado.set(null); }
  selecionarPagto(id: PagtoId)       { this.pagtoSelecionado.set(id); }
  selecionarEndereco(e: EnderecoApi) { this.enderecoSelecionado.set(e); }

  // -------------------------------------------------------
  // salvarEndereco(): cria ou atualiza um endereço
  //
  // Se enderecoEditandoId não for null: remove o antigo e cria um novo.
  // Isso simula uma "atualização" já que o backend não tem PUT para endereços.
  // -------------------------------------------------------
  salvarEndereco(): void {
    const { rua, cidade, cep } = this.novoEndereco;
    const estado = this.novoEndereco.estado.toUpperCase(); // Estado sempre em maiúsculas (ex: "SP")
    if (!rua || !cidade || !estado || !cep) {
      this.erroEndereco.set('Preencha todos os campos do endereço.');
      return;
    }
    this.erroEndereco.set('');
    this.salvandoEndereco.set(true);

    const editandoId = this.enderecoEditandoId();
    if (editandoId !== null) {
      // Edição: remove o endereço antigo e cria um novo no lugar
      const antigo = this.enderecos().find(e => e.id === editandoId);
      const ePrincipal = antigo?.principal ?? false; // Mantém o flag "principal"
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
      // Criação: primeiro endereço é sempre o principal
      const ePrincipal = this.enderecos().length === 0;
      this.criarEndereco(rua, cidade, estado, cep, ePrincipal);
    }
  }

  // Método privado que chama a API para criar o endereço e atualiza a lista local
  private criarEndereco(rua: string, cidade: string, estado: string, cep: string, principal: boolean): void {
    this.usuarioService.adicionarEndereco({ rua, cidade, estado, cep, principal }).subscribe({
      next: (end) => {
        this.enderecos.update(l => [...l, end]); // Adiciona o novo endereço à lista local
        this.enderecoSelecionado.set(end);        // Pré-seleciona o endereço recém-criado
        this.mostrarFormEndereco.set(false);
        this.salvandoEndereco.set(false);
        this.enderecoEditandoId.set(null);
        this.novoEndereco = { rua: '', cidade: '', estado: '', cep: '' }; // Limpa o formulário
      },
      error: () => {
        this.erroEndereco.set('Erro ao salvar endereço. Tente novamente.');
        this.salvandoEndereco.set(false);
      }
    });
  }

  // Preenche o formulário com os dados do endereço para edição
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
        this.auth.patchUsuario({ telefone: tel }); // Atualiza o token em memória
        this.editandoTelefone.set(false);
        this.salvandoTelefone.set(false);
        this.novoTelefone = '';
      },
      error: () => { this.salvandoTelefone.set(false); }
    });
  }

  // -------------------------------------------------------
  // formatarCep(): aplica máscara "XXXXX-XXX" enquanto o usuário digita
  // e dispara busca automática quando completa 8 dígitos
  // -------------------------------------------------------
  formatarCep(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 8); // Remove não-dígitos, limita a 8
    if (v.length > 5) v = `${v.slice(0, 5)}-${v.slice(5)}`; // Formata: 01310-100
    this.novoEndereco.cep = v;
    input.value = v;
    this.erroCep.set('');
  }

  // -------------------------------------------------------
  // buscarCep(): consulta a API pública ViaCEP para autocompletar o endereço
  //
  // ViaCEP: serviço gratuito para consulta de CEPs brasileiros.
  // Retorna logradouro, localidade (cidade), uf (estado).
  // dados.erro: ViaCEP retorna { erro: true } para CEPs inválidos.
  // -------------------------------------------------------
  buscarCep(): void {
    const cep = this.novoEndereco.cep.replace(/\D/g, ''); // Remove o traço da máscara
    if (cep.length !== 8) return; // Só busca quando tiver 8 dígitos

    this.buscandoCep.set(true);
    this.erroCep.set('');

    this.http.get<ViaCepResponse>(`https://viacep.com.br/ws/${cep}/json/`).subscribe({
      next: (dados) => {
        this.buscandoCep.set(false);
        if (dados.erro) {
          this.erroCep.set('CEP não encontrado.');
          return;
        }
        // Preenche os campos somente se a API retornou os dados
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

  // Formata o prazo de entrega para exibição (ex: "5 – 7 dias")
  prazoLabel(f: FreteApi): string {
    return `${f.prazoMinimo} – ${f.prazoMaximo} dias`;
  }

  // -------------------------------------------------------
  // confirmar(): cria o pedido no backend (ação principal do checkout)
  //
  // 1. Valida endereço e itens no carrinho
  // 2. Monta a lista de produtoIds (um ID por unidade — ex: 2x produto 3 → [3, 3])
  //    flatMap + Array(quantidade).fill(id): expande [{ id:3, quantidade:2 }] → [3, 3]
  // 3. Chama PedidoService.criar()
  // 4. Se sucesso: avança para etapa 2 (confirmação de pagamento)
  // -------------------------------------------------------
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

    // Expande a lista de itens para um array de IDs (um por unidade)
    const produtoIds = this.itens.flatMap(item =>
      Array(item.quantidade).fill(item.id) as number[]
    );

    // undefined = não envia o campo (frete grátis na retirada; sem cupom)
    const freteId = this.freteRetirada() ? undefined : (this.freteSelecionado()?.id ?? undefined);
    const cupom   = this.carrinho.cupom() || undefined;

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
        this.step.set(2); // Avança para a etapa de confirmação de pagamento
        window.scrollTo({ top: 0, behavior: 'smooth' }); // Rola para o topo
      },
      error: (err) => {
        this.erroCheckout.set(err.error?.erro ?? 'Erro ao criar pedido. Tente novamente.');
      }
    });
  }

  // -------------------------------------------------------
  // finalizarPagamento(): confirma o pagamento e avança para a tela de sucesso
  // Limpa o carrinho local e no backend.
  // -------------------------------------------------------
  finalizarPagamento(): void {
    this.step.set(3); // Tela de sucesso
    this.carrinho.limparCarrinho(); // Esvazia o carrinho (local + backend)
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  voltarParaCheckout(): void {
    this.step.set(1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // Retorna o número do pedido criado (ou 0 se não houver)
  numeroPedido(): number { return this.pedidoCriado()?.id ?? 0; }

  get usuarioNome(): string     { return this.auth.currentUser()?.nome ?? ''; }
  get usuarioTelefone(): string { return this.auth.currentUser()?.telefone ?? ''; }

  // Formata o endereço selecionado como uma string legível
  get enderecoFormatado(): string {
    const e = this.enderecoSelecionado();
    if (!e) return 'Nenhum endereço cadastrado. Adicione um em seu perfil.';
    return `${e.rua}, ${e.cidade} – ${e.estado} – ${e.cep}`;
  }

  irParaHome(): void    { this.router.navigate(['/']); }
  irParaPedidos(): void { this.router.navigate(['/meus-pedidos']); }

  // -------------------------------------------------------
  // copiarCodigo(): copia o código PIX para a área de transferência
  //
  // navigator.clipboard.writeText(): API nativa do browser para cópiar texto.
  // .then(): executado após a cópia bem-sucedida.
  // Exibe feedback visual "copiado!" por 2,5 segundos.
  // -------------------------------------------------------
  copiarCodigo(): void {
    navigator.clipboard.writeText(this.codigoPix).then(() => {
      this.copiado.set(true);
      setTimeout(() => this.copiado.set(false), 2500); // Reseta após 2,5 segundos
    });
  }
}
