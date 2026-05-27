// ============================================================
// ARQUIVO: CriarPedidoRequest.java
// FUNÇÃO: DTO de entrada para criar um novo pedido (finalizar checkout).
// O frontend envia esses dados quando o usuário clica em "Finalizar compra".
//
// FLUXO: Checkout → POST /api/pedidos → PedidoController → PedidoService
// → busca os dados do banco e cria o pedido completo
//
// EXEMPLO DE JSON recebido:
// {
//   "usuarioId": 1,
//   "enderecoId": 2,
//   "produtoIds": [3, 5, 7],
//   "freteId": 1,
//   "codigoCupom": "DESCONTO10"
// }
// ============================================================

package com.galeriaseleta.dto.request;

import java.util.List;

public class CriarPedidoRequest {

    private Long usuarioId;           // ID do usuário que está comprando
    private Long enderecoId;          // ID do endereço de entrega escolhido
    private List<Integer> produtoIds; // Lista de IDs dos produtos no carrinho
    private Integer freteId;          // ID da opção de frete escolhida (Padrão ou Expresso)
    private String codigoCupom;       // Código do cupom de desconto (pode ser null se não usou cupom)

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getEnderecoId() { return enderecoId; }
    public void setEnderecoId(Long enderecoId) { this.enderecoId = enderecoId; }

    public List<Integer> getProdutoIds() { return produtoIds; }
    public void setProdutoIds(List<Integer> produtoIds) { this.produtoIds = produtoIds; }

    public Integer getFreteId() { return freteId; }
    public void setFreteId(Integer freteId) { this.freteId = freteId; }

    public String getCodigoCupom() { return codigoCupom; }
    public void setCodigoCupom(String codigoCupom) { this.codigoCupom = codigoCupom; }
}
