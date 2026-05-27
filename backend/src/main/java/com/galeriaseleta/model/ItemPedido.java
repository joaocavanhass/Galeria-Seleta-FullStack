// ============================================================
// ARQUIVO: ItemPedido.java
// FUNÇÃO: Model que representa a tabela "itens_pedido" no banco.
// É a "foto" do carrinho no momento da compra — cada produto comprado
// vira um ItemPedido dentro do Pedido.
//
// Por que salvar o precoPago separadamente?
// O preço do produto pode mudar depois da compra, mas o histórico do
// pedido deve sempre mostrar o preço que o cliente pagou de fato.
//
// RELACIONAMENTOS:
// - ItemPedido → Pedido: ManyToOne (um pedido tem vários itens)
// - ItemPedido → Produto: ManyToOne (qual produto foi comprado)
//
// CONEXÕES: criado pelo PedidoService ao finalizar o checkout.
// ============================================================

package com.galeriaseleta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;

    // @JsonIgnore: ao serializar um ItemPedido, não inclui o Pedido completo,
    //   porque isso causaria um loop infinito (Pedido → ItemPedido → Pedido → ...)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido; // A qual pedido este item pertence

    // O produto comprado — referência para buscar nome, foto, etc.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Quantidade comprada deste produto no pedido
    @Column(nullable = false)
    private Integer quantidade = 1;

    // Preço pago no momento da compra — congelado para o histórico.
    // Mesmo que o produto mude de preço depois, este valor permanece.
    @Column(name = "preco_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoPago;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoPago() { return precoPago; }
    public void setPrecoPago(BigDecimal precoPago) { this.precoPago = precoPago; }
}
