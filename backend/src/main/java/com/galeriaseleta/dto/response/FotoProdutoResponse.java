// ============================================================
// ARQUIVO: FotoProdutoResponse.java
// FUNÇÃO: DTO de saída com os dados de uma foto de produto.
// Não inclui o produto pai (desnecessário — evita recursão).
// Retornado dentro de ProdutoResponse como lista de fotos.
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.FotoProduto;

public class FotoProdutoResponse {

    private Integer id;
    private String url;        // Link da imagem
    private Boolean principal; // true = foto de capa do produto
    private Integer ordem;     // Posição na galeria (0, 1, 2, ...)

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
