// ============================================================
// ARQUIVO: produto-detalhe.model.ts
// FUNÇÃO: Define a estrutura de um produto com informações detalhadas.
//
// HERANÇA DE INTERFACE (extends):
// ProdutoDetalhe "estende" (herda) a interface Produto.
// Isso significa que um ProdutoDetalhe tem todos os campos de Produto
// MAIS os campos adicionais definidos aqui.
// É como dizer: "um produto detalhe é um produto normal, mas com mais dados".
//
// QUANDO É USADO:
// Na página de detalhes do produto (/produtos/:id), onde exibimos
// informações extras que não aparecem na listagem geral.
// ============================================================

// Importa a interface Produto para estendê-la
import { Produto } from './produto.model';

// extends Produto: ProdutoDetalhe herda TODOS os campos de Produto
// e adiciona os campos abaixo
export interface ProdutoDetalhe extends Produto {
  historico: string;                              // Histórico/proveniência do produto
  tamanhos: string[];                             // Lista de tamanhos disponíveis (ex: ['P', 'M', 'G'])
  cor: string;                                    // Cor do produto
  marca?: string;                                 // Marca do produto (opcional — pode estar ausente)
  condicao: 'excelente' | 'muito bom' | 'bom';   // Estado de conservação do item
}
