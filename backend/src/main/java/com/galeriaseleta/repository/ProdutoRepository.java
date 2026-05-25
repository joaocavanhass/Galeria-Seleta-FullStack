package com.galeriaseleta.repository;

import com.galeriaseleta.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByCategoriaId(Integer categoriaId);
    List<Produto> findByStatus(String status);
    Page<Produto> findByStatus(String status, Pageable pageable);
    Page<Produto> findByCategoriaId(Integer categoriaId, Pageable pageable);
    List<Produto> findByNovidade(Boolean novidade);
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT p FROM Produto p ORDER BY p.preco ASC")
    List<Produto> findAllOrderByPrecoAsc();

    @Query("SELECT p FROM Produto p ORDER BY p.preco DESC")
    List<Produto> findAllOrderByPrecoDesc();
}
