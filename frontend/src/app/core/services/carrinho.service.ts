// ============================================================
// ARQUIVO: carrinho.service.ts
// FUNÇÃO: Serviço reativo do carrinho de compras (estado local + sincronização com API).
//
// ARQUITETURA DE ESTADO REATIVO (Signals):
// O carrinho é mantido em memória usando Signals do Angular.
// Quando o usuário está logado, as operações também são sincronizadas
// com o backend via API. Se não estiver logado, funciona apenas localmente.
//
// SIGNALS USADOS:
// _itens:    lista dos itens no carrinho (privado — só alterado internamente)
// _cupom:    código do cupom aplicado
// _desconto: valor do desconto em reais
//
// COMPUTED SIGNALS (valores derivados automaticamente):
// subtotal:  soma de (preco * quantidade) de todos os itens — recalculado quando _itens muda
// totalItens: quantidade total de produtos — recalculado quando _itens muda
// total:     subtotal - desconto — recalculado automaticamente
//
// SINCRONIZAÇÃO COM BACKEND:
// As operações de add/remove/limpar tentam sincronizar com a API.
// O erro é silenciado ({ error: () => {} }) — o carrinho local não depende do backend.
//
// NOTA SOBRE aplicarCupomApi vs aplicarCupom:
// aplicarCupomApi(): valida o cupom no backend (recomendado)
// aplicarCupom(): fallback hardcoded para o cupom "GALERIA10" (legado)
// ============================================================

// Injectable: marca como serviço | computed: Signal derivado | signal: Signal reativo | inject: injeta serviços
import { Injectable, computed, signal, inject } from '@angular/core';
// HttpClient: faz requisições HTTP
import { HttpClient } from '@angular/common/http';
// URL base da API
import { environment } from '../../../environments/environment';
// AuthService: usado para verificar se o usuário está logado antes de sincronizar
import { AuthService } from './auth.service';

// Interface que representa um item no carrinho (produto + quantidade)
export interface ItemCarrinho {
  id: number;         // ID do produto
  nome: string;       // Nome do produto
  descricao: string;  // Descrição breve
  preco: number;      // Preço unitário
  imagem: string;     // URL da imagem
  quantidade: number; // Quantas unidades deste item
  tamanho?: string;   // Tamanho opcional (ex: P, M, G)
}

// @Injectable({ providedIn: 'root' }): instância única (Singleton) para toda a aplicação
@Injectable({ providedIn: 'root' })
export class CarrinhoService {

  // inject(): forma moderna de obter serviços sem construtor (funcional injection)
  private http        = inject(HttpClient);
  private authService = inject(AuthService);

  // Signals privados — só podem ser alterados dentro desta classe
  private _itens    = signal<ItemCarrinho[]>([]); // Lista de itens no carrinho
  private _cupom    = signal<string>('');          // Código do cupom aplicado (vazio = nenhum)
  private _desconto = signal<number>(0);           // Valor do desconto em reais

  // Versões somente-leitura dos Signals (componentes podem ler, mas não alterar)
  // .asReadonly(): garante que componentes externos não alterem o estado diretamente
  readonly itens    = this._itens.asReadonly();
  readonly cupom    = this._cupom.asReadonly();
  readonly desconto = this._desconto.asReadonly();

  // -------------------------------------------------------
  // Computed Signals: valores calculados automaticamente quando dependências mudam
  //
  // computed(): cria um Signal cujo valor é derivado de outros Signals.
  // Sempre que _itens ou _desconto mudarem, esses valores são recalculados.
  // -------------------------------------------------------

  // subtotal: soma de (preco * quantidade) de todos os itens
  // .reduce(acumulador, valorInicial): percorre a lista somando os valores
  readonly subtotal = computed(() =>
    this._itens().reduce((acc, i) => acc + i.preco * i.quantidade, 0)
  );

  // totalItens: conta a quantidade total de produtos no carrinho
  readonly totalItens = computed(() =>
    this._itens().reduce((acc, i) => acc + i.quantidade, 0)
  );

  // total: preço final = subtotal menos desconto do cupom
  readonly total = computed(() => this.subtotal() - this._desconto());

  // -------------------------------------------------------
  // adicionarItem(): adiciona um produto ao carrinho
  //
  // Omit<ItemCarrinho, 'quantidade'>: tipo sem o campo "quantidade"
  //   (a quantidade é passada separadamente, com padrão 1)
  //
  // _itens.update(): atualiza o Signal usando uma função que recebe o valor atual
  //
  // LÓGICA:
  //   Se o produto (mesmo ID e tamanho) já está no carrinho: incrementa a quantidade
  //   Se é novo: adiciona ao final da lista
  //   Spread (...): { ...i, quantidade: ... } cria uma cópia do objeto com o campo alterado
  //   [...itens, { ...item, quantidade }]: cria uma nova lista com o item novo no final
  // -------------------------------------------------------
  adicionarItem(item: Omit<ItemCarrinho, 'quantidade'>, quantidade = 1) {
    this._itens.update(itens => {
      const existente = itens.find(
        i => i.id === item.id && i.tamanho === item.tamanho // Verifica se o mesmo produto/tamanho já existe
      );
      if (existente) {
        // Produto já no carrinho: incrementa a quantidade sem duplicar
        return itens.map(i =>
          i === existente ? { ...i, quantidade: i.quantidade + quantidade } : i
        );
      }
      // Produto novo: adiciona ao final da lista
      return [...itens, { ...item, quantidade }];
    });

    // Sincroniza com backend apenas se o usuário estiver logado
    if (this.authService.currentUser()) {
      this.http.post(`${environment.apiUrl}/carrinho/itens`, {
        produtoId: item.id,
        quantidade
      }).subscribe({ error: () => {} }); // Ignora erros silenciosamente
    }
  }

  // -------------------------------------------------------
  // removerItem(): remove um produto do carrinho pelo ID e tamanho
  //
  // .filter(): mantém apenas os itens que NÃO correspondem ao removido
  // !(condição): inverte — "mantenha os que NÃO são o item a remover"
  // -------------------------------------------------------
  removerItem(id: number, tamanho?: string) {
    this._itens.update(itens =>
      itens.filter(i => !(i.id === id && i.tamanho === tamanho)) // Remove o item correspondente
    );

    // Sincroniza remoção com o backend se logado
    if (this.authService.currentUser()) {
      this.http.delete(`${environment.apiUrl}/carrinho/itens/${id}`)
        .subscribe({ error: () => {} });
    }
  }

  // Incrementa a quantidade de um item específico em +1
  aumentarQtd(id: number, tamanho?: string) {
    this._itens.update(itens =>
      itens.map(i =>
        i.id === id && i.tamanho === tamanho
          ? { ...i, quantidade: i.quantidade + 1 } // Cópia com quantidade incrementada
          : i // Outros itens permanecem iguais
      )
    );
  }

  // -------------------------------------------------------
  // diminuirQtd(): decrementa a quantidade em -1
  //
  // Se a quantidade chegar a 0 após decrementar, o .filter() remove o item.
  // Isso garante que não ficam itens com quantidade 0 no carrinho.
  // -------------------------------------------------------
  diminuirQtd(id: number, tamanho?: string) {
    this._itens.update(itens =>
      itens
        .map(i =>
          i.id === id && i.tamanho === tamanho
            ? { ...i, quantidade: i.quantidade - 1 } // Decrementa
            : i
        )
        .filter(i => i.quantidade > 0) // Remove itens com quantidade <= 0
    );
  }

  // -------------------------------------------------------
  // aplicarCupomApi(): valida o cupom no backend
  // Retorna Promise<boolean>: true se válido, false se inválido.
  //
  // Promise: similar ao Observable, mas mais simples — um único valor futuro.
  // new Promise(resolve => ...): cria uma Promise que resolve quando a API responder.
  // resolve(true/false): marca a Promise como concluída com o valor informado.
  //
  // Fallback: se a API falhar, verifica o cupom "GALERIA10" localmente.
  // -------------------------------------------------------
  /** Valida cupom no backend */
  aplicarCupomApi(codigo: string): Promise<boolean> {
    return new Promise(resolve => {
      this.http.post<{ desconto: number; tipo: string }>(
        `${environment.apiUrl}/cupons/validar`,
        { codigo, subtotal: this.subtotal() }
      ).subscribe({
        next: (res) => {
          // Calcula o desconto baseado no tipo: percentual ou fixo
          const desconto = res.tipo === 'percentual'
            ? this.subtotal() * (res.desconto / 100) // Ex: 10% de R$200 = R$20
            : res.desconto;                          // Ex: R$20 fixo
          this._cupom.set(codigo.toUpperCase());
          this._desconto.set(desconto);
          resolve(true); // Cupom válido
        },
        error: () => {
          // Fallback: cupom hardcoded para compatibilidade com código legado
          const upper = codigo.trim().toUpperCase();
          if (upper === 'GALERIA10') {
            this._cupom.set(upper);
            this._desconto.set(this.subtotal() * 0.1); // 10% de desconto
            resolve(true);
          } else {
            this._cupom.set('');
            this._desconto.set(0);
            resolve(false); // Cupom inválido
          }
        }
      });
    });
  }

  /** Mantido para compatibilidade com código legado */
  aplicarCupom(cupom: string): boolean {
    const upper = cupom.trim().toUpperCase();
    if (upper === 'GALERIA10') {
      this._cupom.set(upper);
      this._desconto.set(this.subtotal() * 0.1);
      return true;
    }
    this._cupom.set('');
    this._desconto.set(0);
    return false;
  }

  // Remove o cupom aplicado e zera o desconto
  limparCupom() {
    this._cupom.set('');
    this._desconto.set(0);
  }

  // -------------------------------------------------------
  // limparCarrinho(): remove todos os itens e reseta o estado
  // Chamado após a finalização do pedido no checkout.
  // -------------------------------------------------------
  limparCarrinho() {
    this._itens.set([]);    // Esvazia a lista
    this._cupom.set('');    // Remove o cupom
    this._desconto.set(0);  // Zera o desconto

    // Sincroniza com o backend se o usuário estiver logado
    if (this.authService.currentUser()) {
      this.http.delete(`${environment.apiUrl}/carrinho`)
        .subscribe({ error: () => {} });
    }
  }
}
