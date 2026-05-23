package com.galeriaseleta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id")
    private Cupom cupom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frete_id")
    private OpcaoFrete frete;

    @Column(nullable = false)
    private String status = "pendente";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "valor_frete", precision = 10, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public Cupom getCupom() { return cupom; }
    public void setCupom(Cupom cupom) { this.cupom = cupom; }

    public OpcaoFrete getFrete() { return frete; }
    public void setFrete(OpcaoFrete frete) { this.frete = frete; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
