package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Categoria;

public class CategoriaResponse {

    private Integer id;
    private String nome;
    private String nomeUrl;
    private Boolean ativo;
    private Integer categoriaMaeId;
    private String categoriaMaeNome;

    public static CategoriaResponse from(Categoria categoria) {
        CategoriaResponse dto = new CategoriaResponse();
        dto.id = categoria.getId();
        dto.nome = categoria.getNome();
        dto.nomeUrl = categoria.getNomeUrl();
        dto.ativo = categoria.getAtivo();
        if (categoria.getCategoriaMae() != null) {
            dto.categoriaMaeId = categoria.getCategoriaMae().getId();
            dto.categoriaMaeNome = categoria.getCategoriaMae().getNome();
        }
        return dto;
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getNomeUrl() { return nomeUrl; }
    public Boolean getAtivo() { return ativo; }
    public Integer getCategoriaMaeId() { return categoriaMaeId; }
    public String getCategoriaMaeNome() { return categoriaMaeNome; }
}
