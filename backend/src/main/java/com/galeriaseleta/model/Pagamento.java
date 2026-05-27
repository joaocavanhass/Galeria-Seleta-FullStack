// ============================================================
// ARQUIVO: Pagamento.java
// FUNÇÃO: Model que representa a tabela "pagamentos" no banco.
// Registra os dados de pagamento de um pedido: método escolhido,
// número de parcelas, status, valor e códigos de transação.
//
// MÉTODOS suportados: "cartao", "pix", "boleto", "dinheiro"
// STATUS possíveis: "pendente", "confirmado", "recusado"
//
// RELACIONAMENTOS:
// - Pagamento → Pedido: ManyToOne (um pedido pode ter um pagamento)
//
// CONEXÕES: criado pelo PedidoService ao finalizar o checkout.
// ============================================================

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

    // O pedido ao qual este pagamento pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Método de pagamento escolhido pelo cliente
    // Valores possíveis: "cartao", "pix", "boleto", "dinheiro"
    @Column(nullable = false)
    private String metodo;

    // Número de parcelas (1 = à vista; 2, 3, ... = parcelado)
    // Só relevante para cartão de crédito
    @Column(nullable = false)
    private Integer parcelas = 1;

    // Status do pagamento: "pendente", "confirmado" ou "recusado"
    @Column(nullable = false)
    private String status;

    // Valor total pago
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // Código PIX gerado (só preenchido quando metodo = "pix")
    @Column(name = "codigo_pix")
    private String codigoPix;

    // Código de transação do gateway de pagamento (para rastreamento)
    @Column(name = "codigo_transacao")
    private String codigoTransacao;

    // Data e hora em que o pagamento foi confirmado (null se ainda pendente)
    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    // Getters e Setters
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
