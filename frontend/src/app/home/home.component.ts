// ============================================================
// ARQUIVO: home.component.ts
// FUNÇÃO: Componente da página inicial (/).
//
// RESPONSABILIDADES:
// - Exibir o carrossel de slides do hero banner
// - Buscar e exibir produtos marcados como novidade (faixa marquee)
//
// CARROSSEL:
// Autoplay a cada 4 segundos usando setInterval().
// O timer é limpo no ngOnDestroy() para evitar memory leak
// (o timer continuaria rodando mesmo após sair da página).
//
// MARQUEE:
// produtosMarquee duplica a lista de novidades para criar
// o efeito de scroll infinito (a cópia começa quando a original termina).
//
// LIFECYCLE HOOKS:
// ngOnInit(): executa quando o componente é criado — busca as novidades
// ngOnDestroy(): executa quando o componente é destruído — para o timer
//
// VERIFICAÇÃO isPlatformBrowser:
// setInterval() não existe no servidor (SSR). A verificação garante
// que o autoplay só inicia no navegador.
// ============================================================

import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
// isPlatformBrowser: verifica se está rodando no browser (não no servidor SSR)
import { isPlatformBrowser, CommonModule } from '@angular/common';
// RouterLink: links de navegação para a página de detalhes
import { RouterLink } from '@angular/router';
import { Produto } from '../core/models/produto.model';
import { ProdutoService } from '../core/services/produto.service';

// Interface de cada slide do hero banner
export interface SlideHero {
  id: number;
  url: string; // URL da imagem
  alt: string; // Texto alternativo para acessibilidade
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
// implements OnInit, OnDestroy: contrato que exige implementar ngOnInit() e ngOnDestroy()
export class HomeComponent implements OnInit, OnDestroy {

  constructor(
    @Inject(PLATFORM_ID) private platformId: object, // 'browser' ou 'server'
    private produtoService: ProdutoService
  ) {}

  // Array de slides para o hero banner
  slides: SlideHero[] = [
    { id: 1, url: 'https://images.unsplash.com/photo-1595175131388-65cd0200facb?auto=format&fit=crop&w=800&q=80', alt: 'Coleção primavera' },
    { id: 2, url: 'https://images.unsplash.com/photo-1590664325935-68aba5254108?auto=format&fit=crop&w=800&q=80', alt: 'Peças exclusivas' },
    { id: 3, url: 'https://images.unsplash.com/photo-1577959806854-375d1bed6c93?auto=format&fit=crop&w=800&q=80', alt: 'Novidades da semana' },
  ];

  currentSlide = 0; // Índice do slide atualmente visível

  // ReturnType<typeof setInterval>: tipo correto para armazenar o ID do timer
  // null = timer não está rodando
  private autoplayTimer: ReturnType<typeof setInterval> | null = null;

  produtosNovidades: Produto[] = []; // Lista de produtos marcados como novidade

  // -------------------------------------------------------
  // produtosMarquee: duplica a lista para o efeito de scroll infinito
  // Ex: [A, B, C] → [A, B, C, A, B, C]
  // Quando a animação CSS chega ao final da segunda cópia,
  // reinicia do início criando a ilusão de movimento contínuo.
  // -------------------------------------------------------
  get produtosMarquee(): Produto[] {
    return [...this.produtosNovidades, ...this.produtosNovidades];
  }

  ngOnInit(): void {
    // Busca os produtos novidade da API (máximo 8 itens)
    this.produtoService.novidades().subscribe({
      next: (lista) => { this.produtosNovidades = lista.slice(0, 8); },
      error: () => { this.produtosNovidades = []; } // Silencia erros — marquee fica vazia
    });

    // Inicia o autoplay apenas no browser (não no servidor SSR)
    if (isPlatformBrowser(this.platformId)) {
      this.iniciarAutoplay();
    }
  }

  // ngOnDestroy(): chamado quando o usuário navega para outra página
  // IMPORTANTE: limpar o timer aqui evita memory leak
  ngOnDestroy(): void {
    this.pararAutoplay();
  }

  // setInterval: executa a função a cada 4000ms (4 segundos)
  iniciarAutoplay(): void {
    this.autoplayTimer = setInterval(() => this.proximo(), 4000);
  }

  // clearInterval: cancela o timer para liberar memória
  pararAutoplay(): void {
    if (this.autoplayTimer) {
      clearInterval(this.autoplayTimer);
      this.autoplayTimer = null;
    }
  }

  // Avança para o próximo slide (com loop — vai para 0 após o último)
  // % this.slides.length: operação de módulo garante que não ultrapassa o último slide
  proximo(): void  { this.currentSlide = (this.currentSlide + 1) % this.slides.length; }

  // Volta para o slide anterior (com loop — vai para o último após o 0)
  anterior(): void { this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length; }

  // Navega diretamente para um slide pelo índice (clique nos pontinhos)
  irPara(index: number): void { this.currentSlide = index; }

  // Retorna o preço final (com desconto se houver, ou preço normal)
  // "??" (nullish coalescing): usa preco se preco_desconto for null/undefined
  precoFinal(p: Produto): number  { return p.preco_desconto ?? p.preco; }

  // Retorna true se o produto tem desconto ativo (preco_desconto < preco)
  temDesconto(p: Produto): boolean { return p.preco_desconto !== null && p.preco_desconto! < p.preco; }
}
