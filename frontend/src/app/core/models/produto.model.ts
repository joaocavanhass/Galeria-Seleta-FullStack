// ============================================================
// ARQUIVO: produto.model.ts
// FUNÇÃO: Define a estrutura de um produto no frontend.
//
// MAPEAMENTO BACKEND → FRONTEND:
// O backend usa nomes em camelCase (ex: criadoEm, categoriaId).
// O frontend usa snake_case (ex: criado_em, categoria_id).
// O ProdutoService faz esse mapeamento na função mapProduto().
//
// CAMPOS OPCIONAIS (com "?"):
// O "?" indica que o campo pode estar ausente no objeto.
//   Ex: imagens?: ImagemProduto[] significa que um Produto pode
//   existir sem ter imagens (array vazio seria diferente de ausente).
//
// CAMPO "status":
// O backend usa "ativo"/"inativo"; internamente o frontend tem
// um terceiro valor possível "rascunho" para produtos em criação.
// ============================================================

// Importa a interface ImagemProduto para usá-la no campo imagens[]
import { ImagemProduto } from './imagem-produto.model';

export interface Produto {
  id: number;                        // Identificador único do produto
  categoria_id: number;              // ID da categoria à qual pertence
  nome: string;                      // Nome do produto (ex: "Bolsa de Couro")
  descricao: string | null;          // Descrição detalhada (pode ser nula)
  preco: number;                     // Preço atual em reais
  preco_desconto: number | null;     // Preço com desconto (null = sem desconto)
  estoque: number;                   // Quantidade disponível em estoque
  status: 'ativo' | 'inativo' | 'rascunho'; // Estado de visibilidade do produto
  novidade: boolean;                 // Indica se o produto aparece no carrossel de novidades
  criado_em: string;                 // Data de criação no formato ISO (string)
  imagens?: ImagemProduto[];         // Lista de fotos do produto (opcional)
  imagem_url?: string;               // URL da imagem principal (calculada pelo service)
  categoria?: string;                // Nome da categoria (string, preenchido pelo service)
}
