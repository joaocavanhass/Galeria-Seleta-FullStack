package com.galeriaseleta.dto.request;

import java.util.List;

public class CriarPedidoRequest {

    private Long usuarioId;
    private Long enderecoId;
    private List<Integer> produtoIds;
    private Integer freteId;
    private String codigoCupom;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getEnderecoId() { return enderecoId; }
    public void setEnderecoId(Long enderecoId) { this.enderecoId = enderecoId; }

    public List<Integer> getProdutoIds() { return produtoIds; }
    public void setProdutoIds(List<Integer> produtoIds) { this.produtoIds = produtoIds; }

    public Integer getFreteId() { return freteId; }
    public void setFreteId(Integer freteId) { this.freteId = freteId; }

    public String getCodigoCupom() { return codigoCupom; }
    public void setCodigoCupom(String codigoCupom) { this.codigoCupom = codigoCupom; }
}
