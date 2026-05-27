// ============================================================
// ARQUIVO: ItemPedidoRepository.java
// FUNÇÃO: Interface que acessa a tabela "itens_pedido" no banco.
// Permite buscar todos os itens que pertencem a um pedido específico.
//
// CONEXÕES: usado pelo PedidoService para montar o detalhamento de um pedido
// (quais produtos foram comprados, em quais quantidades e a qual preço).
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {

    // Busca todos os itens de um pedido específico
    // → SELECT * FROM itens_pedido WHERE pedido_id = ?
    List<ItemPedido> findByPedidoId(Integer pedidoId);
}
