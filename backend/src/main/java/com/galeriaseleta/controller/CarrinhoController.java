package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AdicionarCarrinhoRequest;
import com.galeriaseleta.dto.request.AtualizarCarrinhoRequest;
import com.galeriaseleta.dto.response.CarrinhoItemResponse;
import com.galeriaseleta.dto.response.CarrinhoResponse;
import com.galeriaseleta.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    /** Retorna o carrinho do usuário autenticado com itens e total. O ID do usuário virá do contexto de autenticação. */
    @GetMapping
    public ResponseEntity<CarrinhoResponse> obterCarrinho() {
        return ResponseEntity.ok(carrinhoService.obterCarrinho(1L));
    }

    /** Adiciona um produto ao carrinho. Body: { produtoId, quantidade }. */
    @PostMapping("/itens")
    public ResponseEntity<CarrinhoItemResponse> adicionarItem(@RequestBody AdicionarCarrinhoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carrinhoService.adicionarItem(1L, request));
    }

    /** Atualiza a quantidade de um item do carrinho. Body: { quantidade }. */
    @PutMapping("/itens/{itemId}")
    public ResponseEntity<Void> atualizarItem(
            @PathVariable Long itemId,
            @RequestBody AtualizarCarrinhoRequest request) {
        carrinhoService.atualizarQuantidade(itemId, request);
        return ResponseEntity.ok().build();
    }

    /** Remove um item do carrinho pelo ID. */
    @DeleteMapping("/itens/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long itemId) {
        carrinhoService.removerItem(1L, itemId);
        return ResponseEntity.noContent().build();
    }

    /** Esvazia completamente o carrinho do usuário. */
    @DeleteMapping
    public ResponseEntity<Void> limparCarrinho() {
        carrinhoService.limpar(1L);
        return ResponseEntity.noContent().build();
    }
}
