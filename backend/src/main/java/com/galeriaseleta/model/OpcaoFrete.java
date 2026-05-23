package com.galeriaseleta.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "opcoes_frete")
public class OpcaoFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "frete_id")
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "prazo_minimo", nullable = false)
    private Integer prazoMinimo;

    @Column(name = "prazo_maximo", nullable = false)
    private Integer prazoMaximo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getPrazoMinimo() { return prazoMinimo; }
    public void setPrazoMinimo(Integer prazoMinimo) { this.prazoMinimo = prazoMinimo; }

    public Integer getPrazoMaximo() { return prazoMaximo; }
    public void setPrazoMaximo(Integer prazoMaximo) { this.prazoMaximo = prazoMaximo; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
}
