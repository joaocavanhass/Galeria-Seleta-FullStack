// ============================================================
// ARQUIVO: Carrinho.java
// FUNÇÃO: Model que representa a tabela "carrinho" no banco.
// Cada linha desta tabela representa UM produto no carrinho de UM usuário.
// Se um usuário adicionar 3 produtos diferentes, haverá 3 linhas na tabela.
//
// RELACIONAMENTOS:
// - Carrinho → Usuario: ManyToOne (vários itens de carrinho por usuário)
// - Carrinho → Produto: ManyToOne (o produto que está no carrinho)
//
// CONEXÕES: CarrinhoService gerencia toda a lógica do carrinho.
// CarrinhoController expõe os endpoints para o frontend.
// ============================================================

package com.galeriaseleta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "carrinho")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrinho_id")
    private Integer id;

    // @JsonIgnore: quando retornar este item de carrinho em JSON,
    //   não inclui o objeto completo do usuário (evita dados desnecessários e loops)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Dono do carrinho

    // O produto que está sendo carregado no carrinho
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Quantidade deste produto no carrinho (mínimo 1)
    @Column(nullable = false)
    private Integer quantidade = 1; // Padrão: 1 unidade

    // Momento em que o produto foi adicionado ao carrinho
    @Column(name = "adicionado_em")
    private LocalDateTime adicionadoEm = LocalDateTime.now();

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getAdicionadoEm() { return adicionadoEm; }
    public void setAdicionadoEm(LocalDateTime adicionadoEm) { this.adicionadoEm = adicionadoEm; }
}
