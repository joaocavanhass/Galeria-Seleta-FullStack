// ============================================================
// ARQUIVO: AdicionarCarrinhoRequest.java
// FUNÇÃO: DTO de entrada para adicionar um produto ao carrinho.
// O frontend envia o ID do produto e a quantidade desejada.
//
// FLUXO: Botão "Adicionar ao carrinho" → POST /api/carrinho/itens
// → CarrinhoController → CarrinhoService → salva ou incrementa no banco
//
// EXEMPLO DE JSON recebido:
// { "produtoId": 5, "quantidade": 2 }
// ============================================================

package com.galeriaseleta.dto.request;

public class AdicionarCarrinhoRequest {

    private Long produtoId;    // ID do produto a ser adicionado
    private Integer quantidade; // Quantidade a adicionar (ex: 2 unidades)

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
