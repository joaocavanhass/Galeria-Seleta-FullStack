// ============================================================
// ARQUIVO: CarrinhoItemResponse.java
// FUNÇÃO: DTO de saída que representa um único item do carrinho.
// Inclui o produto completo (com fotos e categoria) e a quantidade.
//
// É usado dentro de CarrinhoResponse como a lista de itens.
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Carrinho;
import java.time.LocalDateTime;

public class CarrinhoItemResponse {

    private Integer id;               // ID do registro no carrinho
    private ProdutoResponse produto;  // Produto completo (nome, preço, fotos, etc.)
    private Integer quantidade;       // Quantidade deste produto no carrinho
    private LocalDateTime adicionadoEm; // Quando foi adicionado

    // Converte um item de Carrinho (model) → CarrinhoItemResponse (DTO)
    public static CarrinhoItemResponse from(Carrinho carrinho) {
        CarrinhoItemResponse dto = new CarrinhoItemResponse();
        dto.id = carrinho.getId();
        // Converte o Produto para ProdutoResponse (inclui fotos e categoria)
        dto.produto = ProdutoResponse.from(carrinho.getProduto());
        dto.quantidade = carrinho.getQuantidade();
        dto.adicionadoEm = carrinho.getAdicionadoEm();
        return dto;
    }

    public Integer getId() { return id; }
    public ProdutoResponse getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }
    public LocalDateTime getAdicionadoEm() { return adicionadoEm; }
}
