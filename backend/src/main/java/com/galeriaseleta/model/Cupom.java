// ============================================================
// ARQUIVO: Cupom.java
// FUNÇÃO: Model que representa a tabela "cupons" no banco.
// Cupons são códigos que os clientes inserem no checkout para
// obter desconto. Podem ser de dois tipos:
// - "percentual": desconta uma porcentagem do total (ex: 10%)
// - "fixo": desconta um valor fixo em reais (ex: R$20,00)
//
// CONEXÕES: validado pelo CupomController e CupomService,
// e referenciado pelo Pedido quando um cupom é aplicado.
// ============================================================

package com.galeriaseleta.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupons")
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cupom_id")
    private Integer id;

    // O código que o cliente digita no checkout (ex: "DESCONTO10")
    // unique = true: não podem existir dois cupons com o mesmo código
    @Column(nullable = false, unique = true)
    private String codigo;

    // Tipo do desconto: "percentual" (%) ou "fixo" (R$)
    @Column(name = "tipo_desconto", nullable = false)
    private String tipoDesconto;

    // O valor do desconto — interpretado conforme o tipoDesconto:
    // Se "percentual": 10.00 significa 10% de desconto
    // Se "fixo": 20.00 significa R$20,00 de desconto
    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    // Se false, o cupom foi desativado e não pode mais ser usado
    @Column(nullable = false)
    private Boolean ativo = true;

    // Data e hora de expiração do cupom.
    // Se null: o cupom não expira. Se preenchido: só é válido até essa data.
    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipoDesconto() { return tipoDesconto; }
    public void setTipoDesconto(String tipoDesconto) { this.tipoDesconto = tipoDesconto; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getExpiraEm() { return expiraEm; }
    public void setExpiraEm(LocalDateTime expiraEm) { this.expiraEm = expiraEm; }
}
