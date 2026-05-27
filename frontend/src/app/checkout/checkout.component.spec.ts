// ============================================================
// ARQUIVO: checkout.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do CheckoutComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Cada .spec.ts verifica se o componente correspondente funciona corretamente.
//
// COMO EXECUTAR: ng test
//
// NOTA: O CheckoutComponent é um dos mais complexos da aplicação.
// Ele depende de: CarrinhoService, UsuarioService, PedidoService e HttpClient.
// Em testes completos, todos esses serviços precisariam ser mockados
// para isolar o componente e testar apenas sua lógica.
//
// ESTRUTURA:
// - describe(): grupo de testes
// - beforeEach(): configura o ambiente de teste antes de cada it()
// - it(): verifica que o componente é criado sem erros
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckoutComponent } from './checkout.component';

describe('CheckoutComponent', () => {
  let component: CheckoutComponent;
  let fixture: ComponentFixture<CheckoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CheckoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é criado sem erros de inicialização
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
