package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.OpcaoFrete;
import java.math.BigDecimal;

public class OpcaoFreteResponse {

    private Integer id;
    private String nome;
    private Integer prazoMinimo;
    private Integer prazoMaximo;
    private BigDecimal preco;

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
