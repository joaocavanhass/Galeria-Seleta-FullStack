// ============================================================
// ARQUIVO: CategoriaRepository.java
// FUNÇÃO: Interface que acessa a tabela "categorias" no banco.
// Permite buscar categorias ativas e encontrar uma categoria
// pelo seu slug (nome na URL).
//
// CONEXÕES: usado pelo CategoriaService para listar e gerenciar categorias.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Lista apenas categorias ativas (ativo = true)
    // → SELECT * FROM categorias WHERE ativo = ?
    List<Categoria> findByAtivo(Boolean ativo);

    // Busca uma categoria pelo seu slug (nomeUrl)
    // Ex: findByNomeUrl("camisetas") → categoria com nomeUrl = "camisetas"
    // Útil para buscas por URL sem usar o ID
    Optional<Categoria> findByNomeUrl(String nomeUrl);
}
