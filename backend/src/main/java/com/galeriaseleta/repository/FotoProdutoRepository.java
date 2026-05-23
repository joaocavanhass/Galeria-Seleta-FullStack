package com.galeriaseleta.repository;

import com.galeriaseleta.model.FotoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FotoProdutoRepository extends JpaRepository<FotoProduto, Integer> {
    List<FotoProduto> findByProdutoId(Integer produtoId);
    Optional<FotoProduto> findByProdutoIdAndPrincipalTrue(Integer produtoId);
}
