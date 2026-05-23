export interface Usuario {
  id: number;
  nome: string;
  email: string;
  papel: 'admin' | 'cliente';
  status: 'ativo' | 'bloqueado';
  criado_em: string;
  telefone?: string;
  cpf?: string;
  endereco?: string;
  total_pedidos?: number;
  total_gasto?: number;
}

export interface ItemPedido {
  produto_id: number;
  produto_nome: string;
  produto_imagem?: string;
  quantidade: number;
  preco_unitario: number;
  subtotal: number;
}

export interface Pedido {
  id: number;
  usuario_id: number;
  usuario_nome: string;
  usuario_email: string;
  itens: ItemPedido[];
  total: number;
  status: 'pendente' | 'pago' | 'enviado' | 'entregue' | 'cancelado';
  forma_pagamento: 'cartao_credito' | 'pix' | 'boleto';
  endereco_entrega: string;
  criado_em: string;
  atualizado_em: string;
}
