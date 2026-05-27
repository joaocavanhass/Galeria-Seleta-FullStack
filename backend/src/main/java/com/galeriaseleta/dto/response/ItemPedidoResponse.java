// ============================================================
// ARQUIVO: ItemPedidoResponse.java
// FUNÇÃO: DTO de saída que representa um produto dentro de um pedido.
// Inclui o preço pago no momento da compra (que pode diferir do preço atual).
// Retornado dentro de PedidoResponse como lista de itens comprados.
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.ItemPedido;
import java.math.BigDecimal;

public class ItemPedidoResponse {

    private Integer id;
    private ProdutoResponse produto;  // Produto completo (nome, fotos, etc.)
    private Integer quantidade;       // Quantidade comprada
    private BigDecimal precoPago;     // Preço no momento da compra (histórico imutável)

    public static ItemPedidoResponse from(ItemPedido item) {
        ItemPedidoResponse dto = new ItemPedidoResponse();
        dto.id = item.getId();
        // Converte o produto para ProdutoResponse (com fotos e categoria)
        dto.produto = ProdutoResponse.from(item.getProduto());
        dto.quantidade = item.getQuantidade();
        dto.precoPago = item.getPrecoPago(); // Preço original no momento da compra
        return dto;
    }

    public Integer getId() { return id; }
    public ProdutoResponse getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }
    public BigDecimal getPrecoPago() { return precoPago; }
}
