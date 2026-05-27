// ============================================================
// ARQUIVO: imagem-produto.model.ts
// FUNÇÃO: Define a estrutura de uma foto associada a um produto.
//
// RELAÇÃO COM O BACKEND:
// Corresponde à tabela "fotos_produto" no banco de dados.
// Cada produto pode ter múltiplas fotos; uma delas é marcada
// como "principal" (exibida na listagem) e as demais ficam
// disponíveis para galeria na página de detalhes.
//
// CAMPO "ordem":
// Controla a ordem de exibição das fotos (0, 1, 2...).
// A foto com menor ordem aparece primeiro na galeria.
// ============================================================

export interface ImagemProduto {
  id: number;         // Identificador único da foto no banco de dados
  produto_id: number; // ID do produto ao qual esta foto pertence
  url: string;        // URL da imagem (endereço onde a foto está hospedada)
  principal: boolean; // true = imagem principal do produto (usada na listagem)
  ordem: number;      // Posição na galeria de fotos (0 = primeira)
}
