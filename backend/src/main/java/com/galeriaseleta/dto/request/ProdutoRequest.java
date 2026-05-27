// ============================================================
// ARQUIVO: ProdutoRequest.java
// FUNÇÃO: DTO de entrada para criar ou atualizar um produto (admin).
// Contém todos os campos editáveis de um produto.
//
// FLUXO: Painel admin → POST /api/produtos ou PUT /api/produtos/{id}
// → ProdutoController → ProdutoService → salva no banco
//
// EXEMPLO DE JSON recebido:
// {
//   "nome": "Camiseta Vintage",
//   "descricao": "Camiseta anos 80 em ótimo estado",
//   "preco": 89.90,
//   "status": "disponivel",
//   "novidade": true,
//   "categoriaId": 1
// }
// ============================================================

package com.galeriaseleta.dto.request;

import java.math.BigDecimal;

public class ProdutoRequest {

    private String nome;         // Nome do produto
    private String descricao;    // Descrição detalhada
    private BigDecimal preco;    // Preço em reais (ex: 89.90)
    private String status;       // "disponivel" ou "indisponivel"
    private Boolean novidade;    // true = aparece na seção de novidades
    private Integer categoriaId; // ID da categoria à qual o produto pertence

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
