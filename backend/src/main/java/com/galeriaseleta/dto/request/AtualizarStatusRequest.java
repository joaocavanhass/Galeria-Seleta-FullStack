// ============================================================
// ARQUIVO: AtualizarStatusRequest.java
// FUNÇÃO: DTO de entrada genérico para atualizar o status de um recurso.
// Usado pelo admin para alterar o status de pedidos.
//
// FLUXO: Painel admin → PATCH /api/pedidos/{id}/status
// → PedidoController → PedidoService → atualiza o status no banco
//
// EXEMPLO DE JSON recebido:
// { "status": "enviado" }
//
// Valores válidos para pedidos: "pendente", "confirmado",
// "em_separacao", "enviado", "entregue", "cancelado"
// ============================================================

package com.galeriaseleta.dto.request;

public class AtualizarStatusRequest {

    private String status; // Novo status a ser aplicado

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
