// ============================================================
// ARQUIVO: FotoProduto.java
// FUNÇÃO: Model que representa a tabela "fotos_produto" no banco.
// Um produto pode ter várias fotos. Uma delas é marcada como
// "principal" e aparece como capa na listagem de produtos.
// O campo "ordem" define a sequência de exibição na galeria.
//
// RELACIONAMENTOS:
// - FotoProduto → Produto: ManyToOne (uma foto pertence a um produto)
//
// CONEXÕES: carregada junto com o Produto (FetchType.EAGER em Produto.java).
// Gerenciada pelo ProdutoService ao adicionar fotos.
// ============================================================

package com.galeriaseleta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "fotos_produto")
public class FotoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foto_id")
    private Integer id;

    // @JsonIgnore: ao retornar uma foto em JSON, não inclui o produto completo.
    //   Evita loop: Produto → fotos → FotoProduto → produto → Produto → ...
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // URL da imagem — pode ser um link externo ou caminho interno
    @Column(nullable = false)
    private String url;

    // Indica se esta é a foto principal (capa) do produto
    @Column(nullable = false)
    private Boolean principal = false;

    // Ordem de exibição na galeria (0, 1, 2, ...).
    // Produtos são ordenados por este campo (veja @OrderBy em Produto.java)
    @Column(nullable = false)
    private Integer ordem = 0;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Boolean getPrincipal() { return principal; }
    public void setPrincipal(Boolean principal) { this.principal = principal; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
}
