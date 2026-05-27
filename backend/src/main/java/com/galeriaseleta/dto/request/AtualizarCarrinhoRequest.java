// ============================================================
// ARQUIVO: AtualizarCarrinhoRequest.java
// FUNÇÃO: DTO de entrada para atualizar a quantidade de um item no carrinho.
// Usado quando o usuário aumenta ou diminui a quantidade de um produto.
//
// FLUXO: +/- no carrinho → PUT /api/carrinho/itens/{id}
// → CarrinhoController → CarrinhoService → atualiza no banco
//
// EXEMPLO DE JSON recebido:
// { "quantidade": 3 }
// ============================================================

package com.galeriaseleta.dto.request;

public class AtualizarCarrinhoRequest {

    private Integer quantidade; // Nova quantidade do item (substitui a atual)

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
