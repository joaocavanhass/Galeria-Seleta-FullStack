// ============================================================
// ARQUIVO: admin.models.ts
// FUNÇÃO: Define as interfaces TypeScript usadas exclusivamente no painel admin.
//
// O QUE SÃO INTERFACES?
// Interfaces são "contratos" que definem a estrutura de um objeto.
// Elas não geram código JavaScript — existem apenas durante o desenvolvimento
// para o TypeScript verificar se os dados têm os campos corretos.
//
// POR QUE SEPARAR DO BACKEND?
// O backend retorna dados no formato da API (ex: PedidoApi, PerfilApi).
// O painel admin usava um modelo próprio antes de existir integração completa.
// Estas interfaces refletem a visão do admin sobre usuários e pedidos.
//
// DIFERENÇA DE admin.models vs core/models:
// - core/models/: usado em toda a aplicação (cliente e admin)
// - admin/models/: específico das telas administrativas
//
// CONEXÕES: importado por admin.mocks.ts e pelos componentes do painel admin.
// ============================================================

// Interface que representa um usuário do sistema no painel admin.
// '|' (pipe) significa "ou" — o campo só pode ter um desses valores exatos.
export interface Usuario {
  id: number;
  nome: string;
  email: string;
  papel: 'admin' | 'cliente';      // Papel: define o que o usuário pode fazer
  status: 'ativo' | 'bloqueado';   // Status da conta
  criado_em: string;               // Data de criação (formato ISO: "2025-01-10")
  telefone?: string;               // '?' = campo opcional (pode não existir)
  cpf?: string;
  endereco?: string;
  total_pedidos?: number;          // Estatísticas opcionais do usuário
  total_gasto?: number;
}

// Interface que representa um item dentro de um pedido.
// Cada item é um produto comprado com sua quantidade e valor.
export interface ItemPedido {
  produto_id: number;
  produto_nome: string;
  produto_imagem?: string;  // Imagem opcional (nem todo item tem foto)
  quantidade: number;
  preco_unitario: number;
  subtotal: number;         // subtotal = quantidade × preco_unitario
}

// Interface que representa um pedido completo no painel admin.
// Contém dados do usuário, lista de itens e informações de pagamento/entrega.
export interface Pedido {
  id: number;
  usuario_id: number;
  usuario_nome: string;
  usuario_email: string;
  itens: ItemPedido[];     // Array de itens: um pedido tem zero ou mais itens
  total: number;
  status: 'pendente' | 'pago' | 'enviado' | 'entregue' | 'cancelado';
  forma_pagamento: 'cartao_credito' | 'pix' | 'boleto';
  endereco_entrega: string;
  criado_em: string;
  atualizado_em: string;
}
