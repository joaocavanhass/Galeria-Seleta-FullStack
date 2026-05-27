// ============================================================
// ARQUIVO: Produto.java
// FUNÇÃO: Model que representa a tabela "produtos" no banco.
// Cada produto pertence a uma categoria e pode ter várias fotos.
//
// RELACIONAMENTOS:
// - Produto → Categoria: ManyToOne (muitos produtos pertencem a uma categoria)
// - Produto → FotoProduto: OneToMany (um produto tem várias fotos)
//
// CONEXÕES: usado por ProdutoService, ProdutoController, CarrinhoService,
// PedidoService e ItemPedido.
// ============================================================

package com.galeriaseleta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;   // Para valores monetários com precisão exata (sem erros de float)
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;         // Para representar a lista de fotos

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produto_id")
    private Integer id;

    // @ManyToOne: muitos produtos pertencem a uma única categoria (relação N:1)
    // fetch = FetchType.LAZY: a categoria só é carregada do banco quando for acessada,
    //   economizando memória e consultas desnecessárias
    // @JoinColumn(name = "categoria_id"): a coluna que faz a ligação com a tabela categorias
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private String nome;

    // columnDefinition = "TEXT": coluna do tipo TEXT no SQLite, permite textos longos
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // BigDecimal: tipo correto para dinheiro em Java.
    // precision = 10: até 10 dígitos no total; scale = 2: 2 casas decimais (ex: 99.99)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    // Status do produto: "disponivel" ou "indisponivel"
    @Column(nullable = false)
    private String status = "disponivel"; // Padrão ao criar um produto

    // Marca se o produto é uma novidade (aparece na seção de novidades)
    @Column(nullable = false)
    private Boolean novidade = false; // Padrão: não é novidade

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    // @OneToMany: um produto pode ter várias fotos (relação 1:N)
    // mappedBy = "produto": o campo "produto" na classe FotoProduto é quem controla a relação
    // fetch = FetchType.EAGER: as fotos são carregadas JUNTO com o produto (sempre)
    //   — diferente de LAZY, aqui carregamos tudo de uma vez porque sempre precisamos das fotos
    // @OrderBy("ordem ASC"): as fotos são ordenadas pelo campo "ordem" de forma crescente
    @OneToMany(mappedBy = "produto", fetch = FetchType.EAGER)
    @OrderBy("ordem ASC")
    private List<FotoProduto> fotos = new ArrayList<>(); // Lista vazia como padrão

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

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

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public List<FotoProduto> getFotos() { return fotos; }
    public void setFotos(List<FotoProduto> fotos) { this.fotos = fotos; }
}
