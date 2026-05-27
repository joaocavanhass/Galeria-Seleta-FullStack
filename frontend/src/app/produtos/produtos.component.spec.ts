// ============================================================
// ARQUIVO: produtos.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do ProdutosComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Cada .spec.ts verifica se o componente funciona corretamente de forma automatizada.
//
// COMO EXECUTAR: ng test
//
// NOTA: O ProdutosComponent usa ProdutoService e CategoriaService para buscar dados.
// Também usa @HostListener para detectar cliques e fechar dropdowns.
// Em testes completos, os serviços seriam mockados para verificar:
// - Se a lista de produtos é exibida
// - Se o filtro por categoria funciona
// - Se o dropdown fecha ao clicar fora
//
// ESTRUTURA:
// - describe(): grupo de testes do ProdutosComponent
// - beforeEach(): configura o ambiente antes de cada teste
// - it(): define um caso de teste individual
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProdutosComponent } from './produtos.component';

describe('ProdutosComponent', () => {
  let component: ProdutosComponent;
  let fixture: ComponentFixture<ProdutosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProdutosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProdutosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é instanciado sem erros
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
