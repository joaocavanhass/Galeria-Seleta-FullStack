package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AtualizarStatusRequest;
import com.galeriaseleta.dto.request.CriarPedidoRequest;
import com.galeriaseleta.dto.response.PageResponse;
import com.galeriaseleta.dto.response.PedidoResponse;
import com.galeriaseleta.security.UsuarioDetails;
import com.galeriaseleta.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// Fluxo de status: AGUARDANDO_PAGAMENTO → CONFIRMADO → EM_SEPARACAO → ENVIADO → ENTREGUE | CANCELADO
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** Lista pedidos com paginação. Admins veem todos; clientes veem apenas os próprios. */
    @GetMapping
    public ResponseEntity<PageResponse<PedidoResponse>> listarPedidos(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        boolean isAdmin = "admin".equals(ud.getUsuario().getPapel());
        Integer usuarioId = isAdmin ? null : ud.getUsuario().getId();
        return ResponseEntity.ok(pedidoService.listarPaginado(usuarioId, status, page, size));
    }

    /** Retorna os detalhes de um pedido pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    /** Cria um novo pedido (finalização do checkout). */
    @PostMapping
    public ResponseEntity<PedidoResponse> criarPedido(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody CriarPedidoRequest request) {
        // Garante que o pedido é criado para o usuário autenticado
        request.setUsuarioId((long) ud.getUsuario().getId());
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
