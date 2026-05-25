package com.galeriaseleta.repository;

import com.galeriaseleta.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUsuarioId(Integer usuarioId);
    List<Pedido> findByUsuarioIdAndStatus(Integer usuarioId, String status);
    List<Pedido> findByStatus(String status);
    Page<Pedido> findByUsuarioId(Integer usuarioId, Pageable pageable);
    Page<Pedido> findByUsuarioIdAndStatus(Integer usuarioId, String status, Pageable pageable);
    Page<Pedido> findByStatus(String status, Pageable pageable);
}
