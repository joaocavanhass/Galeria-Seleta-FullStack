// ============================================================
// ARQUIVO: footer.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do FooterComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// Cada .spec.ts garante que o componente correspondente não quebra ao ser criado.
//
// COMO EXECUTAR: ng test
//
// NOTA: O FooterComponent é um componente estático com formulário de newsletter.
// Testes avançados verificariam se o envio do email funciona corretamente.
//
// ESTRUTURA:
// - describe(): grupo de testes do FooterComponent
// - beforeEach(): prepara o ambiente de teste
// - it(): teste individual
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterComponent } from './footer.component';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente é instanciado sem erros
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
