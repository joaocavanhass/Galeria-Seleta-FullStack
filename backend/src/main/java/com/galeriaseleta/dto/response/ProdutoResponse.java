// ============================================================
// ARQUIVO: ProdutoResponse.java
// FUNÇÃO: DTO de saída com todos os dados de um produto.
// Inclui a categoria aninhada e a lista de fotos do produto.
//
// MÉTODO from(): converte Produto → ProdutoResponse.
// Versão 1: from(produto) — usa as fotos já carregadas no model
// Versão 2: from(produto, fotos) — sobrescreve as fotos com uma lista customizada
//
// STREAM API: produto.getFotos().stream().map(FotoProdutoResponse::from).toList()
// Significa: para cada foto na lista, converte para FotoProdutoResponse
// É o equivalente a um forEach que transforma cada item.
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.Produto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProdutoResponse {

    private Integer id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String status;        // "disponivel" ou "indisponivel"
    private Boolean novidade;     // true = aparece na seção de novidades
    private LocalDateTime criadoEm;
    private CategoriaResponse categoria; // Categoria aninhada no JSON de resposta
    private List<FotoProdutoResponse> fotos; // Lista de fotos do produto

    // Converte Produto model → ProdutoResponse DTO
    // Monta também a categoria e as fotos como DTOs aninhados
    public static ProdutoResponse from(Produto produto) {
        ProdutoResponse dto = new ProdutoResponse();
        dto.id = produto.getId();
        dto.nome = produto.getNome();
        dto.descricao = produto.getDescricao();
        dto.preco = produto.getPreco();
        dto.status = produto.getStatus();
        dto.novidade = produto.getNovidade();
        dto.criadoEm = produto.getCriadoEm();

        // Converte a categoria apenas se ela existir
        if (produto.getCategoria() != null) {
            dto.categoria = CategoriaResponse.from(produto.getCategoria());
        }

        // Converte cada foto usando Stream: transforma List<FotoProduto> → List<FotoProdutoResponse>
        if (produto.getFotos() != null) {
            dto.fotos = produto.getFotos().stream().map(FotoProdutoResponse::from).toList();
        }
        return dto;
    }

    // Versão alternativa que substitui a lista de fotos por uma lista customizada
    public static ProdutoResponse from(Produto produto, List<FotoProdutoResponse> fotos) {
        ProdutoResponse dto = from(produto);
        dto.fotos = fotos;
        return dto;
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public String getStatus() { return status; }
    public Boolean getNovidade() { return novidade; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public CategoriaResponse getCategoria() { return categoria; }
    public List<FotoProdutoResponse> getFotos() { return fotos; }
    public void setFotos(List<FotoProdutoResponse> fotos) { this.fotos = fotos; }
}
