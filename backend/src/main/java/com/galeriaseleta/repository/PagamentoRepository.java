// ============================================================
// ARQUIVO: PagamentoRepository.java
// FUNÇÃO: Interface que acessa a tabela "pagamentos" no banco.
// Permite buscar o pagamento associado a um pedido específico.
//
// CONEXÕES: usado pelo PedidoService ao registrar e consultar pagamentos.
// ============================================================

package com.galeriaseleta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.galeriaseleta.model.Pagamento;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

    // Busca o pagamento de um pedido pelo ID do pedido
    // → SELECT * FROM pagamentos WHERE pedido_id = ?
    // Retorna Optional porque pode ser que o pedido ainda não tenha pagamento registrado
    Optional<Pagamento> findByPedidoId(Integer pedidoId);
}
