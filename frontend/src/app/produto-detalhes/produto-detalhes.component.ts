import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Produto } from '../core/models/produto.model';
import { PRODUTOS_MOCK } from '../core/mocks/produtos.mock';
import { CarrinhoService } from '../core/services/carrinho.service';

@Component({
  selector: 'app-produto-detalhes',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, RouterLink],
  templateUrl: './produto-detalhes.component.html',
  styleUrl: './produto-detalhes.component.css',
})
export class ProdutoDetalhesComponent implements OnInit {
  produto: Produto | null = null;
  imagemSelecionada = 0;
  tamanhoSelecionado: string | null = null;
  quantidade = 1;
  adicionado = false;

  readonly tamanhos = ['PP', 'P', 'M', 'G', 'GG'];

  private route    = inject(ActivatedRoute);
  private router   = inject(Router);
  private carrinho = inject(CarrinhoService);

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.produto = PRODUTOS_MOCK.find(p => p.id === id) ?? null;
    if (!this.produto) this.router.navigate(['/produtos']);
  }

  get imagemAtual(): string {
    if (!this.produto) return '';
    if (this.produto.imagens?.length) {
      return this.produto.imagens[this.imagemSelecionada]?.url ?? '';
    }
    return this.produto.imagem_url ?? '';
  }

  get precoFinal(): number {
    if (!this.produto) return 0;
    return this.produto.preco_desconto ?? this.produto.preco;
  }

  get temDesconto(): boolean {
    if (!this.produto) return false;
    return !!this.produto.preco_desconto && this.produto.preco_desconto < this.produto.preco;
  }

  selecionarTamanho(t: string) { this.tamanhoSelecionado = t; }
  diminuirQtd() { if (this.quantidade > 1) this.quantidade--; }
  aumentarQtd() { if (this.quantidade < 10) this.quantidade++; }

  adicionarAoCarrinho() {
    if (!this.produto) return;
    this.carrinho.adicionarItem({
      id:        this.produto.id,
      nome:      this.produto.nome,
      descricao: this.produto.descricao ?? '',
      preco:     this.precoFinal,
      imagem:    this.imagemAtual,
      tamanho:   this.tamanhoSelecionado ?? undefined,
    }, this.quantidade);

    this.adicionado = true;
    setTimeout(() => (this.adicionado = false), 2000);
  }

  comprar() {
    if (!this.produto) return;
    this.carrinho.adicionarItem({
      id:        this.produto.id,
      nome:      this.produto.nome,
      descricao: this.produto.descricao ?? '',
      preco:     this.precoFinal,
      imagem:    this.imagemAtual,
      tamanho:   this.tamanhoSelecionado ?? undefined,
    }, this.quantidade);
    this.router.navigate(['/carrinho']);
  }
}