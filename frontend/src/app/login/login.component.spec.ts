// ============================================================
// ARQUIVO: login.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do LoginComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Verificam se o componente é criado corretamente e funciona como esperado.
//
// COMO EXECUTAR: ng test
//
// NOTA: O LoginComponent usa AuthService e Router — em testes mais avançados,
// esses serviços seriam substituídos por "mocks" (versões falsas controladas)
// para testar sem depender da API real.
//
// ESTRUTURA:
// - describe(): grupo de testes
// - beforeEach(): configuração antes de cada teste
// - it(): um teste individual
// - expect().toBeTruthy(): verifica que o valor não é null/undefined/false
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é instanciado sem erros
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
