// ============================================================
// ARQUIVO: FotoProdutoRepository.java
// FUNÇÃO: Interface que acessa a tabela "fotos_produto" no banco.
// Permite buscar todas as fotos de um produto e encontrar
// a foto marcada como principal (capa do produto).
//
// CONEXÕES: usado pelo ProdutoService ao adicionar ou listar fotos de produto.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.FotoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FotoProdutoRepository extends JpaRepository<FotoProduto, Integer> {

    // Busca todas as fotos de um produto
    // → SELECT * FROM fotos_produto WHERE produto_id = ?
    List<FotoProduto> findByProdutoId(Integer produtoId);

    // Busca a foto marcada como principal de um produto
    // "PrincipalTrue" → WHERE principal = true
    // → SELECT * FROM fotos_produto WHERE produto_id = ? AND principal = true
    Optional<FotoProduto> findByProdutoIdAndPrincipalTrue(Integer produtoId);
}
