// ============================================================
// ARQUIVO: sobre.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do SobreComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático gerados pelo Angular CLI.
// O SobreComponent é uma página estática (sem lógica ou chamadas à API),
// então o teste básico de criação é suficiente para garantir que não há erros.
//
// COMO EXECUTAR: ng test
//
// ESTRUTURA:
// - describe(): grupo de testes do SobreComponent
// - beforeEach(): configura o ambiente de teste
// - it(): teste que verifica se o componente é criado sem erros
// ============================================================

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SobreComponent } from './sobre.component';

describe('SobreComponent', () => {
  let component: SobreComponent;
  let fixture: ComponentFixture<SobreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SobreComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SobreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Teste básico: verifica se o componente estático é criado sem erros
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
