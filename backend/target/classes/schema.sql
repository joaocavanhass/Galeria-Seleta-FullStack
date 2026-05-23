PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS usuarios (
  usuario_id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome       TEXT    NOT NULL,
  email      TEXT    NOT NULL UNIQUE,
  senha      TEXT    NOT NULL,
  telefone   TEXT,
  criado_em  TEXT    DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS categorias (
  categoria_id     INTEGER PRIMARY KEY AUTOINCREMENT,
  categoria_mae_id INTEGER REFERENCES categorias(categoria_id),
  nome             TEXT NOT NULL,
  nome_url         TEXT NOT NULL UNIQUE,
  ativo            INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS produtos (
  produto_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  categoria_id INTEGER NOT NULL REFERENCES categorias(categoria_id),
  nome         TEXT    NOT NULL,
  descricao    TEXT,
  preco        REAL    NOT NULL,
  status       TEXT    NOT NULL DEFAULT 'disponivel',
  novidade     INTEGER DEFAULT 0,
  criado_em    TEXT    DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS fotos_produto (
  foto_id    INTEGER PRIMARY KEY AUTOINCREMENT,
  produto_id INTEGER NOT NULL REFERENCES produtos(produto_id),
  url        TEXT    NOT NULL,
  principal  INTEGER DEFAULT 0,
  ordem      INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS cupons (
  cupom_id       INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo         TEXT NOT NULL UNIQUE,
  tipo_desconto  TEXT NOT NULL,
  valor_desconto REAL NOT NULL,
  ativo          INTEGER DEFAULT 1,
  expira_em      TEXT
);

CREATE TABLE IF NOT EXISTS enderecos (
  endereco_id INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id  INTEGER NOT NULL REFERENCES usuarios(usuario_id),
  rua         TEXT    NOT NULL,
  cidade      TEXT    NOT NULL,
  estado      TEXT    NOT NULL,
  cep         TEXT    NOT NULL,
  principal   INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS opcoes_frete (
  frete_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  nome          TEXT NOT NULL,
  prazo_minimo  INTEGER NOT NULL,
  prazo_maximo  INTEGER NOT NULL,
  preco         REAL    NOT NULL
);

CREATE TABLE IF NOT EXISTS carrinho (
  carrinho_id  INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id   INTEGER NOT NULL REFERENCES usuarios(usuario_id),
  produto_id   INTEGER NOT NULL REFERENCES produtos(produto_id),
  adicionado_em TEXT   DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS pedidos (
  pedido_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id  INTEGER NOT NULL REFERENCES usuarios(usuario_id),
  endereco_id INTEGER NOT NULL REFERENCES enderecos(endereco_id),
  cupom_id    INTEGER REFERENCES cupons(cupom_id),
  frete_id    INTEGER REFERENCES opcoes_frete(frete_id),
  status      TEXT    NOT NULL DEFAULT 'pendente',
  subtotal    REAL    NOT NULL,
  valor_frete REAL    DEFAULT 0,
  desconto    REAL    DEFAULT 0,
  total       REAL    NOT NULL,
  criado_em   TEXT    DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS itens_pedido (
  item_id    INTEGER PRIMARY KEY AUTOINCREMENT,
  pedido_id  INTEGER NOT NULL REFERENCES pedidos(pedido_id),
  produto_id INTEGER NOT NULL REFERENCES produtos(produto_id),
  preco_pago REAL    NOT NULL
);

CREATE TABLE IF NOT EXISTS pagamentos (
  pagamento_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  pedido_id         INTEGER NOT NULL REFERENCES pedidos(pedido_id),
  metodo            TEXT    NOT NULL,
  parcelas          INTEGER DEFAULT 1,
  status            TEXT    NOT NULL,
  valor             REAL    NOT NULL,
  codigo_pix        TEXT,
  codigo_transacao  TEXT,
  pago_em           TEXT
);

CREATE TABLE IF NOT EXISTS contatos (
  contato_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  nome         TEXT NOT NULL,
  sobrenome    TEXT,
  email        TEXT NOT NULL,
  telefone     TEXT,
  mensagem     TEXT NOT NULL,
  recebido_em  TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS newsletter (
  newsletter_id INTEGER PRIMARY KEY AUTOINCREMENT,
  email         TEXT NOT NULL UNIQUE,
  ativo         INTEGER DEFAULT 1,
  inscrito_em   TEXT DEFAULT (datetime('now'))
);
