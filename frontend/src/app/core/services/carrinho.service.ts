import { Injectable, computed, signal } from '@angular/core';

export interface ItemCarrinho {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  imagem: string;
  quantidade: number;
  tamanho?: string;
}

@Injectable({ providedIn: 'root' })
export class CarrinhoService {

  private _itens = signal<ItemCarrinho[]>([]);
  private _cupom   = signal<string>('');
  private _desconto = signal<number>(0);

  readonly itens    = this._itens.asReadonly();
  readonly cupom    = this._cupom.asReadonly();
  readonly desconto = this._desconto.asReadonly();

  readonly subtotal = computed(() =>
    this._itens().reduce((acc, i) => acc + i.preco * i.quantidade, 0)
  );

  readonly totalItens = computed(() =>
    this._itens().reduce((acc, i) => acc + i.quantidade, 0)
  );

  readonly total = computed(() => this.subtotal() - this._desconto());

  adicionarItem(item: Omit<ItemCarrinho, 'quantidade'>, quantidade = 1) {
    this._itens.update(itens => {
      const existente = itens.find(
        i => i.id === item.id && i.tamanho === item.tamanho
      );
      if (existente) {
        return itens.map(i =>
          i === existente ? { ...i, quantidade: i.quantidade + quantidade } : i
        );
      }
      return [...itens, { ...item, quantidade }];
    });
  }

  removerItem(id: number, tamanho?: string) {
    this._itens.update(itens =>
      itens.filter(i => !(i.id === id && i.tamanho === tamanho))
    );
  }

  aumentarQtd(id: number, tamanho?: string) {
    this._itens.update(itens =>
      itens.map(i =>
        i.id === id && i.tamanho === tamanho
          ? { ...i, quantidade: i.quantidade + 1 }
          : i
      )
    );
  }

  diminuirQtd(id: number, tamanho?: string) {
    this._itens.update(itens =>
      itens
        .map(i =>
          i.id === id && i.tamanho === tamanho
            ? { ...i, quantidade: i.quantidade - 1 }
            : i
        )
        .filter(i => i.quantidade > 0)
    );
  }

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

  limparCupom() {
    this._cupom.set('');
    this._desconto.set(0);
  }

  limparCarrinho() {
    this._itens.set([]);
    this._cupom.set('');
    this._desconto.set(0);
  }
}