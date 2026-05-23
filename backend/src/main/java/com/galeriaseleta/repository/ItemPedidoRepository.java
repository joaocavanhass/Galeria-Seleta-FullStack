package com.galeriaseleta.repository;

import com.galeriaseleta.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
    List<ItemPedido> findByPedidoId(Integer pedidoId);
}
