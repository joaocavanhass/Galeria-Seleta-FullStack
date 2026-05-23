package com.galeriaseleta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.galeriaseleta.model.Pagamento;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    Optional<Pagamento> findByPedidoId(Integer pedidoId);
}
