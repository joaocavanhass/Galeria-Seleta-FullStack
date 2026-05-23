package com.galeriaseleta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.galeriaseleta.model.Cupom;

import java.util.Optional;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Integer> {
    Optional<Cupom> findByCodigo(String codigo);
}
