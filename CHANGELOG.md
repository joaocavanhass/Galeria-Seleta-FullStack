# Changelog — GaleriaSeleta Backend

## [2026-05-14] — Correções de consistência entre modelos, services e DTOs

---

### Contexto

Os repositórios JPA (`repository/`) estavam corretos e não foram alterados.
As correções abaixo resolveram inconsistências entre os modelos de dados, a camada de serviço e os DTOs de resposta — principalmente a ausência do campo `quantidade` que já era esperado pelos DTOs de requisição mas não existia nas entidades.

---

### Modelos (`model/`)

#### `Carrinho.java`
- **Adicionado** campo `quantidade` (`Integer`, `NOT NULL`, padrão `1`)
- **Adicionado** getter `getQuantidade()` e setter `setQuantidade()`

**Antes:**
```java
@Column(name = "adicionado_em")
private LocalDateTime adicionadoEm = LocalDateTime.now();
```

**Depois:**
```java
@Column(nullable = false)
private Integer quantidade = 1;

@Column(name = "adicionado_em")
private LocalDateTime adicionadoEm = LocalDateTime.now();
```

---

#### `ItemPedido.java`
- **Adicionado** campo `quantidade` (`Integer`, `NOT NULL`, padrão `1`)
- **Adicionado** getter `getQuantidade()` e setter `setQuantidade()`

**Antes:**
```java
@Column(name = "preco_pago", nullable = false, precision = 10, scale = 2)
private BigDecimal precoPago;
```

**Depois:**
```java
@Column(nullable = false)
private Integer quantidade = 1;

@Column(name = "preco_pago", nullable = false, precision = 10, scale = 2)
private BigDecimal precoPago;
```

---

### Services (`service/`)

#### `CarrinhoService.java`

**`adicionarItem()`**
- **Antes:** sempre criava um novo item no carrinho, mesmo se o produto já estivesse lá, e não definia `quantidade`
- **Depois:** se o produto já existe no carrinho do usuário, incrementa a `quantidade`; caso contrário cria o item com a quantidade enviada na requisição (padrão `1`)

**`atualizarQuantidade()`**
- **Antes:** método era um no-op completo (`// operação ignorada`)
- **Depois:** busca o item pelo ID e atualiza o campo `quantidade` corretamente

**`limpar()`**
- **Antes:** chamava `deleteAll(itens)` sem verificar se a lista estava vazia (gerava aviso de tipo nulo)
- **Depois:** verifica `!itens.isEmpty()` antes de chamar `deleteAll`

---

#### `PedidoService.java`

**`criar()`**
- **Antes:** salvava cada `ItemPedido` sem definir `quantidade`
- **Depois:** define `quantidade = 1` em cada item ao criar o pedido

---

### DTOs de Resposta (`dto/response/`)

#### `CarrinhoItemResponse.java`
- **Adicionado** campo `quantidade` populado a partir de `carrinho.getQuantidade()`
- **Adicionado** getter `getQuantidade()`

#### `CarrinhoResponse.java`
- **Corrigido** cálculo do campo `total`
- **Antes:** somava apenas o preço unitário de cada item (`preco`)
- **Depois:** multiplica `preco × quantidade` antes de somar (`preco * quantidade`)

```java
// Antes
.map(item -> item.getProduto().getPreco())

// Depois
.map(item -> item.getProduto().getPreco()
        .multiply(BigDecimal.valueOf(item.getQuantidade())))
```

#### `ItemPedidoResponse.java`
- **Adicionado** campo `quantidade` populado a partir de `item.getQuantidade()`
- **Adicionado** getter `getQuantidade()`

---

### Banco de Dados

As novas colunas precisam existir nas tabelas. Se o banco já foi criado antes dessas mudanças, execute:

```sql
ALTER TABLE carrinho ADD COLUMN quantidade INT NOT NULL DEFAULT 1;
ALTER TABLE itens_pedido ADD COLUMN quantidade INT NOT NULL DEFAULT 1;
```

Se o banco é gerado automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update` ou `create`), as colunas são criadas automaticamente.

