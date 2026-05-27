// ============================================================
// ARQUIVO: header.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do HeaderComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Cada .spec.ts verifica se o componente funciona corretamente de forma automatizada.
//
// COMO EXECUTAR: ng test
//
// NOTA: O HeaderComponent usa CarrinhoService e AuthService (via Signals).
// Em testes completos, esses serviços seriam mockados para verificar:
// - Se o total de itens no carrinho aparece corretamente no header
// - Se o nome do usuário logado é exibido
// - Se o dropdown abre e fecha corretamente
//
// ESTRUTURA:
// - describe(): grupo de testes do HeaderComponent
// - beforeEach(): configura o ambiente antes de cada teste
// - it(): define um caso de teste individual
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é instanciado sem erros
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
