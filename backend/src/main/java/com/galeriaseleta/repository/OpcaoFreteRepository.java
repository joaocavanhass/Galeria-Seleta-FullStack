package com.galeriaseleta.repository;

import com.galeriaseleta.model.OpcaoFrete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpcaoFreteRepository extends JpaRepository<OpcaoFrete, Integer> {
}
