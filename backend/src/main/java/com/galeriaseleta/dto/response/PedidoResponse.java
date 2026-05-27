// ============================================================
// ARQUIVO: PedidoResponse.java
// FUNÇÃO: DTO de saída completo de um pedido.
// Agrega informações do pedido, usuário, endereço, frete, cupom e itens.
//
// CLASSE ANINHADA UsuarioResumo: representa apenas id, nome e email do usuário
// dentro do pedido. Evitamos retornar o UsuarioResponse completo (que tem CPF,
// telefone, etc.) para o admin não ver dados desnecessários.
//
// STREAM API: itens.stream().map(ItemPedidoResponse::from).toList()
// Converte a lista de ItemPedido (model) → lista de ItemPedidoResponse (DTO)
// ============================================================

package com.galeriaseleta.dto.response;

import com.galeriaseleta.model.ItemPedido;
import com.galeriaseleta.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    // Classe interna que representa o usuário de forma resumida dentro do pedido
    // (só o necessário para identificar quem fez o pedido)
    public static class UsuarioResumo {
        private Integer id;
        private String nome;
        private String email;

        public UsuarioResumo(Integer id, String nome, String email) {
            this.id = id; this.nome = nome; this.email = email;
        }

        public Integer getId()  { return id; }
        public String getNome() { return nome; }
        public String getEmail(){ return email; }
    }

    private Integer id;
    private String status;           // Status atual do pedido
    private BigDecimal subtotal;     // Soma dos preços dos itens (sem frete/desconto)
    private BigDecimal valorFrete;   // Valor do frete escolhido
    private BigDecimal desconto;     // Desconto aplicado pelo cupom
    private BigDecimal total;        // Total final: subtotal + frete - desconto
    private LocalDateTime criadoEm;
    private UsuarioResumo usuario;   // Resumo do usuário que fez o pedido
    private EnderecoResponse endereco;    // Endereço de entrega
    private OpcaoFreteResponse frete;    // Opção de frete usada
    private CupomResponse cupom;         // Cupom aplicado (null se não usou)
    private List<ItemPedidoResponse> itens; // Lista de produtos comprados

    // Converte Pedido + lista de itens → PedidoResponse DTO completo
    public static PedidoResponse from(Pedido pedido, List<ItemPedido> itens) {
        PedidoResponse dto = new PedidoResponse();
        dto.id = pedido.getId();
        dto.status = pedido.getStatus();
        dto.subtotal = pedido.getSubtotal();
        dto.valorFrete = pedido.getValorFrete();
        dto.desconto = pedido.getDesconto();
        dto.total = pedido.getTotal();
        dto.criadoEm = pedido.getCriadoEm();

        // Monta o resumo do usuário apenas com id, nome e email
        if (pedido.getUsuario() != null) {
            dto.usuario = new UsuarioResumo(
                pedido.getUsuario().getId(),
                pedido.getUsuario().getNome(),
                pedido.getUsuario().getEmail()
            );
        }

        // Converte endereço, frete e cupom para seus respectivos DTOs
        if (pedido.getEndereco() != null) dto.endereco = EnderecoResponse.from(pedido.getEndereco());
        if (pedido.getFrete() != null) dto.frete = OpcaoFreteResponse.from(pedido.getFrete());
        if (pedido.getCupom() != null) dto.cupom = CupomResponse.from(pedido.getCupom());

        // Converte cada ItemPedido para ItemPedidoResponse usando Stream
        dto.itens = itens.stream().map(ItemPedidoResponse::from).toList();
        return dto;
    }

    public Integer getId()             { return id; }
    public String getStatus()          { return status; }
    public BigDecimal getSubtotal()    { return subtotal; }
    public BigDecimal getValorFrete()  { return valorFrete; }
    public BigDecimal getDesconto()    { return desconto; }
    public BigDecimal getTotal()       { return total; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public UsuarioResumo getUsuario()  { return usuario; }
    public EnderecoResponse getEndereco()      { return endereco; }
    public OpcaoFreteResponse getFrete()       { return frete; }
    public CupomResponse getCupom()            { return cupom; }
    public List<ItemPedidoResponse> getItens() { return itens; }
}
