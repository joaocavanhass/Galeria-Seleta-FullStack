// ============================================================
// ARQUIVO: categorias.mock.ts
// FUNÇÃO: Dados fictícios (mock) de categorias para uso em desenvolvimento.
//
// O QUE SÃO MOCKS?
// Mocks são dados falsos que imitam o formato dos dados reais.
// Eles são usados quando:
//   - A API ainda não está pronta
//   - Queremos testar o frontend sem depender do backend
//   - Escrevemos testes unitários (sem fazer chamadas reais à API)
//
// COMO USAR:
// Em vez de chamar categoriaService.listar() (que vai à API),
// podemos temporariamente usar CATEGORIAS_MOCK para ver como
// a tela se comporta com esses dados.
//
// ATENÇÃO: Este arquivo NÃO é usado em produção.
// Em produção, os dados vêm sempre da API real.
//
// CONEXÕES: pode ser importado por componentes ou testes durante desenvolvimento.
// ============================================================

// Importa a interface Categoria para garantir que os dados seguem o formato correto
import { Categoria } from '../models/categoria.model';

// Lista de categorias fictícias.
// "export const" significa que este array pode ser importado em outros arquivos.
// "CATEGORIAS_MOCK" em maiúsculas é uma convenção para constantes (dados que não mudam).
export const CATEGORIAS_MOCK: Categoria[] = [
  // Cada objeto representa uma categoria com os campos definidos na interface Categoria:
  // id: identificador único | cat_pai_id: categoria pai (null = categoria raiz) |
  // nome: nome exibido | slug: versão da URL | descricao: texto descritivo | ativo: visível?
  { id: 1, cat_pai_id: null, nome: 'Calças',    slug: 'calcas',    descricao: 'Calças e jeans',         ativo: true },
  { id: 2, cat_pai_id: null, nome: 'Camisas',   slug: 'camisas',   descricao: 'Camisas sociais',        ativo: true },
  { id: 3, cat_pai_id: null, nome: 'Casacos',   slug: 'casacos',   descricao: 'Casacos e jaquetas',     ativo: true },
  { id: 4, cat_pai_id: null, nome: 'Moletons',  slug: 'moletons',  descricao: 'Moletons e hoodies',     ativo: true },
  { id: 5, cat_pai_id: null, nome: 'Vestidos',  slug: 'vestidos',  descricao: 'Vestidos e saias',       ativo: true },
  { id: 6, cat_pai_id: null, nome: 'Camisetas', slug: 'camisetas', descricao: 'Camisetas e básicos',    ativo: true },
];
