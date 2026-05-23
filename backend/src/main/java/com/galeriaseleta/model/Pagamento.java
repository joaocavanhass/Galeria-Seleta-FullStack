package com.galeriaseleta.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagamento_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false)
    private String metodo;

    @Column(nullable = false)
    private Integer parcelas = 1;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "codigo_pix")
    private String codigoPix;

    @Column(name = "codigo_transacao")
    private String codigoTransacao;

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public Integer getParcelas() { return parcelas; }
    public void setParcelas(Integer parcelas) { this.parcelas = parcelas; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getCodigoPix() { return codigoPix; }
    public void setCodigoPix(String codigoPix) { this.codigoPix = codigoPix; }

    public String getCodigoTransacao() { return codigoTransacao; }
    public void setCodigoTransacao(String codigoTransacao) { this.codigoTransacao = codigoTransacao; }

    public LocalDateTime getPagoEm() { return pagoEm; }
    public void setPagoEm(LocalDateTime pagoEm) { this.pagoEm = pagoEm; }
}
