// ============================================================
// ARQUIVO: ProdutoController.java
// FUNÇÃO: Controlador das rotas de produtos (/api/produtos).
//
// ENDPOINT DE LISTAGEM FLEXÍVEL:
// O método listarProdutos() é o mais complexo — aceita filtros e
// retorna dois tipos diferentes dependendo dos parâmetros:
//   - Com ?page=N: retorna PageResponse<ProdutoResponse> (paginado)
//   - Sem ?page: retorna List<ProdutoResponse> (lista simples com ordenação)
// O tipo de retorno ResponseEntity<?> usa "?" (wildcard) para aceitar ambos.
//
// ROTAS (públicas para leitura, admin para escrita):
// - GET    /api/produtos              → lista com filtros e paginação opcional
// - GET    /api/produtos/novidades    → produtos marcados como novidade
// - GET    /api/produtos/busca?termo= → busca por nome
// - GET    /api/produtos/{id}         → detalha um produto
// - POST   /api/produtos              → cria produto (admin)
// - PUT    /api/produtos/{id}         → atualiza produto (admin)
// - PATCH  /api/produtos/{id}/status  → ativa/desativa produto (admin)
// - POST   /api/produtos/{id}/fotos   → define imagem principal (admin)
// - DELETE /api/produtos/{id}         → remove produto (admin)
//
// CONEXÕES: chama ProdutoService.
// ============================================================

package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AtualizarStatusRequest;
import com.galeriaseleta.dto.request.ProdutoRequest;
import com.galeriaseleta.dto.response.FotoProdutoResponse;
import com.galeriaseleta.dto.response.PageResponse;
import com.galeriaseleta.dto.response.ProdutoResponse;
import com.galeriaseleta.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: controlador REST (retorna JSON)
// @RequestMapping("/api/produtos"): prefixo de todas as rotas desta classe
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    // Serviço com toda a lógica de produtos (CRUD, filtros, fotos, etc.)
    private final ProdutoService produtoService;

    // Construtor com Injeção de Dependência
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // -------------------------------------------------------
    // GET /api/produtos?categoriaId=&ordenacao=&status=&page=&size=
    // Endpoint flexível que muda de comportamento conforme os parâmetros:
    //
    // COM ?page=N (paginação ativa):
    //   Retorna PageResponse com metadados de paginação.
    //   Aceita filtro por status (ativo/inativo/todos) e categoriaId.
    //
    // SEM ?page (lista simples):
    //   Com categoriaId: filtra por categoria.
    //   Sem categoriaId: ordena por "menor-preco", "maior-preco", "novidades" ou padrão.
    //
    // ResponseEntity<?>: "?" significa que pode retornar qualquer tipo.
    //   Na prática, retorna PageResponse OU List dependendo do caso.
    //
    // @RequestParam(required = false): parâmetro não obrigatório na URL.
    // @RequestParam(required = false, defaultValue = "padrao"): não obrigatório,
    //   mas se omitido usa "padrao" como valor.
    // -------------------------------------------------------
    /** Lista produtos com filtros e paginação opcionais. Sem page/size retorna lista simples (ordenação especial). */
    @GetMapping
    public ResponseEntity<?> listarProdutos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false, defaultValue = "padrao") String ordenacao,
            @RequestParam(required = false, defaultValue = "ativo") String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        // CASO 1: com paginação
        if (page != null) {
            int pageSize = (size != null) ? size : 20; // Usa 20 itens por página se não informado
            String filtroStatus = "todos".equals(status) ? null : status; // "todos" → sem filtro de status
            PageResponse<ProdutoResponse> resultado = produtoService.listarPaginado(filtroStatus, categoriaId, page, pageSize);
            return ResponseEntity.ok(resultado);
        }

        // CASO 2: sem paginação — retorna lista com ordenação específica
        List<ProdutoResponse> resultado;
        if (categoriaId != null) {
            // Filtra por categoria (ignora ordenação neste caso)
            resultado = produtoService.buscarPorCategoria(categoriaId).stream()
                    .map(ProdutoResponse::from).toList();
        } else {
            // switch: seleciona o método de listagem baseado no parâmetro "ordenacao"
            resultado = switch (ordenacao) {
                case "menor-preco" -> produtoService.listarPorMenorPreco().stream().map(ProdutoResponse::from).toList();
                case "maior-preco" -> produtoService.listarPorMaiorPreco().stream().map(ProdutoResponse::from).toList();
                case "novidades"   -> produtoService.listarNovidades().stream().map(ProdutoResponse::from).toList();
                default -> "todos".equals(status)
                        ? produtoService.listarTodos().stream().map(ProdutoResponse::from).toList()   // Ativos + inativos
                        : produtoService.listarAtivos().stream().map(ProdutoResponse::from).toList(); // Somente ativos
            };
        }
        return ResponseEntity.ok(resultado);
    }

    // -------------------------------------------------------
    // GET /api/produtos/novidades
    // Retorna produtos marcados como "novidade" (campo isNovidade = true).
    // Usado no componente de marquee (faixa rolante) da página inicial.
    // -------------------------------------------------------
    /** Retorna os produtos marcados como novidade para o marquee da home. */
    @GetMapping("/novidades")
    public ResponseEntity<List<ProdutoResponse>> listarNovidades() {
        return ResponseEntity.ok(produtoService.listarNovidades().stream()
                .map(ProdutoResponse::from).toList());
    }

    // -------------------------------------------------------
    // GET /api/produtos/busca?termo=bolsa
    // Busca produtos pelo nome (pesquisa parcial — contém o termo).
    //
    // @RequestParam String termo: o termo vem da URL como query param.
    //   Ex: GET /api/produtos/busca?termo=bolsa
    // -------------------------------------------------------
    /** Busca produtos cujo nome contenha o termo informado. */
    @GetMapping("/busca")
    public ResponseEntity<List<ProdutoResponse>> buscar(@RequestParam String termo) {
        return ResponseEntity.ok(produtoService.buscarPorNome(termo).stream()
                .map(ProdutoResponse::from).toList());
    }

    // -------------------------------------------------------
    // GET /api/produtos/{id}
    // Retorna todos os detalhes de um produto pelo ID,
    // incluindo fotos, categoria e preço.
    // -------------------------------------------------------
    /** Retorna os detalhes de um produto pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarProduto(@PathVariable Long id) {
        return ResponseEntity.ok(ProdutoResponse.from(produtoService.buscarPorId(id)));
    }

    // -------------------------------------------------------
    // POST /api/produtos
    // Cria um novo produto no banco de dados (somente admin).
    // Retorna 201 Created com os dados do produto criado.
    // -------------------------------------------------------
    /** Cadastra um novo produto. */
    @PostMapping
    public ResponseEntity<ProdutoResponse> criarProduto(@RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProdutoResponse.from(produtoService.salvar(request)));
    }

    // -------------------------------------------------------
    // PUT /api/produtos/{id}
    // Atualiza os dados de um produto existente (somente admin).
    // O serviço atualiza apenas os campos não nulos do request.
    // -------------------------------------------------------
    /** Atualiza os dados de um produto existente. */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizarProduto(
            @PathVariable Long id,
            @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(ProdutoResponse.from(produtoService.atualizar(id, request)));
    }

    // -------------------------------------------------------
    // PATCH /api/produtos/{id}/status
    // Ativa ou desativa um produto sem alterar outros dados.
    // Body: { "status": "inativo" } ou { "status": "ativo" }
    //
    // Cria um ProdutoRequest com apenas o campo status preenchido.
    // O serviço ignora os demais campos nulos (atualização parcial).
    // -------------------------------------------------------
    /** Altera o status de visibilidade de um produto (ativo/inativo). */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatusProduto(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequest request) {
        // Cria um ProdutoRequest "vazio" com apenas o status preenchido
        ProdutoRequest statusRequest = new ProdutoRequest();
        statusRequest.setStatus(request.getStatus());
        produtoService.atualizar(id, statusRequest); // O serviço atualiza apenas o campo status
        return ResponseEntity.ok().build(); // 200 OK
    }

    // -------------------------------------------------------
    // POST /api/produtos/{id}/fotos
    // Define (ou substitui) a imagem principal de um produto.
    // Body: { "url": "https://..." }
    //
    // java.util.Map<String, String>: lê o corpo como um Map simples.
    // body.get("url"): extrai o valor da chave "url" do JSON.
    // setPrincipalFoto(): no service, deleta fotos existentes e cria uma nova.
    // -------------------------------------------------------
    /** Define a imagem principal de um produto (substitui fotos existentes). */
    @PostMapping("/{id}/fotos")
    public ResponseEntity<FotoProdutoResponse> adicionarFoto(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String url = body.get("url"); // Extrai a URL da imagem do corpo da requisição
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FotoProdutoResponse.from(produtoService.setPrincipalFoto(id, url)));
    }

    // -------------------------------------------------------
    // DELETE /api/produtos/{id}
    // Remove permanentemente um produto e suas fotos do banco.
    // (O service deleta as fotos primeiro por causa da chave estrangeira.)
    // -------------------------------------------------------
    /** Remove um produto pelo ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
