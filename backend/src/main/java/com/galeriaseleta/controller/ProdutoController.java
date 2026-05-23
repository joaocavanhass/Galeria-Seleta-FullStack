package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AtualizarStatusRequest;
import com.galeriaseleta.dto.request.ProdutoRequest;
import com.galeriaseleta.dto.response.ProdutoResponse;
import com.galeriaseleta.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /** Lista produtos com filtros opcionais. Roteia para o método de serviço adequado com base nos parâmetros. */
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarProdutos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false, defaultValue = "padrao") String ordenacao,
            @RequestParam(required = false, defaultValue = "ativo") String status) {

        List<ProdutoResponse> resultado;

        if (categoriaId != null) {
            resultado = produtoService.buscarPorCategoria(categoriaId).stream()
                    .map(ProdutoResponse::from).toList();
        } else {
            resultado = switch (ordenacao) {
                case "menor-preco" -> produtoService.listarPorMenorPreco().stream().map(ProdutoResponse::from).toList();
                case "maior-preco" -> produtoService.listarPorMaiorPreco().stream().map(ProdutoResponse::from).toList();
                case "novidades"   -> produtoService.listarNovidades().stream().map(ProdutoResponse::from).toList();
                default -> "todos".equals(status)
                        ? produtoService.listarTodos().stream().map(ProdutoResponse::from).toList()
                        : produtoService.listarAtivos().stream().map(ProdutoResponse::from).toList();
            };
        }

        return ResponseEntity.ok(resultado);
    }

    /** Retorna os produtos marcados como novidade para o marquee da home. */
    @GetMapping("/novidades")
    public ResponseEntity<List<ProdutoResponse>> listarNovidades() {
        return ResponseEntity.ok(produtoService.listarNovidades().stream()
                .map(ProdutoResponse::from).toList());
    }

    /** Busca produtos cujo nome contenha o termo informado. */
    @GetMapping("/busca")
    public ResponseEntity<List<ProdutoResponse>> buscar(@RequestParam String termo) {
        return ResponseEntity.ok(produtoService.buscarPorNome(termo).stream()
                .map(ProdutoResponse::from).toList());
    }

    /** Retorna os detalhes de um produto pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarProduto(@PathVariable Long id) {
        return ResponseEntity.ok(ProdutoResponse.from(produtoService.buscarPorId(id)));
    }

    /** Cadastra um novo produto. */
    @PostMapping
    public ResponseEntity<ProdutoResponse> criarProduto(@RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProdutoResponse.from(produtoService.salvar(request)));
    }

    /** Atualiza os dados de um produto existente. */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizarProduto(
            @PathVariable Long id,
            @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(ProdutoResponse.from(produtoService.atualizar(id, request)));
    }

    /** Altera o status de visibilidade de um produto (ativo/inativo). */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatusProduto(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequest request) {
        ProdutoRequest statusRequest = new ProdutoRequest();
        statusRequest.setStatus(request.getStatus());
        produtoService.atualizar(id, statusRequest);
        return ResponseEntity.ok().build();
    }

    /** Remove um produto pelo ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
