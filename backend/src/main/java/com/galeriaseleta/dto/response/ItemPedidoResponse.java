package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.ItemPedido;
import java.math.BigDecimal;

public class ItemPedidoResponse {

    private Integer id;
    private ProdutoResponse produto;
    private Integer quantidade;
    private BigDecimal precoPago;

    public static ItemPedidoResponse from(ItemPedido item) {
        ItemPedidoResponse dto = new ItemPedidoResponse();
        dto.id = item.getId();
        dto.produto = ProdutoResponse.from(item.getProduto());
        dto.quantidade = item.getQuantidade();
        dto.precoPago = item.getPrecoPago();
        return dto;
    }

    public Integer getId() { return id; }
    public ProdutoResponse getProduto() { return produto; }
    public Integer getQuantidade() { return quantidade; }
    public BigDecimal getPrecoPago() { return precoPago; }
}
