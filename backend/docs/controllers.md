# Controllers — Galeria Seleta API

Base URL: `http://localhost:8080`

---

## ProdutoController
Rota base: `/api/produtos`

| Método | Rota | Parâmetros / Body | Descrição |
|--------|------|-------------------|-----------|
| GET | `/api/produtos` | `?categoriaId=` `?ordenacao=menor-preco\|maior-preco\|novidades\|padrao` `?status=ativo\|todos` | Lista produtos com filtros |
| GET | `/api/produtos/novidades` | — | 8 produtos ativos mais recentes |
| GET | `/api/produtos/busca` | `?termo=` | Busca por nome |
| GET | `/api/produtos/{id}` | — | Detalhes de um produto |
| POST | `/api/produtos` | `{ nome, descricao, preco, precoDesconto?, imagemUrl, categoriaId, status }` | Cadastra produto |
| PUT | `/api/produtos/{id}` | `{ nome, descricao, preco, precoDesconto?, imagemUrl, categoriaId, status }` | Atualiza produto |
| PATCH | `/api/produtos/{id}/status` | `{ "status": "ativo" \| "inativo" }` | Altera visibilidade |
| DELETE | `/api/produtos/{id}` | — | Remove produto |

---

## CategoriaController
Rota base: `/api/categorias`

| Método | Rota | Body | Descrição |
|--------|------|------|-----------|
| GET | `/api/categorias` | — | Lista todas as categorias |
| GET | `/api/categorias/{id}` | — | Detalhes de uma categoria |
| GET | `/api/categorias/{id}/produtos` | — | Produtos de uma categoria |
| POST | `/api/categorias` | `{ nome, descricao?, imagemUrl? }` | Cadastra categoria |
| PUT | `/api/categorias/{id}` | `{ nome, descricao?, imagemUrl? }` | Atualiza categoria |
| DELETE | `/api/categorias/{id}` | — | Remove categoria |

---

## AuthController
Rota base: `/api/auth`

| Método | Rota | Body | Descrição |
|--------|------|------|-----------|
| POST | `/api/auth/login` | `{ email, senha }` | Autentica o usuário |
| POST | `/api/auth/register` | `{ nome, sobrenome, email, senha, telefone?, cpf? }` | Cadastra novo usuário |
| POST | `/api/auth/logout` | — | Encerra a sessão |
| POST | `/api/auth/refresh` | `{ refreshToken }` | Renova o token de acesso |
| POST | `/api/auth/forgot-password` | `{ email }` | Inicia recuperação de senha |
| POST | `/api/auth/reset-password` | `{ token, novaSenha }` | Redefine a senha |

---

## UsuarioController
Rota base: `/api/usuarios`

| Método | Rota | Body | Descrição |
|--------|------|------|-----------|
| GET | `/api/usuarios/me` | — | Perfil do usuário autenticado |
| PUT | `/api/usuarios/me` | `{ nome, sobrenome, email, telefone }` | Atualiza dados do perfil |
| PATCH | `/api/usuarios/me/senha` | `{ senhaAtual, novaSenha }` | Altera a senha |
| DELETE | `/api/usuarios/me` | — | Remove a conta |
| GET | `/api/usuarios/me/enderecos` | — | Lista endereços de entrega |
| POST | `/api/usuarios/me/enderecos` | `{ cep, logradouro, numero?, complemento?, bairro, cidade, estado }` | Adiciona endereço |
| DELETE | `/api/usuarios/me/enderecos/{enderecoId}` | — | Remove endereço |

---

## PedidoController
Rota base: `/api/pedidos`

| Método | Rota | Parâmetros / Body | Descrição |
|--------|------|-------------------|-----------|
| GET | `/api/pedidos` | `?status=` | Lista pedidos do usuário |
| GET | `/api/pedidos/{id}` | — | Detalhes de um pedido |
| POST | `/api/pedidos` | `{ itens: [{ produtoId, quantidade, tamanho?, cor? }], enderecoEntrega, formaPagamento }` | Cria pedido (checkout) |
| PATCH | `/api/pedidos/{id}/cancelar` | — | Cancela pedido |
| PATCH | `/api/pedidos/{id}/status` | `{ status }` | Atualiza status (admin) |

Fluxo de status: `AGUARDANDO_PAGAMENTO` → `CONFIRMADO` → `EM_SEPARACAO` → `ENVIADO` → `ENTREGUE` | `CANCELADO`

---

## CarrinhoController
Rota base: `/api/carrinho`

| Método | Rota | Body | Descrição |
|--------|------|------|-----------|
| GET | `/api/carrinho` | — | Retorna o carrinho com itens e total |
| POST | `/api/carrinho/itens` | `{ produtoId, quantidade, tamanho?, cor? }` | Adiciona item |
| PUT | `/api/carrinho/itens/{itemId}` | `{ quantidade }` | Atualiza quantidade |
| DELETE | `/api/carrinho/itens/{itemId}` | — | Remove item |
| DELETE | `/api/carrinho` | — | Esvazia o carrinho |

---

## ContatoController
Rota base: `/api`

| Método | Rota | Body / Params | Descrição |
|--------|------|---------------|-----------|
| POST | `/api/contato` | `{ nome, sobrenome, email, telefone?, mensagem }` | Envia mensagem de contato |
| POST | `/api/newsletter/inscrever` | `{ email, nome? }` | Inscreve na newsletter |
| DELETE | `/api/newsletter/cancelar` | `?email=` | Cancela inscrição na newsletter |
