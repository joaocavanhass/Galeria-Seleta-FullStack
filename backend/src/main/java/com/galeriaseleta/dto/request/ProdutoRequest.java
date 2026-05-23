package com.galeriaseleta.dto.request;

import java.math.BigDecimal;

public class ProdutoRequest {

    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String status;
    private Boolean novidade;
    private Integer categoriaId;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getNovidade() { return novidade; }
    public void setNovidade(Boolean novidade) { this.novidade = novidade; }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
}
