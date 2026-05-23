package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.ItemPedido;
import com.galeriaseleta.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private Integer id;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal valorFrete;
    private BigDecimal desconto;
    private BigDecimal total;
    private LocalDateTime criadoEm;
    private EnderecoResponse endereco;
    private OpcaoFreteResponse frete;
    private CupomResponse cupom;
    private List<ItemPedidoResponse> itens;

    public static PedidoResponse from(Pedido pedido, List<ItemPedido> itens) {
        PedidoResponse dto = new PedidoResponse();
        dto.id = pedido.getId();
        dto.status = pedido.getStatus();
        dto.subtotal = pedido.getSubtotal();
        dto.valorFrete = pedido.getValorFrete();
        dto.desconto = pedido.getDesconto();
        dto.total = pedido.getTotal();
        dto.criadoEm = pedido.getCriadoEm();
        if (pedido.getEndereco() != null) {
            dto.endereco = EnderecoResponse.from(pedido.getEndereco());
        }
        if (pedido.getFrete() != null) {
            dto.frete = OpcaoFreteResponse.from(pedido.getFrete());
        }
        if (pedido.getCupom() != null) {
            dto.cupom = CupomResponse.from(pedido.getCupom());
        }
        dto.itens = itens.stream()
                .map(ItemPedidoResponse::from)
                .toList();
        return dto;
    }

    public Integer getId() { return id; }
    public String getStatus() { return status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getValorFrete() { return valorFrete; }
    public BigDecimal getDesconto() { return desconto; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public EnderecoResponse getEndereco() { return endereco; }
    public OpcaoFreteResponse getFrete() { return frete; }
    public CupomResponse getCupom() { return cupom; }
    public List<ItemPedidoResponse> getItens() { return itens; }
}
