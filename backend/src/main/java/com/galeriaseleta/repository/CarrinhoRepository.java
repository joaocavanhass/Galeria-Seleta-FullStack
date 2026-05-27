// ============================================================
// ARQUIVO: CarrinhoRepository.java
// FUNÇÃO: Interface que acessa a tabela "carrinho" no banco.
// Permite buscar os itens do carrinho de um usuário específico,
// e verificar se um produto já está no carrinho (para incrementar
// a quantidade em vez de criar um item duplicado).
//
// CONEXÕES: usado pelo CarrinhoService para gerenciar o carrinho.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {

    // Retorna todos os itens do carrinho de um usuário
    // → SELECT * FROM carrinho WHERE usuario_id = ?
    List<Carrinho> findByUsuarioId(Integer usuarioId);

    // Verifica se um produto específico já está no carrinho de um usuário.
    // Usado para evitar duplicatas: se já existe, incrementa a quantidade.
    // → SELECT * FROM carrinho WHERE usuario_id = ? AND produto_id = ?
    Optional<Carrinho> findByUsuarioIdAndProdutoId(Integer usuarioId, Integer produtoId);
}
