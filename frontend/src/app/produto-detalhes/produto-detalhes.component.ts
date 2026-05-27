// ============================================================
// ARQUIVO: produto-detalhes.component.ts
// FUNÇÃO: Componente da página de detalhes de um produto (/produtos/:id).
//
// RESPONSABILIDADES:
// - Ler o parâmetro ":id" da URL (ActivatedRoute)
// - Buscar os dados completos do produto na API
// - Galeria de imagens com troca ao clicar
// - Seleção de tamanho e quantidade
// - Adicionar ao carrinho ou comprar diretamente
//
// PARÂMETRO DE ROTA:
// this.route.snapshot.paramMap.get('id'):
//   - snapshot: leitura direta (sem Observable) — adequado pois o componente
//     é destruído ao navegar para outro produto
//   - paramMap.get('id'): lê o :id da URL (ex: /produtos/42 → '42')
//   - Number(...): converte a string para número
//
// FEEDBACK "ADICIONADO":
// Após adicionar ao carrinho, exibe confirmação visual por 2 segundos
// usando setTimeout. O timeout é curto o suficiente para não precisar
// de limpeza no ngOnDestroy.
// ============================================================

import { Component, OnInit, inject } from '@angular/core';
// CommonModule: *ngIf, *ngFor | CurrencyPipe: formata moeda
import { CommonModule, CurrencyPipe } from '@angular/common';
// ActivatedRoute: acessa parâmetros da rota atual | Router: navegação programática
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Produto } from '../core/models/produto.model';
import { ProdutoService } from '../core/services/produto.service';
import { CarrinhoService } from '../core/services/carrinho.service';

@Component({
  selector: 'app-produto-detalhes',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, RouterLink],
  templateUrl: './produto-detalhes.component.html',
  styleUrl: './produto-detalhes.component.css',
})
export class ProdutoDetalhesComponent implements OnInit {

  produto: Produto | null = null; // null = ainda carregando ou não encontrado
  imagemSelecionada  = 0;         // Índice da imagem ativa na galeria
  tamanhoSelecionado: string | null = null; // Tamanho selecionado pelo usuário
  quantidade         = 1;          // Quantidade a adicionar ao carrinho
  adicionado         = false;       // true por 2s após adicionar ao carrinho
  carregando         = true;

  // Lista fixa de tamanhos disponíveis
  readonly tamanhos = ['PP', 'P', 'M', 'G', 'GG'];

  // inject(): obtém os serviços necessários
  private route          = inject(ActivatedRoute);  // Acessa os parâmetros da URL
  private router         = inject(Router);           // Para redirecionar se produto não existir
  private carrinho       = inject(CarrinhoService);
  private produtoService = inject(ProdutoService);

  ngOnInit() {
    // Lê o :id da URL e converte para número
    // /produtos/42 → paramMap.get('id') = '42' → Number('42') = 42
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.produtoService.buscarPorId(id).subscribe({
      next: (p) => {
        this.produto    = p;
        this.carregando = false;
      },
      error: () => {
        // Produto não encontrado: redireciona para a listagem
        this.router.navigate(['/produtos']);
      }
    });
  }

  // Retorna a URL da imagem atualmente selecionada na galeria
  get imagemAtual(): string {
    if (!this.produto) return '';
    if (this.produto.imagens?.length) {
      // Usa a galeria de imagens se disponível
      return this.produto.imagens[this.imagemSelecionada]?.url ?? '';
    }
    // Fallback: usa a imagem_url calculada pelo serviço
    return this.produto.imagem_url ?? '';
  }

  // Retorna o preço final (desconto tem prioridade sobre o preço normal)
  get precoFinal(): number {
    if (!this.produto) return 0;
    return this.produto.preco_desconto ?? this.produto.preco;
  }

  // true se o produto tem preço de desconto válido (menor que o preço normal)
  get temDesconto(): boolean {
    if (!this.produto) return false;
    return !!this.produto.preco_desconto && this.produto.preco_desconto < this.produto.preco;
  }

  selecionarTamanho(t: string) { this.tamanhoSelecionado = t; }

  // Controle de quantidade (mínimo 1, máximo 10)
  diminuirQtd() { if (this.quantidade > 1)  this.quantidade--; }
  aumentarQtd() { if (this.quantidade < 10) this.quantidade++; }

  // Adiciona o produto ao carrinho e exibe confirmação visual por 2 segundos
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
    // setTimeout: agenda o reset da flag após 2000ms (2 segundos)
    setTimeout(() => (this.adicionado = false), 2000);
  }

  // Adiciona ao carrinho e navega imediatamente para /carrinho
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
