# Services — Galeria Seleta API

---

## ProdutoService

| Método | Parâmetros | Retorno | Descrição |
|--------|-----------|---------|-----------|
| `listarTodos()` | — | `List<Object>` | Todos os produtos, independente do status |
| `listarAtivos()` | — | `List<Object>` | Apenas produtos com status ativo |
| `buscarPorId(id)` | `Long` | `Object` | Produto pelo ID |
| `buscarPorCategoria(categoriaId)` | `Long` | `List<Object>` | Produtos de uma categoria |
| `buscarPorNome(nome)` | `String` | `List<Object>` | Busca por nome (contém o termo) |
| `listarNovidades()` | — | `List<Object>` | Ativos ordenados por data decrescente |
| `listarPorMenorPreco()` | — | `List<Object>` | Ativos do menor para o maior preço |
| `listarPorMaiorPreco()` | — | `List<Object>` | Ativos do maior para o menor preço |
| `salvar(dados)` | `Map<String, Object>` | `Object` | Cadastra novo produto |
| `atualizar(id, dados)` | `Long, Map<String, Object>` | `Object` | Atualiza produto existente |
| `deletar(id)` | `Long` | `void` | Remove produto |

---

## CategoriaService

| Método | Parâmetros | Retorno | Descrição |
|--------|-----------|---------|-----------|
| `listarTodas()` | — | `List<Object>` | Todas as categorias |
| `buscarPorId(id)` | `Long` | `Object` | Categoria pelo ID |
| `salvar(dados)` | `Map<String, Object>` | `Object` | Cadastra nova categoria |
| `atualizar(id, dados)` | `Long, Map<String, Object>` | `Object` | Atualiza categoria existente |
| `deletar(id)` | `Long` | `void` | Remove categoria |

---

## UsuarioService

Utiliza `BCryptPasswordEncoder` para criptografia de senha nos métodos `cadastrar` e `atualizar`.

| Método | Parâmetros | Retorno | Descrição |
|--------|-----------|---------|-----------|
| `cadastrar(dados)` | `Map<String, Object>` | `Object` | Registra usuário com validação de e-mail duplicado e senha criptografada |
| `login(email, senha)` | `String, String` | `Object` | Autentica comparando a senha com o hash armazenado |
| `buscarPorId(id)` | `Long` | `Object` | Usuário pelo ID |
| `buscarPorEmail(email)` | `String` | `Object` | Usuário pelo e-mail |
| `atualizar(id, dados)` | `Long, Map<String, Object>` | `Object` | Atualiza dados; recriptografa a senha se informada |
| `deletar(id)` | `Long` | `void` | Remove usuário |

---

## CarrinhoService

| Método | Parâmetros | Retorno | Descrição |
|--------|-----------|---------|-----------|
| `buscarOuCriar(usuarioId)` | `Long` | `Object` | Retorna o carrinho do usuário ou cria um vazio |
| `adicionarItem(usuarioId, produtoId, quantidade)` | `Long, Long, Integer` | `Object` | Adiciona item; incrementa a quantidade se já existir |
| `removerItem(carrinhoId, itemId)` | `Long, Long` | `void` | Remove item do carrinho |
| `atualizarQuantidade(itemId, quantidade)` | `Long, Integer` | `void` | Atualiza a quantidade de um item |
| `limpar(usuarioId)` | `Long` | `void` | Remove todos os itens do carrinho |
| `calcularTotal(carrinhoId)` | `Long` | `double` | Soma os itens usando preço com desconto quando disponível |

---

## ContatoService

| Método | Parâmetros | Retorno | Descrição |
|--------|-----------|---------|-----------|
| `enviarMensagem(dados)` | `Map<String, Object>` | `void` | Salva mensagem do formulário de contato |
| `listarMensagens()` | — | `List<Object>` | Retorna todas as mensagens recebidas |
| `deletarMensagem(id)` | `Long` | `void` | Remove uma mensagem |

---

## AuthService

| Método | Parâmetros | Retorno | Descrição |
|--------|-----------|---------|-----------|
| `login(email, senha)` | `String, String` | `Object` | Valida credenciais e autentica |
| `registrar(dados)` | `Map<String, Object>` | `Object` | Registra novo usuário |
| `logout()` | — | `void` | Encerra a sessão |
| `refreshToken(refreshToken)` | `String` | `Object` | Renova o token de acesso |
| `esqueceuSenha(email)` | `String` | `void` | Dispara fluxo de recuperação de senha |
| `redefinirSenha(token, novaSenha)` | `String, String` | `void` | Redefine a senha com token de recuperação |

---

## PedidoService

| Método | Parâmetros | Retorno | Descrição |
|--------|-----------|---------|-----------|
| `listar(status)` | `String` | `List<Object>` | Pedidos do usuário com filtro por status |
| `buscarPorId(id)` | `Long` | `Object` | Pedido pelo ID |
| `criar(dados)` | `Map<String, Object>` | `Object` | Cria pedido (checkout) |
| `cancelar(id)` | `Long` | `void` | Cancela pedido |
| `atualizarStatus(id, status)` | `Long, String` | `void` | Atualiza status (uso administrativo) |
