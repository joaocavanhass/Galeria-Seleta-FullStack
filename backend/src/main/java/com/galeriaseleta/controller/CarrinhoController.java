// ============================================================
// ARQUIVO: CarrinhoController.java
// FUNÇÃO: Controlador das rotas do carrinho de compras (/api/carrinho).
//
// CONCEITO IMPORTANTE — @AuthenticationPrincipal:
// Esta anotação injeta automaticamente o usuário logado no método.
// O Spring Security lê o JWT do cabeçalho Authorization, valida,
// e disponibiliza o objeto UsuarioDetails (que contém o usuário completo).
// Assim não precisamos receber o ID do usuário como parâmetro —
// ele vem diretamente do token, o que é mais seguro.
//
// ROTAS (todas exigem autenticação JWT):
// - GET    /api/carrinho              → retorna o carrinho do usuário logado
// - POST   /api/carrinho/itens        → adiciona um produto ao carrinho
// - PUT    /api/carrinho/itens/{id}   → atualiza a quantidade de um item
// - DELETE /api/carrinho/itens/{id}   → remove um item do carrinho
// - DELETE /api/carrinho              → limpa todo o carrinho
//
// CONEXÕES: chama CarrinhoService para a lógica de negócio.
// ============================================================

package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AdicionarCarrinhoRequest;
import com.galeriaseleta.dto.request.AtualizarCarrinhoRequest;
import com.galeriaseleta.dto.response.CarrinhoItemResponse;
import com.galeriaseleta.dto.response.CarrinhoResponse;
import com.galeriaseleta.security.UsuarioDetails;
import com.galeriaseleta.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// @AuthenticationPrincipal: injeta o usuário autenticado a partir do JWT
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// @RestController: controlador REST, todos os métodos retornam JSON
// @RequestMapping: prefixo de URL para todas as rotas desta classe
@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    // Serviço que contém toda a lógica do carrinho (adicionar, remover, etc.)
    private final CarrinhoService carrinhoService;

    // Injeção de Dependência via construtor
    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    // -------------------------------------------------------
    // GET /api/carrinho
    // Retorna todos os itens do carrinho do usuário logado.
    //
    // @GetMapping: responde a requisições HTTP GET (leitura de dados).
    // @AuthenticationPrincipal UsuarioDetails ud: o Spring injeta aqui
    //   o objeto do usuário autenticado extraído do JWT.
    // ud.getUsuario().getId(): pega o ID do usuário logado.
    // (long): converte o int do ID para long (tipo esperado pelo serviço).
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<CarrinhoResponse> obterCarrinho(@AuthenticationPrincipal UsuarioDetails ud) {
        // Busca o carrinho usando o ID do usuário que está logado
        return ResponseEntity.ok(carrinhoService.obterCarrinho((long) ud.getUsuario().getId()));
    }

    // -------------------------------------------------------
    // POST /api/carrinho/itens
    // Adiciona um produto ao carrinho. Se o produto já está no carrinho,
    // incrementa a quantidade em vez de criar uma duplicata.
    //
    // @PostMapping("/itens"): rota completa = /api/carrinho/itens
    // @RequestBody: lê o JSON do corpo da requisição e converte para
    //   AdicionarCarrinhoRequest (contém produtoId e quantidade).
    // HttpStatus.CREATED (201): código HTTP para "criado com sucesso".
    // -------------------------------------------------------
    @PostMapping("/itens")
    public ResponseEntity<CarrinhoItemResponse> adicionarItem(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody AdicionarCarrinhoRequest request) {
        // Passa o ID do usuário logado e os dados do produto para o serviço
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carrinhoService.adicionarItem((long) ud.getUsuario().getId(), request));
    }

    // -------------------------------------------------------
    // PUT /api/carrinho/itens/{itemId}
    // Atualiza a quantidade de um item específico do carrinho.
    //
    // @PutMapping: requisição HTTP PUT (atualização completa de um recurso).
    // @PathVariable Long itemId: extrai o {itemId} diretamente da URL.
    //   Ex: PUT /api/carrinho/itens/42 → itemId = 42
    // -------------------------------------------------------
    @PutMapping("/itens/{itemId}")
    public ResponseEntity<Void> atualizarItem(
            @PathVariable Long itemId,
            @RequestBody AtualizarCarrinhoRequest request) {
        carrinhoService.atualizarQuantidade(itemId, request);
        return ResponseEntity.ok().build(); // 200 OK sem corpo
    }

    // -------------------------------------------------------
    // DELETE /api/carrinho/itens/{itemId}
    // Remove um item específico do carrinho pelo seu ID.
    //
    // @DeleteMapping: requisição HTTP DELETE (remoção de recurso).
    // ResponseEntity.noContent().build(): retorna 204 No Content,
    //   o padrão para operações de exclusão bem-sucedidas.
    // -------------------------------------------------------
    @DeleteMapping("/itens/{itemId}")
    public ResponseEntity<Void> removerItem(
            @AuthenticationPrincipal UsuarioDetails ud,
            @PathVariable Long itemId) {
        // Passa tanto o ID do usuário quanto o ID do item (para verificação de segurança)
        carrinhoService.removerItem((long) ud.getUsuario().getId(), itemId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // -------------------------------------------------------
    // DELETE /api/carrinho
    // Remove todos os itens do carrinho do usuário logado.
    // Chamado após a finalização de um pedido para limpar o carrinho.
    // -------------------------------------------------------
    @DeleteMapping
    public ResponseEntity<Void> limparCarrinho(@AuthenticationPrincipal UsuarioDetails ud) {
        carrinhoService.limpar((long) ud.getUsuario().getId()); // Apaga todos os itens do carrinho
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
