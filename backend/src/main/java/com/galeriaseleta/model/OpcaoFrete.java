// ============================================================
// ARQUIVO: OpcaoFrete.java
// FUNÇÃO: Model que representa a tabela "opcoes_frete" no banco.
// Define as opções de entrega disponíveis na loja.
// Os dados iniciais são inseridos pelo AdminInitializer:
// - Padrão: R$29,90 / 5 a 7 dias úteis
// - Expresso: R$54,90 / 1 a 3 dias úteis
//
// CONEXÕES: listado pelo FreteController (GET /api/frete),
// selecionado no checkout e salvo no Pedido.
// ============================================================

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
    private String nome; // Ex: "Padrão" ou "Expresso"

    // Prazo mínimo de entrega em dias úteis
    @Column(name = "prazo_minimo", nullable = false)
    private Integer prazoMinimo;

    // Prazo máximo de entrega em dias úteis
    @Column(name = "prazo_maximo", nullable = false)
    private Integer prazoMaximo;

    // Custo do frete em reais
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    // Getters e Setters
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
