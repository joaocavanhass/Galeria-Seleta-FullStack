// ============================================================
// ARQUIVO: app.component.spec.ts
// FUNÇÃO: Arquivo de testes unitários do AppComponent.
//
// O QUE SÃO ARQUIVOS .spec.ts?
// São arquivos de teste automático. ".spec" vem de "specification" (especificação).
// Cada teste verifica se uma parte do código funciona como esperado.
// No Angular, os testes são escritos com Jasmine (a linguagem de testes)
// e executados pelo Karma (o executor de testes no navegador).
//
// COMO EXECUTAR OS TESTES:
//   ng test    → abre o Karma e executa todos os testes
//
// ESTRUTURA DE UM TESTE:
// - describe('Nome', () => { ... }): agrupa testes relacionados (suite de testes)
// - beforeEach(async () => { ... }): roda ANTES de cada teste (configuração)
// - it('deve fazer X', () => { ... }): um teste individual
// - expect(valor).toEqual('esperado'): verifica se o resultado é o esperado
//
// TestBed: o ambiente de teste do Angular — simula o módulo Angular para os testes.
// fixture: representa a instância do componente renderizado no ambiente de teste.
// component: o objeto TypeScript da instância do componente.
//
// ATENÇÃO: Este arquivo foi gerado automaticamente pelo Angular CLI.
// Estes são testes básicos de criação — a maioria dos projetos substitui
// esses testes por testes mais específicos conforme o projeto cresce.
// ============================================================

// TestBed: utilitário principal para configurar o ambiente de teste Angular
import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';

// describe(): agrupa todos os testes do AppComponent
describe('AppComponent', () => {

  // beforeEach(): executado antes de CADA teste (it()) abaixo
  // Configura o módulo de teste com o AppComponent
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent], // Standalone component: importado diretamente
    }).compileComponents();   // compileComponents(): compila os templates HTML
  });

  // Teste 1: verifica se o componente é criado sem erros
  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance; // Obtém a instância TypeScript do componente
    expect(app).toBeTruthy(); // toBeTruthy(): verifica que o valor não é null/undefined/false
  });

  // Teste 2: verifica se o título do app é 'galeria-seleta'
  it(`should have the 'galeria-seleta' title`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('galeria-seleta'); // toEqual(): compara o valor exato
  });

  // Teste 3: verifica se o título é renderizado no HTML
  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges(); // detectChanges(): aplica o data binding (como o ngOnInit)
    const compiled = fixture.nativeElement as HTMLElement; // Acessa o DOM renderizado
    expect(compiled.querySelector('h1')?.textContent).toContain('Hello, galeria-seleta');
  });
});
