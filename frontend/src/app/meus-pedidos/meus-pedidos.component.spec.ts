// ============================================================
// ARQUIVO: meus-pedidos.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do MeusPedidosComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Cada .spec.ts verifica se o componente funciona corretamente.
//
// COMO EXECUTAR: ng test
//
// NOTA: O MeusPedidosComponent usa PedidoService para buscar os pedidos do usuário.
// Em testes completos, o PedidoService seria mockado para retornar dados fictícios
// e verificar se a lista de pedidos é exibida corretamente.
//
// ESTRUTURA:
// - describe(): grupo de testes do MeusPedidosComponent
// - beforeEach(): configura o ambiente antes de cada teste
// - it(): define um caso de teste individual
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeusPedidosComponent } from './meus-pedidos.component';

describe('MeusPedidosComponent', () => {
  let component: MeusPedidosComponent;
  let fixture: ComponentFixture<MeusPedidosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeusPedidosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MeusPedidosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é criado sem erros
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
