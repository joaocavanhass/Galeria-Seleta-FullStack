package com.galeriaseleta.dto.request;

public class CategoriaRequest {

    private String nome;
    private String nomeUrl;
    private Integer categoriaMaeId;
    private Boolean ativo;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeUrl() { return nomeUrl; }
    public void setNomeUrl(String nomeUrl) { this.nomeUrl = nomeUrl; }

    public Integer getCategoriaMaeId() { return categoriaMaeId; }
    public void setCategoriaMaeId(Integer categoriaMaeId) { this.categoriaMaeId = categoriaMaeId; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
