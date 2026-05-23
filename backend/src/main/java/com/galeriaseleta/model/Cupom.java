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

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(name = "tipo_desconto", nullable = false)
    private String tipoDesconto;

    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

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
