// ============================================================
// ARQUIVO: carrinho.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do CarrinhoComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Cada .spec.ts verifica se o componente correspondente funciona corretamente.
//
// COMO EXECUTAR: ng test
//
// NOTA: O CarrinhoComponent depende do CarrinhoService.
// Em testes mais avançados, o CarrinhoService seria substituído por um mock
// para testar o componente de forma isolada (sem chamar a API real).
//
// ESTRUTURA:
// - describe(): grupo de testes para o CarrinhoComponent
// - beforeEach(): prepara o ambiente antes de cada teste individual
// - it(): um teste que verifica um comportamento específico
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarrinhoComponent } from './carrinho.component';

describe('CarrinhoComponent', () => {
  let component: CarrinhoComponent;
  let fixture: ComponentFixture<CarrinhoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarrinhoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarrinhoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é instanciado sem erros
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
