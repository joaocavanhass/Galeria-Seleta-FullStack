package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.FotoProduto;

public class FotoProdutoResponse {

    private Integer id;
    private String url;
    private Boolean principal;
    private Integer ordem;

    public static FotoProdutoResponse from(FotoProduto foto) {
        FotoProdutoResponse dto = new FotoProdutoResponse();
        dto.id = foto.getId();
        dto.url = foto.getUrl();
        dto.principal = foto.getPrincipal();
        dto.ordem = foto.getOrdem();
        return dto;
    }

    public Integer getId() { return id; }
    public String getUrl() { return url; }
    public Boolean getPrincipal() { return principal; }
    public Integer getOrdem() { return ordem; }
}
