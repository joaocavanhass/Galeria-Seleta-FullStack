// ============================================================
// ARQUIVO: OpcaoFreteResponse.java
// FUNÇÃO: DTO de saída com os dados de uma opção de frete.
// Retornado pelo GET /api/frete para o frontend listar as opções de entrega.
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.OpcaoFrete;
import java.math.BigDecimal;

public class OpcaoFreteResponse {

    private Integer id;
    private String nome;          // "Padrão" ou "Expresso"
    private Integer prazoMinimo;  // Prazo mínimo em dias úteis
    private Integer prazoMaximo;  // Prazo máximo em dias úteis
    private BigDecimal preco;     // Custo do frete em reais

    public static OpcaoFreteResponse from(OpcaoFrete frete) {
        OpcaoFreteResponse dto = new OpcaoFreteResponse();
        dto.id = frete.getId();
        dto.nome = frete.getNome();
        dto.prazoMinimo = frete.getPrazoMinimo();
        dto.prazoMaximo = frete.getPrazoMaximo();
        dto.preco = frete.getPreco();
        return dto;
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public Integer getPrazoMinimo() { return prazoMinimo; }
    public Integer getPrazoMaximo() { return prazoMaximo; }
    public BigDecimal getPreco() { return preco; }
}
