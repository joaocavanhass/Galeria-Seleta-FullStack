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
    private String status;
    private Boolean novidade;
    private LocalDateTime criadoEm;
    private CategoriaResponse categoria;
    private List<FotoProdutoResponse> fotos;

    public static ProdutoResponse from(Produto produto) {
        ProdutoResponse dto = new ProdutoResponse();
        dto.id = produto.getId();
        dto.nome = produto.getNome();
        dto.descricao = produto.getDescricao();
        dto.preco = produto.getPreco();
        dto.status = produto.getStatus();
        dto.novidade = produto.getNovidade();
        dto.criadoEm = produto.getCriadoEm();
        if (produto.getCategoria() != null) {
            dto.categoria = CategoriaResponse.from(produto.getCategoria());
        }
        return dto;
    }

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
