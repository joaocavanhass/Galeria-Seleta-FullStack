package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Carrinho;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CarrinhoResponse {

    private List<CarrinhoItemResponse> itens;
    private BigDecimal total;

    public static CarrinhoResponse from(List<Carrinho> itens) {
        CarrinhoResponse dto = new CarrinhoResponse();
        dto.itens = itens.stream()
                .map(CarrinhoItemResponse::from)
                .toList();
        dto.total = itens.stream()
                .map(item -> item.getProduto().getPreco()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return dto;
    }

    public List<CarrinhoItemResponse> getItens() { return itens; }
    public BigDecimal getTotal() { return total; }
}
