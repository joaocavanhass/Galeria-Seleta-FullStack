// ============================================================
// ARQUIVO: CategoriaResponse.java
// FUNÇÃO: DTO de saída com os dados de uma categoria.
// Inclui informações da categoria mãe (se existir) de forma segura,
// sem risco de loop infinito (diferente do Model que usa @JsonIgnore).
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Categoria;

public class CategoriaResponse {

    private Integer id;
    private String nome;            // Nome exibido (ex: "Camisetas")
    private String nomeUrl;         // Slug da URL (ex: "camisetas")
    private Boolean ativo;
    private Integer categoriaMaeId;   // ID da categoria pai (null se raiz)
    private String categoriaMaeNome;  // Nome da categoria pai (null se raiz)

    // Converte Categoria (model) → CategoriaResponse (DTO)
    public static CategoriaResponse from(Categoria categoria) {
        CategoriaResponse dto = new CategoriaResponse();
        dto.id = categoria.getId();
        dto.nome = categoria.getNome();
        dto.nomeUrl = categoria.getNomeUrl();
        dto.ativo = categoria.getAtivo();

        // Se a categoria tem mãe, extrai apenas o ID e o nome
        // (evita criar um CategoriaResponse aninhado que poderia virar loop)
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
