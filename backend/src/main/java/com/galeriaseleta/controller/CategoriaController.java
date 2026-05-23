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

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;

    public CategoriaController(CategoriaService categoriaService, ProdutoService produtoService) {
        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
    }

    /** Lista todas as categorias. */
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarTodas().stream()
                .map(CategoriaResponse::from).toList());
    }

    /** Retorna os detalhes de uma categoria pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(CategoriaResponse.from(categoriaService.buscarPorId(id)));
    }

    /** Lista os produtos de uma categoria específica. */
    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<ProdutoResponse>> listarProdutosPorCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(id).stream()
                .map(ProdutoResponse::from).toList());
    }

    /** Cadastra uma nova categoria. */
    @PostMapping
    public ResponseEntity<CategoriaResponse> criarCategoria(@RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaResponse.from(categoriaService.salvar(request)));
    }

    /** Atualiza os dados de uma categoria existente. */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(CategoriaResponse.from(categoriaService.atualizar(id, request)));
    }

    /** Remove uma categoria pelo ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
