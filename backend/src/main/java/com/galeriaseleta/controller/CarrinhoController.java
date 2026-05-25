package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AdicionarCarrinhoRequest;
import com.galeriaseleta.dto.request.AtualizarCarrinhoRequest;
import com.galeriaseleta.dto.response.CarrinhoItemResponse;
import com.galeriaseleta.dto.response.CarrinhoResponse;
import com.galeriaseleta.security.UsuarioDetails;
import com.galeriaseleta.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping
    public ResponseEntity<CarrinhoResponse> obterCarrinho(@AuthenticationPrincipal UsuarioDetails ud) {
        return ResponseEntity.ok(carrinhoService.obterCarrinho((long) ud.getUsuario().getId()));
    }

    @PostMapping("/itens")
    public ResponseEntity<CarrinhoItemResponse> adicionarItem(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody AdicionarCarrinhoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carrinhoService.adicionarItem((long) ud.getUsuario().getId(), request));
    }

    @PutMapping("/itens/{itemId}")
    public ResponseEntity<Void> atualizarItem(
            @PathVariable Long itemId,
            @RequestBody AtualizarCarrinhoRequest request) {
        carrinhoService.atualizarQuantidade(itemId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/itens/{itemId}")
    public ResponseEntity<Void> removerItem(
            @AuthenticationPrincipal UsuarioDetails ud,
            @PathVariable Long itemId) {
        carrinhoService.removerItem((long) ud.getUsuario().getId(), itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> limparCarrinho(@AuthenticationPrincipal UsuarioDetails ud) {
        carrinhoService.limpar((long) ud.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }
}
