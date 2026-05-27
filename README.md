# Galeria Seleta

Aplicação web full-stack para a **Galeria Seleta**, um brechó online com curadoria de peças vintage e exclusivas.

- **Frontend:** Angular 19 com SSR (Server-Side Rendering)
- **Backend:** Java 17 + Spring Boot 3 + SQLite + Spring Security + JWT

---

## Pré-requisitos

Antes de começar, instale:

| Ferramenta | Versão mínima | Download |
|---|---|---|
| Node.js | 18+ | https://nodejs.org |
| Java (JDK) | 17+ | https://adoptium.net |

> O Maven (ferramenta de build do Java) **não precisa ser instalado** — o projeto já inclui o Maven Wrapper (`mvnw.cmd`).

---

## Como rodar o projeto

### 1. Clone o repositório

```bash
git clone https://github.com/GustAndrade07/Galeria-Seleta-FullStack.git
cd Galeria-Seleta-FullStack
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

O banco de dados (`galeria_seleta.db`) é criado automaticamente na pasta `backend/`.  
A API ficará disponível em `http://localhost:8080`.

### 3. Rode o frontend (Angular)

Abra **outro terminal** na pasta `frontend/` e execute:

```bash
cd frontend
npm install
npm start
```

Acesse `http://localhost:4200/` no navegador.

> Sempre suba o backend antes do frontend.

---

## Usuário administrador padrão

Na primeira inicialização do backend, um usuário admin é criado automaticamente:

| Campo | Valor |
|---|---|
| E-mail | `admin@galeriaseleta.com` |
| Senha | `admin123` |

---

## Estrutura do projeto

```
Galeria-Seleta-FullStack/
├── backend/                              # API REST em Spring Boot
│   ├── src/main/java/com/galeriaseleta/
│   │   ├── GaleriaSelataApplication.java # Ponto de entrada da aplicação
│   │   ├── config/                       # Configurações gerais
│   │   │   ├── AdminInitializer.java     # Seed de dados iniciais (admin, categorias, fretes)
│   │   │   ├── GlobalExceptionHandler.java # Tratamento global de erros
│   │   │   └── SecurityConfig.java       # CORS, autenticação, rotas públicas/protegidas
│   │   ├── controller/                   # Endpoints REST
│   │   ├── converter/                    # Conversor LocalDateTime ↔ SQLite
│   │   ├── dto/
│   │   │   ├── request/                  # Objetos recebidos pela API
│   │   │   └── response/                 # Objetos retornados pela API
│   │   ├── model/                        # Entidades JPA (tabelas do banco)
│   │   ├── repository/                   # Repositórios Spring Data JPA
│   │   ├── security/                     # Filtro JWT e UserDetailsService
│   │   └── service/                      # Regras de negócio
│   ├── src/main/resources/
│   │   ├── application.properties        # Configuração do servidor e banco
│   │   └── schema.sql                    # Criação das tabelas do banco
│   ├── mvnw.cmd                          # Maven Wrapper para Windows
│   └── pom.xml                           # Dependências Java
│
├── frontend/                             # Aplicação Angular 19
│   └── src/app/
│       ├── core/
│       │   ├── guards/                   # authGuard (rotas protegidas)
│       │   ├── interceptors/             # authInterceptor (anexa token JWT)
│       │   ├── models/                   # Interfaces TypeScript
│       │   └── services/                 # AuthService, ProdutoService, CarrinhoService...
│       ├── admin/                        # Painel administrativo
│       │   ├── dashboard/
│       │   ├── produtos/
│       │   ├── pedidos/
│       │   ├── usuarios/
│       │   └── guards/                   # adminGuard
│       ├── home/                         # Página inicial
│       ├── produtos/                     # Catálogo de produtos
│       ├── produto-detalhes/             # Detalhes do produto
│       ├── carrinho/                     # Carrinho de compras
│       ├── checkout/                     # Finalização de compra
│       ├── meus-pedidos/                 # Histórico de pedidos
│       ├── login/
│       ├── cadastro/
│       ├── esqueci-senha/
│       ├── sobre/
│       ├── header/
│       └── footer/
│
├── teste.http                            # Requisições de teste da API (REST Client VSCode)
└── README.md
```

---

## Rotas do frontend

| Rota | Componente | Autenticação |
|---|---|---|
| `/` | Home | Não |
| `/produtos` | Catálogo de produtos | Não |
| `/produtos/:id` | Detalhes do produto | Não |
| `/carrinho` | Carrinho | Não |
| `/login` | Login | Não |
| `/cadastro` | Cadastro | Não |
| `/esqueci-senha` | Recuperação de senha | Não |
| `/sobre` | Sobre | Não |
| `/checkout` | Checkout | Sim |
| `/meus-pedidos` | Histórico de pedidos | Sim |
| `/admin/login` | Login admin | Não |
| `/admin/dashboard` | Dashboard admin | Sim (admin) |
| `/admin/produtos` | Gerenciar produtos | Sim (admin) |
| `/admin/pedidos` | Gerenciar pedidos | Sim (admin) |
| `/admin/usuarios` | Gerenciar usuários | Sim (admin) |

---

## Endpoints da API

Base URL: `http://localhost:8080/api`

### Autenticação (público)
| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastrar usuário |
| POST | `/auth/login` | Login — retorna `accessToken` e `refreshToken` |
| POST | `/auth/logout` | Logout |
| POST | `/auth/refresh` | Renovar token de acesso |
| POST | `/auth/forgot-password` | Solicitar recuperação de senha |
| POST | `/auth/reset-password` | Redefinir senha com token |

### Usuário (autenticado)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/usuarios/me` | Ver perfil |
| PUT | `/usuarios/me` | Atualizar perfil (nome, telefone) |
| PATCH | `/usuarios/me/senha` | Alterar senha |
| DELETE | `/usuarios/me` | Deletar conta |
| GET | `/usuarios/me/enderecos` | Listar endereços |
| POST | `/usuarios/me/enderecos` | Adicionar endereço |
| DELETE | `/usuarios/me/enderecos/{id}` | Remover endereço |

### Usuário (admin)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/usuarios` | Listar todos os usuários (paginado) |
| PATCH | `/usuarios/{id}/papel` | Promover ou rebaixar papel (cliente/admin) |

### Produtos (público — GET)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/produtos` | Listar produtos (filtros: `categoriaId`, `ordenacao`, `status`) |
| GET | `/produtos/{id}` | Buscar por ID |
| GET | `/produtos/novidades` | Listar novidades |
| GET | `/produtos/busca?termo=` | Buscar por nome |

### Produtos (admin)
| Método | Rota | Descrição |
|---|---|---|
| POST | `/produtos` | Criar produto |
| PUT | `/produtos/{id}` | Atualizar produto |
| PATCH | `/produtos/{id}/status` | Ativar ou desativar produto |
| POST | `/produtos/{id}/fotos` | Adicionar foto principal |
| DELETE | `/produtos/{id}` | Deletar produto |

### Categorias (público — GET)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/categorias` | Listar categorias |
| GET | `/categorias/{id}` | Buscar por ID |
| GET | `/categorias/{id}/produtos` | Produtos de uma categoria |

### Categorias (admin)
| Método | Rota | Descrição |
|---|---|---|
| POST | `/categorias` | Criar categoria |
| PUT | `/categorias/{id}` | Atualizar categoria |
| DELETE | `/categorias/{id}` | Deletar categoria |

### Carrinho (autenticado)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/carrinho` | Ver carrinho |
| POST | `/carrinho/itens` | Adicionar item |
| PUT | `/carrinho/itens/{id}` | Atualizar quantidade |
| DELETE | `/carrinho/itens/{id}` | Remover item |
| DELETE | `/carrinho` | Limpar carrinho |

### Pedidos (autenticado)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/pedidos` | Listar pedidos (cliente: próprios / admin: todos) |
| GET | `/pedidos/{id}` | Buscar por ID |
| POST | `/pedidos` | Criar pedido (finalizar checkout) |
| PATCH | `/pedidos/{id}/cancelar` | Cancelar pedido |
| PATCH | `/pedidos/{id}/status` | Atualizar status (admin) |

### Cupom, Frete, Contato e Newsletter (público)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/cupons/{codigo}` | Validar cupom de desconto |
| GET | `/frete` | Listar opções de frete |
| POST | `/contato` | Enviar mensagem de contato |
| POST | `/newsletter/inscrever` | Inscrever e-mail na newsletter |
| DELETE | `/newsletter/cancelar?email=` | Cancelar inscrição |

---

## Fluxo de status dos pedidos

```
AGUARDANDO_PAGAMENTO → CONFIRMADO → EM_SEPARACAO → ENVIADO → ENTREGUE
                                                           ↘ CANCELADO
```

---

## Banco de dados

O banco SQLite é criado automaticamente em `backend/galeria_seleta.db` na primeira execução. As tabelas são definidas em `backend/src/main/resources/schema.sql`.

| Tabela | Descrição |
|---|---|
| `usuarios` | Contas de usuário com papel (cliente/admin) |
| `categorias` | Categorias de produtos (suporta hierarquia) |
| `produtos` | Catálogo de peças |
| `fotos_produto` | Imagens dos produtos (múltiplas por produto) |
| `carrinho` | Itens no carrinho por usuário |
| `pedidos` | Pedidos realizados |
| `itens_pedido` | Produtos de cada pedido |
| `enderecos` | Endereços de entrega por usuário |
| `cupons` | Cupons de desconto (percentual ou fixo) |
| `opcoes_frete` | Opções de envio (Padrão e Expresso) |
| `pagamentos` | Dados de pagamento (PIX, cartão, boleto, dinheiro) |
| `contatos` | Mensagens do formulário de contato |
| `newsletter` | Inscrições de e-mail |

---

## Autenticação JWT

O sistema usa JWT (JSON Web Token) para autenticação stateless:

- **Access Token:** válido por 24 horas
- **Refresh Token:** válido por 7 dias
- **Header:** `Authorization: Bearer <token>`
- **Secret:** configurado via variável de ambiente `JWT_SECRET` (em produção)

---

## Tratamento de erros

| Código | Situação |
|---|---|
| `200` / `201` / `204` | Sucesso |
| `400` | Dados inválidos |
| `401` | Credenciais incorretas ou token inválido |
| `404` | Recurso não encontrado |
| `409` | Conflito (ex: e-mail já cadastrado) |
| `500` | Erro interno |

---

## Testando a API

O arquivo [`teste.http`](teste.http) contém requisições cobrindo todos os endpoints. Para usar, instale a extensão **REST Client** no VSCode (Huachao Mao) e clique em **Send Request** acima de cada requisição.