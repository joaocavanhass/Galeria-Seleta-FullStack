package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AtualizarStatusRequest;
import com.galeriaseleta.dto.request.CriarPedidoRequest;
import com.galeriaseleta.dto.response.PedidoResponse;
import com.galeriaseleta.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Fluxo de status: AGUARDANDO_PAGAMENTO → CONFIRMADO → EM_SEPARACAO → ENVIADO → ENTREGUE | CANCELADO
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** Lista os pedidos do usuário autenticado. Param opcional: status. */
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarPedidos(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(pedidoService.listar(status));
    }

    /** Retorna os detalhes de um pedido pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    /** Cria um novo pedido (finalização do checkout). */
    @PostMapping
    public ResponseEntity<PedidoResponse> criarPedido(@RequestBody CriarPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request));
    }

    /** Cancela um pedido do usuário autenticado. */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelar(id);
        return ResponseEntity.ok().build();
    }

    /** Atualiza o status de um pedido. Body: { status }. Uso administrativo. */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatusPedido(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequest request) {
        pedidoService.atualizarStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }
}
