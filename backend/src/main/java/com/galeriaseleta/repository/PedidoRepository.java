// ============================================================
// ARQUIVO: PedidoRepository.java
// FUNÇÃO: Interface que acessa a tabela "pedidos" no banco.
// Oferece métodos para listar pedidos por usuário, por status,
// ou pela combinação de ambos. Suporta paginação.
//
// REGRA DE NEGÓCIO:
// - Cliente vê apenas seus próprios pedidos (findByUsuarioId)
// - Admin vê todos os pedidos (findAll — herdado do JpaRepository)
//
// CONEXÕES: usado pelo PedidoService para listar e filtrar pedidos.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Lista todos os pedidos de um usuário (sem filtro de status)
    List<Pedido> findByUsuarioId(Integer usuarioId);

    // Lista pedidos de um usuário filtrados por status
    // Ex: buscar apenas pedidos "enviados" de um cliente específico
    List<Pedido> findByUsuarioIdAndStatus(Integer usuarioId, String status);

    // Lista todos os pedidos de qualquer usuário com um status específico
    // Usado pelo admin para filtrar pedidos por status
    List<Pedido> findByStatus(String status);

    // Versão paginada: pedidos de um usuário com paginação
    Page<Pedido> findByUsuarioId(Integer usuarioId, Pageable pageable);

    // Versão paginada: pedidos de um usuário filtrados por status com paginação
    Page<Pedido> findByUsuarioIdAndStatus(Integer usuarioId, String status, Pageable pageable);

    // Versão paginada: todos os pedidos com um status específico
    Page<Pedido> findByStatus(String status, Pageable pageable);
}
