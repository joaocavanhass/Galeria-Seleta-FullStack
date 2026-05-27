// ============================================================
// ARQUIVO: CategoriaController.java
// FUNÇÃO: Controlador das rotas de categorias de produtos (/api/categorias).
//
// ROTAS:
// - GET    /api/categorias              → lista todas as categorias (público)
// - GET    /api/categorias/{id}         → detalha uma categoria (público)
// - GET    /api/categorias/{id}/produtos → lista produtos de uma categoria (público)
// - POST   /api/categorias              → cria uma nova categoria (admin)
// - PUT    /api/categorias/{id}         → atualiza uma categoria (admin)
// - DELETE /api/categorias/{id}         → remove uma categoria (admin)
//
// PADRÃO DE CONVERSÃO:
// Os métodos retornam CategoriaResponse (DTO), não a entidade Categoria.
// O método CategoriaResponse.from(categoria) faz essa conversão.
// Isso protege o banco de dados e controla exatamente o que é enviado ao frontend.
//
// CONEXÕES: chama CategoriaService e ProdutoService.
// ============================================================

package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.CategoriaRequest;
import com.galeriaseleta.dto.response.CategoriaResponse;
import com.galeriaseleta.dto.response.ProdutoResponse;
import com.galeriaseleta.service.CategoriaService;
import com.galeriaseleta.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: esta classe é um controlador REST (retorna JSON, não HTML)
// @RequestMapping("/api/categorias"): todas as rotas começam com este prefixo
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    // Serviço de categorias (CRUD) e de produtos (para listar por categoria)
    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;

    // Construtor: o Spring injeta ambos os serviços automaticamente
    public CategoriaController(CategoriaService categoriaService, ProdutoService produtoService) {
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
    }

    // -------------------------------------------------------
    // GET /api/categorias
    // Lista todas as categorias do sistema.
    //
    // .stream().map(CategoriaResponse::from).toList():
    //   1. stream(): transforma a lista em um fluxo de elementos
    //   2. map(CategoriaResponse::from): converte cada Categoria → CategoriaResponse
    //      (CategoriaResponse::from é uma referência ao método estático from())
    //   3. toList(): coleta o resultado em uma nova Lista
    // -------------------------------------------------------
    /** Lista todas as categorias. */
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarTodas().stream()
                .map(CategoriaResponse::from) // Converte cada entidade Categoria para o DTO de resposta
                .toList());
    }

    // -------------------------------------------------------
    // GET /api/categorias/{id}
    // Retorna os detalhes de uma categoria pelo ID.
    //
    // @PathVariable Long id: extrai o valor {id} da URL.
    //   Ex: GET /api/categorias/3 → id = 3
    // Se não encontrar, CategoriaService lança RuntimeException,
    // que é capturada pelo GlobalExceptionHandler e retorna 404.
    // -------------------------------------------------------
    /** Retorna os detalhes de uma categoria pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarCategoria(@PathVariable Long id) {
        // buscarPorId() já lança exceção se não existir; o GlobalExceptionHandler converte para 404
        return ResponseEntity.ok(CategoriaResponse.from(categoriaService.buscarPorId(id)));
    }

    // -------------------------------------------------------
    // GET /api/categorias/{id}/produtos
    // Lista todos os produtos ativos de uma categoria específica.
    // Útil para a página de listagem de produtos por categoria no frontend.
    // -------------------------------------------------------
    /** Lista os produtos de uma categoria específica. */
    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<ProdutoResponse>> listarProdutosPorCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(id).stream()
                .map(ProdutoResponse::from) // Converte cada Produto para o DTO ProdutoResponse
                .toList());
    }

    // -------------------------------------------------------
    // POST /api/categorias
    // Cria uma nova categoria no banco de dados.
    //
    // @PostMapping: responde a requisições HTTP POST (criação de recurso).
    // @RequestBody CategoriaRequest request: lê o JSON do corpo e converte
    //   para o objeto CategoriaRequest (nome, nomeUrl, categoriaMaeId, ativo).
    // ResponseEntity.status(HttpStatus.CREATED): retorna código 201 Created.
    // -------------------------------------------------------
    /** Cadastra uma nova categoria. */
    @PostMapping
    public ResponseEntity<CategoriaResponse> criarCategoria(@RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaResponse.from(categoriaService.salvar(request)));
    }

    // -------------------------------------------------------
    // PUT /api/categorias/{id}
    // Atualiza os dados de uma categoria existente.
    //
    // @PutMapping: HTTP PUT (atualização completa).
    // O serviço atualiza apenas os campos não nulos do request
    // (atualização parcial segura).
    // -------------------------------------------------------
    /** Atualiza os dados de uma categoria existente. */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(CategoriaResponse.from(categoriaService.atualizar(id, request)));
    }

    // -------------------------------------------------------
    // DELETE /api/categorias/{id}
    // Remove permanentemente uma categoria pelo ID.
    //
    // ResponseEntity.noContent().build(): 204 No Content — padrão HTTP
    // para exclusões bem-sucedidas (sem corpo na resposta).
    // -------------------------------------------------------
    /** Remove uma categoria pelo ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
