import { Produto } from './produto.model';

export interface ProdutoDetalhe extends Produto {
  historico: string;
  tamanhos: string[];
  cor: string;
  marca?: string;
  condicao: 'excelente' | 'muito bom' | 'bom';
}
