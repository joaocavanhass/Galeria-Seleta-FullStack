-- ============================================================
-- ARQUIVO: schema.sql
-- FUNÇÃO: Script SQL que cria todas as tabelas do banco de dados.
-- É executado automaticamente pelo Spring Boot ao iniciar a aplicação
-- (configurado por spring.sql.init.schema-locations em application.properties).
--
-- O QUE É SQL?
-- SQL (Structured Query Language) é a linguagem para criar e manipular
-- bancos de dados relacionais. Cada "CREATE TABLE" cria uma tabela no banco.
--
-- POR QUE "IF NOT EXISTS"?
-- Garante que o script pode ser executado múltiplas vezes sem erro.
-- Se a tabela já existe, o comando é ignorado — não recria nem apaga os dados.
--
-- TIPOS DE DADOS DO SQLITE:
-- INTEGER → número inteiro (id, quantidade, boolean 0/1)
-- TEXT    → texto de qualquer tamanho (nome, email, status)
-- REAL    → número decimal (preço, desconto)
--
-- RELACIONAMENTOS (REFERENCES):
-- "campo INTEGER REFERENCES outra_tabela(campo)" cria uma chave estrangeira
-- que liga registros de tabelas diferentes. Ex: produtos.categoria_id
-- aponta para categorias.categoria_id — cada produto pertence a uma categoria.
--
-- PRAGMA foreign_keys = ON: ativa a verificação de chaves estrangeiras no SQLite.
-- Sem isso, o SQLite permite inserir referências inválidas (produto com categoria inexistente).
-- ============================================================

-- Ativa a verificação de integridade referencial (chaves estrangeiras)
PRAGMA foreign_keys = ON;

-- -------------------------------------------------------
-- TABELA: usuarios
-- Armazena os dados de todos os usuários cadastrados no sistema.
-- "papel" define se o usuário é 'cliente' ou 'admin'.
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
  usuario_id INTEGER PRIMARY KEY AUTOINCREMENT, -- ID único gerado automaticamente
  nome       TEXT    NOT NULL,                  -- Nome completo do usuário
  email      TEXT    NOT NULL UNIQUE,           -- Email único (não pode repetir)
  senha      TEXT    NOT NULL,                  -- Senha criptografada com BCrypt
  telefone   TEXT,                              -- Telefone opcional
  cpf        TEXT,                              -- CPF opcional
  papel      TEXT    NOT NULL DEFAULT 'cliente',-- Papel: 'cliente' ou 'admin'
  criado_em  TEXT    DEFAULT (datetime('now'))  -- Data/hora de criação automática
);

-- -------------------------------------------------------
-- TABELA: categorias
-- Armazena as categorias dos produtos (ex: Calças, Camisetas).
-- "categoria_mae_id" permite criar hierarquia (subcategorias).
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS categorias (
  categoria_id     INTEGER PRIMARY KEY AUTOINCREMENT,
  categoria_mae_id INTEGER REFERENCES categorias(categoria_id), -- Categoria pai (opcional)
  nome             TEXT NOT NULL,                               -- Nome exibido na tela
  nome_url         TEXT NOT NULL UNIQUE,                        -- Slug para URLs (ex: "calcas")
  ativo            INTEGER DEFAULT 1                            -- 1 = ativo, 0 = inativo
);

-- -------------------------------------------------------
-- TABELA: produtos
-- Armazena todos os produtos da loja.
-- Cada produto pertence a uma categoria (categoria_id).
-- "status": 'disponivel' ou 'indisponivel' controla visibilidade.
-- "novidade": 1 = aparece na seção de novidades da home.
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS produtos (
  produto_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  categoria_id INTEGER NOT NULL REFERENCES categorias(categoria_id), -- Categoria obrigatória
  nome         TEXT    NOT NULL,
  descricao    TEXT,                                                  -- Descrição longa (opcional)
  preco        REAL    NOT NULL,                                      -- Preço em R$
  status       TEXT    NOT NULL DEFAULT 'disponivel',                 -- 'disponivel' | 'indisponivel'
  novidade     INTEGER DEFAULT 0,                                     -- 1 = novidade, 0 = comum
  criado_em    TEXT    DEFAULT (datetime('now'))
);

-- -------------------------------------------------------
-- TABELA: fotos_produto
-- Armazena as URLs das fotos de cada produto.
-- Um produto pode ter várias fotos (OneToMany).
-- "principal": 1 = foto principal exibida na listagem.
-- "ordem": define a ordem de exibição das fotos (crescente).
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS fotos_produto (
  foto_id    INTEGER PRIMARY KEY AUTOINCREMENT,
  produto_id INTEGER NOT NULL REFERENCES produtos(produto_id), -- Produto dono da foto
  url        TEXT    NOT NULL,                                  -- URL da imagem
  principal  INTEGER DEFAULT 0,                                 -- 1 = foto principal
  ordem      INTEGER DEFAULT 0                                  -- Ordem de exibição
);

-- -------------------------------------------------------
-- TABELA: cupons
-- Armazena os cupons de desconto disponíveis na loja.
-- "tipo_desconto": 'percentual' (ex: 10%) ou 'fixo' (ex: R$ 20,00).
-- "expira_em": data de validade do cupom (null = sem validade).
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS cupons (
  cupom_id       INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo         TEXT NOT NULL UNIQUE,   -- Código digitado pelo cliente (ex: "DESCONTO10")
  tipo_desconto  TEXT NOT NULL,          -- 'percentual' ou 'fixo'
  valor_desconto REAL NOT NULL,          -- Valor do desconto (10 = 10% ou R$ 10,00)
  ativo          INTEGER DEFAULT 1,      -- 1 = ativo e válido, 0 = desativado
  expira_em      TEXT                    -- Data de expiração (opcional)
);

-- -------------------------------------------------------
-- TABELA: enderecos
-- Armazena os endereços de entrega dos usuários.
-- Um usuário pode ter vários endereços (OneToMany).
-- "principal": 1 = endereço padrão usado no checkout.
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS enderecos (
  endereco_id INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id  INTEGER NOT NULL REFERENCES usuarios(usuario_id), -- Dono do endereço
  rua         TEXT    NOT NULL,                                  -- Rua e número
  cidade      TEXT    NOT NULL,
  estado      TEXT    NOT NULL,
  cep         TEXT    NOT NULL,
  principal   INTEGER DEFAULT 0 -- 1 = endereço principal do usuário
);

-- -------------------------------------------------------
-- TABELA: opcoes_frete
-- Armazena as opções de frete disponíveis (ex: PAC, SEDEX).
-- "prazo_minimo" e "prazo_maximo": faixa de prazo em dias úteis.
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS opcoes_frete (
  frete_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  nome          TEXT NOT NULL,    -- Nome da opção (ex: "PAC", "SEDEX")
  prazo_minimo  INTEGER NOT NULL, -- Prazo mínimo em dias (ex: 5)
  prazo_maximo  INTEGER NOT NULL, -- Prazo máximo em dias (ex: 10)
  preco         REAL    NOT NULL  -- Custo do frete em R$
);

-- -------------------------------------------------------
-- TABELA: carrinho
-- Armazena os itens no carrinho de compras de cada usuário.
-- Cada linha representa um produto adicionado por um usuário.
-- Relação: um usuário tem um carrinho com vários produtos (ManyToMany via tabela).
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS carrinho (
  carrinho_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id    INTEGER NOT NULL REFERENCES usuarios(usuario_id),  -- Dono do carrinho
  produto_id    INTEGER NOT NULL REFERENCES produtos(produto_id),  -- Produto adicionado
  quantidade    INTEGER NOT NULL DEFAULT 1,                         -- Quantidade no carrinho
  adicionado_em TEXT    DEFAULT (datetime('now'))                   -- Quando foi adicionado
);

-- -------------------------------------------------------
-- TABELA: pedidos
-- Armazena os pedidos realizados pelos clientes.
-- Cada pedido tem: endereço de entrega, cupom (opcional),
-- frete escolhido, status do pedido e valores.
--
-- FLUXO DE STATUS:
-- pendente → aguardando_pagamento → confirmado → em_separacao → enviado → entregue
--                                                                        → cancelado
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS pedidos (
  pedido_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id  INTEGER NOT NULL REFERENCES usuarios(usuario_id),     -- Quem fez o pedido
  endereco_id INTEGER NOT NULL REFERENCES enderecos(endereco_id),   -- Endereço de entrega
  cupom_id    INTEGER REFERENCES cupons(cupom_id),                  -- Cupom aplicado (opcional)
  frete_id    INTEGER REFERENCES opcoes_frete(frete_id),            -- Frete escolhido (opcional)
  status      TEXT    NOT NULL DEFAULT 'pendente',                   -- Status atual do pedido
  subtotal    REAL    NOT NULL,                                       -- Soma dos itens (sem frete/desconto)
  valor_frete REAL    DEFAULT 0,                                      -- Custo do frete
  desconto    REAL    DEFAULT 0,                                      -- Valor do desconto aplicado
  total       REAL    NOT NULL,                                       -- Total final a pagar
  criado_em   TEXT    DEFAULT (datetime('now'))
);

-- -------------------------------------------------------
-- TABELA: itens_pedido
-- Armazena os produtos de cada pedido (snapshot do momento da compra).
-- "preco_pago": registra o preço do momento da compra.
-- Isso garante que mudanças futuras de preço não afetam pedidos antigos.
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS itens_pedido (
  item_id    INTEGER PRIMARY KEY AUTOINCREMENT,
  pedido_id  INTEGER NOT NULL REFERENCES pedidos(pedido_id),    -- Pedido ao qual pertence
  produto_id INTEGER NOT NULL REFERENCES produtos(produto_id),  -- Produto comprado
  quantidade INTEGER NOT NULL DEFAULT 1,                         -- Quantidade deste produto
  preco_pago REAL    NOT NULL                                    -- Preço no momento da compra
);

-- -------------------------------------------------------
-- TABELA: pagamentos
-- Armazena as informações de pagamento de cada pedido.
-- "metodo": 'cartao', 'pix', 'boleto'.
-- "codigo_pix": código para pagamento via PIX (quando aplicável).
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS pagamentos (
  pagamento_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  pedido_id         INTEGER NOT NULL REFERENCES pedidos(pedido_id), -- Pedido relacionado
  metodo            TEXT    NOT NULL,   -- Método de pagamento: 'cartao', 'pix', 'boleto'
  parcelas          INTEGER DEFAULT 1,  -- Número de parcelas (1 = à vista)
  status            TEXT    NOT NULL,   -- Status: 'pendente', 'pago', 'cancelado'
  valor             REAL    NOT NULL,   -- Valor total pago
  codigo_pix        TEXT,               -- Código PIX (quando pagamento é PIX)
  codigo_transacao  TEXT,               -- ID da transação na operadora de pagamento
  pago_em           TEXT                -- Data e hora do pagamento efetivo
);

-- -------------------------------------------------------
-- TABELA: contatos
-- Armazena as mensagens enviadas pelo formulário de contato.
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS contatos (
  contato_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  nome         TEXT NOT NULL,
  sobrenome    TEXT,                             -- Sobrenome opcional
  email        TEXT NOT NULL,
  telefone     TEXT,                             -- Telefone opcional
  mensagem     TEXT NOT NULL,
  recebido_em  TEXT DEFAULT (datetime('now'))   -- Data/hora de envio automática
);

-- -------------------------------------------------------
-- TABELA: newsletter
-- Armazena os emails inscritos na newsletter da loja.
-- "ativo": 1 = inscrito, 0 = cancelou a inscrição.
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS newsletter (
  newsletter_id INTEGER PRIMARY KEY AUTOINCREMENT,
  email         TEXT NOT NULL UNIQUE,           -- Email único (não pode repetir)
  ativo         INTEGER DEFAULT 1,              -- 1 = ativo, 0 = cancelado
  inscrito_em   TEXT DEFAULT (datetime('now'))  -- Data da inscrição
);
