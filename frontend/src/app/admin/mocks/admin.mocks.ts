import { Usuario, Pedido } from '../models/admin.models';

export const USUARIOS_MOCK: Usuario[] = [
  { id: 1, nome: 'Ana Beatriz Silva', email: 'ana@email.com', papel: 'cliente', status: 'ativo', criado_em: '2025-01-10', telefone: '(11) 99999-1111', total_pedidos: 4, total_gasto: 1280.00 },
  { id: 2, nome: 'Carlos Eduardo Mendes', email: 'carlos@email.com', papel: 'cliente', status: 'ativo', criado_em: '2025-02-14', telefone: '(11) 99999-2222', total_pedidos: 2, total_gasto: 540.00 },
  { id: 3, nome: 'Fernanda Lima', email: 'fernanda@email.com', papel: 'cliente', status: 'bloqueado', criado_em: '2025-01-25', telefone: '(21) 99999-3333', total_pedidos: 0, total_gasto: 0 },
  { id: 4, nome: 'Ricardo Santos', email: 'ricardo@email.com', papel: 'cliente', status: 'ativo', criado_em: '2025-03-05', telefone: '(31) 99999-4444', total_pedidos: 7, total_gasto: 3200.00 },
  { id: 5, nome: 'Juliana Costa', email: 'juliana@email.com', papel: 'cliente', status: 'ativo', criado_em: '2025-03-18', telefone: '(41) 99999-5555', total_pedidos: 1, total_gasto: 220.00 },
  { id: 6, nome: 'Administrador', email: 'admin@galeriaseleta.com', papel: 'admin', status: 'ativo', criado_em: '2024-12-01', total_pedidos: 0, total_gasto: 0 },
];

export const PEDIDOS_MOCK: Pedido[] = [
  {
    id: 1001,
    usuario_id: 1,
    usuario_nome: 'Ana Beatriz Silva',
    usuario_email: 'ana@email.com',
    itens: [
      { produto_id: 1, produto_nome: 'Colar Minimalista Dourado', quantidade: 1, preco_unitario: 320.00, subtotal: 320.00 },
      { produto_id: 3, produto_nome: 'Brinco Argola Prata 925', quantidade: 2, preco_unitario: 180.00, subtotal: 360.00 }
    ],
    total: 680.00,
    status: 'enviado',
    forma_pagamento: 'cartao_credito',
    endereco_entrega: 'Rua das Flores, 123 - São Paulo, SP',
    criado_em: '2026-05-18',
    atualizado_em: '2026-05-20'
  },
  {
    id: 1002,
    usuario_id: 4,
    usuario_nome: 'Ricardo Santos',
    usuario_email: 'ricardo@email.com',
    itens: [
      { produto_id: 2, produto_nome: 'Anel Solitário Ouro 18k', quantidade: 1, preco_unitario: 1200.00, subtotal: 1200.00 }
    ],
    total: 1200.00,
    status: 'pago',
    forma_pagamento: 'pix',
    endereco_entrega: 'Av. Brasil, 456 - Belo Horizonte, MG',
    criado_em: '2026-05-20',
    atualizado_em: '2026-05-20'
  },
  {
    id: 1003,
    usuario_id: 2,
    usuario_nome: 'Carlos Eduardo Mendes',
    usuario_email: 'carlos@email.com',
    itens: [
      { produto_id: 5, produto_nome: 'Pulseira Couro Trançado', quantidade: 1, preco_unitario: 240.00, subtotal: 240.00 }
    ],
    total: 240.00,
    status: 'pendente',
    forma_pagamento: 'boleto',
    endereco_entrega: 'Rua Ipiranga, 789 - Rio de Janeiro, RJ',
    criado_em: '2026-05-21',
    atualizado_em: '2026-05-21'
  },
  {
    id: 1004,
    usuario_id: 5,
    usuario_nome: 'Juliana Costa',
    usuario_email: 'juliana@email.com',
    itens: [
      { produto_id: 4, produto_nome: 'Tornozeleira Fina Prata', quantidade: 1, preco_unitario: 220.00, subtotal: 220.00 }
    ],
    total: 220.00,
    status: 'entregue',
    forma_pagamento: 'cartao_credito',
    endereco_entrega: 'Rua Verde, 321 - Curitiba, PR',
    criado_em: '2026-05-10',
    atualizado_em: '2026-05-15'
  },
  {
    id: 1005,
    usuario_id: 1,
    usuario_nome: 'Ana Beatriz Silva',
    usuario_email: 'ana@email.com',
    itens: [
      { produto_id: 6, produto_nome: 'Colar Choker Veludo Preto', quantidade: 1, preco_unitario: 160.00, subtotal: 160.00 }
    ],
    total: 160.00,
    status: 'cancelado',
    forma_pagamento: 'pix',
    endereco_entrega: 'Rua das Flores, 123 - São Paulo, SP',
    criado_em: '2026-05-05',
    atualizado_em: '2026-05-06'
  }
];

export const DASHBOARD_STATS = {
  totalUsuarios: 156,
  totalPedidos: 342,
  produtosEstoque: 89,
  receitaTotal: 48720.50,
  vendasSemana: [
    { dia: 'Seg', valor: 1240 },
    { dia: 'Ter', valor: 980 },
    { dia: 'Qua', valor: 1580 },
    { dia: 'Qui', valor: 2100 },
    { dia: 'Sex', valor: 3200 },
    { dia: 'Sáb', valor: 2800 },
    { dia: 'Dom', valor: 1600 }
  ]
};
