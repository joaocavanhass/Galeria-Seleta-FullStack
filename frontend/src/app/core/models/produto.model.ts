import { ImagemProduto } from './imagem-produto.model';

export interface Produto {
  id: number;
  categoria_id: number;
  nome: string;
  descricao: string | null;
  preco: number;
  preco_desconto: number | null;
  estoque: number;
  status: 'ativo' | 'inativo' | 'rascunho';
  criado_em: string;
  imagens?: ImagemProduto[];
  imagem_url?: string;
  categoria?: string;
}
