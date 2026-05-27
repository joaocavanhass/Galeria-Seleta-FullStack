// ============================================================
// ARQUIVO: CarrinhoResponse.java
// FUNÇÃO: DTO de saída que representa o carrinho completo do usuário.
// Contém a lista de itens e o valor total calculado no servidor.
//
// CÁLCULO DO TOTAL:
// Para cada item: preco * quantidade
// Depois soma todos com reduce(BigDecimal.ZERO, BigDecimal::add)
// setScale(2, HALF_UP) = arredonda para 2 casas decimais (ex: 89.999 → 90.00)
//
// Por que calcular no servidor? Para garantir que o total não seja
// manipulado pelo cliente (o frontend poderia enviar um total falso).
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Carrinho;
import java.math.BigDecimal;
import java.math.RoundingMode; // Define como arredondar (HALF_UP = arredonda pra cima no meio)
import java.util.List;

public class CarrinhoResponse {

    private List<CarrinhoItemResponse> itens; // Lista de itens no carrinho
    private BigDecimal total;                 // Valor total calculado (sem frete e sem desconto)

    // Converte lista de Carrinho (models) → CarrinhoResponse (DTO)
    public static CarrinhoResponse from(List<Carrinho> itens) {
        CarrinhoResponse dto = new CarrinhoResponse();

        // Converte cada item do carrinho para CarrinhoItemResponse
        dto.itens = itens.stream().map(CarrinhoItemResponse::from).toList();

        // Calcula o total:
        // 1. Para cada item: pega o preço do produto e multiplica pela quantidade
        // 2. Reduz a lista de valores em uma soma (começando em ZERO)
        // 3. Arredonda para 2 casas decimais
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
