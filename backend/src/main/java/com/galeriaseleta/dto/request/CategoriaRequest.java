// ============================================================
// ARQUIVO: CategoriaRequest.java
// FUNÇÃO: DTO de entrada para criar ou atualizar uma categoria (admin).
//
// FLUXO: Painel admin → POST /api/categorias ou PUT /api/categorias/{id}
// → CategoriaController → CategoriaService → salva no banco
//
// EXEMPLO DE JSON recebido:
// {
//   "nome": "Jaquetas",
//   "nomeUrl": "jaquetas",
//   "categoriaMaeId": null,
//   "ativo": true
// }
// ============================================================

package com.galeriaseleta.dto.request;

public class CategoriaRequest {

    private String nome;           // Nome exibido na loja (ex: "Jaquetas")
    private String nomeUrl;        // Slug para URL (ex: "jaquetas") — sem acentos
    private Integer categoriaMaeId; // ID da categoria pai (null se for categoria raiz)
    private Boolean ativo;         // true = visível para usuários

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeUrl() { return nomeUrl; }
    public void setNomeUrl(String nomeUrl) { this.nomeUrl = nomeUrl; }

    public Integer getCategoriaMaeId() { return categoriaMaeId; }
    public void setCategoriaMaeId(Integer categoriaMaeId) { this.categoriaMaeId = categoriaMaeId; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
