import { Categoria } from '../models/categoria.model';

export const CATEGORIAS_MOCK: Categoria[] = [
  { id: 1, cat_pai_id: null, nome: 'Calças',    slug: 'calcas',    descricao: 'Calças e jeans',         ativo: true },
  { id: 2, cat_pai_id: null, nome: 'Camisas',   slug: 'camisas',   descricao: 'Camisas sociais',        ativo: true },
  { id: 3, cat_pai_id: null, nome: 'Casacos',   slug: 'casacos',   descricao: 'Casacos e jaquetas',     ativo: true },
  { id: 4, cat_pai_id: null, nome: 'Moletons',  slug: 'moletons',  descricao: 'Moletons e hoodies',     ativo: true },
  { id: 5, cat_pai_id: null, nome: 'Vestidos',  slug: 'vestidos',  descricao: 'Vestidos e saias',       ativo: true },
  { id: 6, cat_pai_id: null, nome: 'Camisetas', slug: 'camisetas', descricao: 'Camisetas e básicos',    ativo: true },
];
