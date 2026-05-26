import { Injectable, computed, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

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

  private http        = inject(HttpClient);
  private authService = inject(AuthService);

  private _itens    = signal<ItemCarrinho[]>([]);
  private _cupom    = signal<string>('');
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

    // Sincroniza com backend se logado
    if (this.authService.currentUser()) {
      this.http.post(`${environment.apiUrl}/carrinho/itens`, {
        produtoId: item.id,
        quantidade
      }).subscribe({ error: () => {} });
    }
  }

  removerItem(id: number, tamanho?: string) {
    this._itens.update(itens =>
      itens.filter(i => !(i.id === id && i.tamanho === tamanho))
    );

    if (this.authService.currentUser()) {
      this.http.delete(`${environment.apiUrl}/carrinho/itens/${id}`)
        .subscribe({ error: () => {} });
    }
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

  /** Valida cupom no backend */
  aplicarCupomApi(codigo: string): Promise<boolean> {
    return new Promise(resolve => {
      this.http.post<{ desconto: number; tipo: string }>(
        `${environment.apiUrl}/cupons/validar`,
        { codigo, subtotal: this.subtotal() }
      ).subscribe({
        next: (res) => {
          const desconto = res.tipo === 'percentual'
            ? this.subtotal() * (res.desconto / 100)
            : res.desconto;
          this._cupom.set(codigo.toUpperCase());
          this._desconto.set(desconto);
          resolve(true);
        },
        error: () => {
          // Fallback: cupom hardcoded para compatibilidade
          const upper = codigo.trim().toUpperCase();
          if (upper === 'GALERIA10') {
            this._cupom.set(upper);
            this._desconto.set(this.subtotal() * 0.1);
            resolve(true);
          } else {
            this._cupom.set('');
            this._desconto.set(0);
            resolve(false);
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

  limparCupom() {
    this._cupom.set('');
    this._desconto.set(0);
  }

  limparCarrinho() {
    this._itens.set([]);
    this._cupom.set('');
    this._desconto.set(0);

    if (this.authService.currentUser()) {
      this.http.delete(`${environment.apiUrl}/carrinho`)
        .subscribe({ error: () => {} });
    }
  }
}