# Galeria Seleta

Aplicação web full-stack para a **Galeria Seleta**, um brechó online com curadoria de peças vintage e exclusivas.

- **Frontend:** Angular 19 com SSR (Server-Side Rendering)
- **Backend:** Java 17 + Spring Boot 3 + SQLite

---

## Pré-requisitos

Antes de começar, instale:

| Ferramenta | Versão mínima | Download |
|---|---|---|
| Node.js | 18+ | https://nodejs.org |
| Java (JDK) | 17+ | https://adoptium.net |

> O Maven (ferramenta de build do Java) **não precisa ser instalado** — o projeto baixa automaticamente na primeira execução.

---

## Como rodar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/GustAndrade07/BackEnd_GaleriaSeleta.git
cd BackEnd_GaleriaSeleta
```

### 2. Rode o backend (Spring Boot)

Abra um terminal na pasta `backend/` e execute:

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

Na **primeira execução**, o script baixa o Maven automaticamente. Aguarde até aparecer:

```
Started GaleriaSelataApplication in X seconds
```

O banco de dados (`galeria_seleta.db`) é criado automaticamente na raiz de `backend/`.
A API ficará disponível em `http://localhost:8080`.

### 3. Rode o frontend (Angular)

Abra **outro terminal** na raiz do projeto e execute:

```bash
npm install
npm start
```

Acesse `http://localhost:4200/` no navegador.

---

## Estrutura do projeto

```
BackEnd_GaleriaSeleta/
├── backend/                        # API REST em Spring Boot
│   ├── src/main/java/com/galeriaseleta/
│   │   ├── config/                 # CORS e tratamento global de erros
│   │   ├── controller/             # Endpoints REST
│   │   ├── converter/              # Conversor LocalDateTime para SQLite
│   │   ├── dto/                    # Data Transfer Objects
│   │   │   ├── request/            # DTOs de entrada (dados recebidos da API)
│   │   │   └── response/           # DTOs de saída (dados retornados pela API)
│   │   ├── model/                  # Entidades JPA
│   │   ├── repository/             # Repositórios Spring Data JPA
│   │   └── service/                # Regras de negócio
│   ├── src/main/resources/
│   │   ├── application.properties  # Configuração do banco e servidor
│   │   └── schema.sql              # Criação das tabelas do banco
│   ├── mvnw.cmd                    # Maven Wrapper para Windows
│   └── pom.xml                     # Dependências Java
│
├── src/                            # Aplicação Angular
│   └── app/
│       ├── core/                   # Models e dados mock
│       └── [componentes]/          # home, produtos, login, cadastro, etc.
│
├── teste.http                      # Arquivo de testes da API (REST Client VSCode)
├── package.json                    # Dependências Node.js
└── README.md
```

---

## Endpoints da API

Base URL: `http://localhost:8080/api`

### Autenticação
| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastrar usuário |
| POST | `/auth/login` | Login |
| POST | `/auth/logout` | Logout |
| POST | `/auth/forgot-password` | Recuperação de senha |

### Usuário
| Método | Rota | Descrição |
|---|---|---|
| GET | `/usuarios/me` | Ver perfil |
| PUT | `/usuarios/me` | Atualizar perfil |
| PATCH | `/usuarios/me/senha` | Alterar senha |
| DELETE | `/usuarios/me` | Deletar conta |
| GET | `/usuarios/me/enderecos` | Listar endereços |
| POST | `/usuarios/me/enderecos` | Adicionar endereço |
| DELETE | `/usuarios/me/enderecos/{id}` | Remover endereço |

### Produtos
| Método | Rota | Descrição |
|---|---|---|
| GET | `/produtos` | Listar produtos (filtros: `ordenacao`, `status`) |
| GET | `/produtos/{id}` | Buscar por ID |
| GET | `/produtos/novidades` | Listar novidades |
| GET | `/produtos/busca?termo=` | Buscar por nome |
| POST | `/produtos` | Criar produto |
| PUT | `/produtos/{id}` | Atualizar produto |
| PATCH | `/produtos/{id}/status` | Alterar status |
| DELETE | `/produtos/{id}` | Deletar produto |

### Categorias
| Método | Rota | Descrição |
|---|---|---|
| GET | `/categorias` | Listar categorias |
| GET | `/categorias/{id}` | Buscar por ID |
| GET | `/categorias/{id}/produtos` | Produtos de uma categoria |
| POST | `/categorias` | Criar categoria |
| PUT | `/categorias/{id}` | Atualizar categoria |
| DELETE | `/categorias/{id}` | Deletar categoria |

### Carrinho
| Método | Rota | Descrição |
|---|---|---|
| GET | `/carrinho` | Ver carrinho |
| POST | `/carrinho/itens` | Adicionar item |
| PUT | `/carrinho/itens/{id}` | Atualizar quantidade |
| DELETE | `/carrinho/itens/{id}` | Remover item |
| DELETE | `/carrinho` | Limpar carrinho |

### Pedidos
| Método | Rota | Descrição |
|---|---|---|
| GET | `/pedidos` | Listar pedidos (filtro: `status`) |
| GET | `/pedidos/{id}` | Buscar por ID |
| POST | `/pedidos` | Criar pedido (checkout) |
| PATCH | `/pedidos/{id}/cancelar` | Cancelar pedido |
| PATCH | `/pedidos/{id}/status` | Atualizar status (admin) |

### Contato e Newsletter
| Método | Rota | Descrição |
|---|---|---|
| POST | `/contato` | Enviar mensagem de contato |
| POST | `/newsletter/inscrever` | Inscrever e-mail |
| DELETE | `/newsletter/cancelar?email=` | Cancelar inscrição |

---

## Tratamento de erros

A API retorna respostas HTTP adequadas para cada situação:

| Código | Situação |
|---|---|
| `200` / `201` / `204` | Sucesso |
| `400` | Dados inválidos |
| `401` | Credenciais incorretas |
| `404` | Recurso não encontrado |
| `409` | Conflito (ex: e-mail já cadastrado) |
| `500` | Erro interno |

---

## Banco de dados

O banco SQLite é criado automaticamente em `backend/galeria_seleta.db` na primeira execução. As tabelas são definidas em `backend/src/main/resources/schema.sql`.

| Tabela | Descrição |
|---|---|
| `usuarios` | Contas de usuário |
| `categorias` | Categorias de produtos (suporta hierarquia) |
| `produtos` | Catálogo de peças |
| `fotos_produto` | Imagens dos produtos |
| `carrinho` | Itens no carrinho por usuário |
| `pedidos` | Pedidos realizados |
| `itens_pedido` | Produtos de cada pedido |
| `enderecos` | Endereços de entrega por usuário |
| `cupons` | Cupons de desconto |
| `opcoes_frete` | Opções de envio |
| `pagamentos` | Dados de pagamento |
| `contatos` | Mensagens do formulário de contato |
| `newsletter` | Inscrições de e-mail |

---

## Testando a API

O arquivo [`teste.http`](teste.http) contém 56 requisições cobrindo todos os endpoints. Para usar, instale a extensão **REST Client** no VSCode (Huachao Mao) e clique em **Send Request** acima de cada requisição.

---

## Pendente

- Autenticação JWT
- Integração frontend ↔ API
- Painel administrativo
- Processamento de pagamento
