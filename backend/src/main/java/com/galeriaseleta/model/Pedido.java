// ============================================================
// ARQUIVO: Pedido.java
// FUNÇÃO: Model que representa a tabela "pedidos" no banco.
// Um pedido é criado quando o usuário finaliza o checkout.
// Armazena o usuário que comprou, o endereço de entrega, o cupom
// usado, o frete escolhido, os valores e o status atual.
//
// RELACIONAMENTOS:
// - Pedido → Usuario: ManyToOne (muitos pedidos por usuário)
// - Pedido → Endereco: ManyToOne (endereço de entrega)
// - Pedido → Cupom: ManyToOne (cupom aplicado, pode ser null)
// - Pedido → OpcaoFrete: ManyToOne (frete escolhido)
//
// FLUXO DE STATUS:
// pendente → confirmado → em_separacao → enviado → entregue (ou cancelado)
//
// CONEXÕES: PedidoService, PedidoController, ItemPedido, Pagamento.
// ============================================================

package com.galeriaseleta.model;

// @JsonIgnore: omite este campo do JSON para evitar loops de serialização
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

    // @JsonIgnore: evita que o objeto usuario completo apareça no JSON do pedido
    // (o frontend recebe apenas as informações do pedido, não todos os dados do usuário)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuário que fez o pedido

    // Endereço de entrega escolhido no checkout
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    // Cupom aplicado — pode ser null (pedido sem cupom)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id")
    private Cupom cupom;

    // Opção de frete escolhida (Padrão ou Expresso)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frete_id")
    private OpcaoFrete frete;

    // Status atual do pedido — segue o fluxo definido acima
    @Column(nullable = false)
    private String status = "pendente"; // Começa como "pendente" ao ser criado

    // Soma dos preços dos itens (sem frete e sem desconto)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Valor do frete escolhido. BigDecimal.ZERO como padrão (retirada gratuita)
    @Column(name = "valor_frete", precision = 10, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    // Valor do desconto do cupom. BigDecimal.ZERO = sem desconto
    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    // total = subtotal + valorFrete - desconto
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    // Getters e Setters
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
