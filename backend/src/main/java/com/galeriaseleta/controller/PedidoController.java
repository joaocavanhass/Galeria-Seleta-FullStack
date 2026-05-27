// ============================================================
// ARQUIVO: PedidoController.java
// FUNÇÃO: Controlador das rotas de pedidos (/api/pedidos).
//
// CONTROLE DE ACESSO POR PAPEL (role-based):
// Clientes veem apenas seus próprios pedidos; admins veem todos.
// A distinção é feita dentro do método, verificando ud.getUsuario().getPapel().
//
// FLUXO DE STATUS DO PEDIDO:
// pendente → AGUARDANDO_PAGAMENTO → CONFIRMADO → EM_SEPARACAO → ENVIADO → ENTREGUE
//                                                                         → CANCELADO
//
// ROTAS (todas exigem JWT):
// - GET    /api/pedidos        → lista pedidos (paginado; admin=todos, cliente=seus)
// - GET    /api/pedidos/{id}   → detalha um pedido
// - POST   /api/pedidos        → cria novo pedido (checkout)
// - PATCH  /api/pedidos/{id}/cancelar → cancela um pedido
// - PATCH  /api/pedidos/{id}/status  → atualiza status (admin)
//
// CONEXÕES: chama PedidoService para toda a lógica.
// ============================================================

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

// Comentário explicativo do fluxo de status — documentação para outros desenvolvedores
// Fluxo de status: AGUARDANDO_PAGAMENTO → CONFIRMADO → EM_SEPARACAO → ENVIADO → ENTREGUE | CANCELADO
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    // Serviço que contém toda a lógica de pedidos (criar, listar, cancelar, etc.)
    private final PedidoService pedidoService;

    // Injeção de Dependência via construtor
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // -------------------------------------------------------
    // GET /api/pedidos?status=&page=0&size=20
    // Lista pedidos com paginação e filtro opcional por status.
    //
    // CONTROLE DE ACESSO:
    //   - Admin: usuarioId = null → pedidoService retorna TODOS os pedidos
    //   - Cliente: usuarioId = ID do usuário logado → retorna apenas os seus
    //
    // @RequestParam(required = false): parâmetro opcional na URL.
    //   Ex: GET /api/pedidos?status=pendente&page=0&size=10
    // @RequestParam(defaultValue = "0"): valor padrão se não informado.
    //
    // PageResponse<PedidoResponse>: resposta paginada com metadados
    //   (total de páginas, total de itens, página atual, etc.)
    // -------------------------------------------------------
    /** Lista pedidos com paginação. Admins veem todos; clientes veem apenas os próprios. */
    @GetMapping
    public ResponseEntity<PageResponse<PedidoResponse>> listarPedidos(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Verifica se o usuário tem papel de admin
        boolean isAdmin = "admin".equals(ud.getUsuario().getPapel());

        // Admin passa null para ver todos os pedidos; cliente passa seu próprio ID
        Integer usuarioId = isAdmin ? null : ud.getUsuario().getId();

        return ResponseEntity.ok(pedidoService.listarPaginado(usuarioId, status, page, size));
    }

    // -------------------------------------------------------
    // GET /api/pedidos/{id}
    // Retorna todos os detalhes de um pedido específico,
    // incluindo os itens, produtos, endereço e valores.
    //
    // @PathVariable Long id: extrai o ID do pedido da URL.
    //   Ex: GET /api/pedidos/15 → id = 15
    // -------------------------------------------------------
    /** Retorna os detalhes de um pedido pelo ID. */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    // -------------------------------------------------------
    // POST /api/pedidos
    // Cria um novo pedido — é chamado ao finalizar o checkout.
    // O usuárioId é definido a partir do JWT (não do corpo do request),
    // garantindo que um usuário não possa criar pedidos em nome de outro.
    //
    // request.setUsuarioId(...): sobrescreve qualquer usuarioId que o
    // frontend tenha enviado com o ID real do usuário autenticado.
    // Isso é uma medida de segurança importante.
    // -------------------------------------------------------
    /** Cria um novo pedido (finalização do checkout). */
    @PostMapping
    public ResponseEntity<PedidoResponse> criarPedido(
            @AuthenticationPrincipal UsuarioDetails ud,
            @RequestBody CriarPedidoRequest request) {
        // Sobrescreve o usuarioId com o do JWT para evitar manipulação pelo frontend
        request.setUsuarioId((long) ud.getUsuario().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request));
    }

    // -------------------------------------------------------
    // PATCH /api/pedidos/{id}/cancelar
    // Muda o status do pedido para "cancelado".
    //
    // @PatchMapping: HTTP PATCH — atualização parcial de um recurso.
    //   Diferença de PUT (atualização completa) e PATCH (atualização parcial):
    //   PUT exige todos os campos; PATCH atualiza apenas o que foi enviado.
    //   Aqui não há corpo — o caminho "/cancelar" já comunica a intenção.
    // -------------------------------------------------------
    /** Cancela um pedido do usuário autenticado. */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelar(id);
        return ResponseEntity.ok().build(); // 200 OK sem corpo
    }

    // -------------------------------------------------------
    // PATCH /api/pedidos/{id}/status
    // Atualiza o status de um pedido para qualquer valor do fluxo.
    // Usado pelo painel administrativo para avançar o pedido no fluxo.
    //
    // Body esperado: { "status": "CONFIRMADO" }
    // AtualizarStatusRequest: DTO simples com apenas o campo "status".
    // -------------------------------------------------------
    /** Atualiza o status de um pedido. Body: { status }. Uso administrativo. */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatusPedido(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequest request) {
        pedidoService.atualizarStatus(id, request.getStatus()); // Delega a atualização para o serviço
        return ResponseEntity.ok().build(); // 200 OK
    }
}
