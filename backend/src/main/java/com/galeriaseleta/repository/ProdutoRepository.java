// ============================================================
// ARQUIVO: ProdutoRepository.java
// FUNÇÃO: Interface que acessa a tabela "produtos" no banco.
// Além dos métodos padrão do JpaRepository, define consultas
// personalizadas para filtrar e ordenar produtos de formas específicas.
//
// NOMENCLATURA DOS MÉTODOS (Spring Data JPA):
// - findBy[Campo]: gera SELECT ... WHERE campo = ?
// - findBy[Campo]Containing: gera SELECT ... WHERE campo LIKE %?%
// - findBy[Campo]IgnoreCase: ignora maiúsculas/minúsculas na busca
// - Page<T> + Pageable: retorna resultados paginados (ex: página 1, 10 itens)
// - @Query: permite escrever JPQL (Java Query Language) diretamente
//
// CONEXÕES: usado pelo ProdutoService para todas as operações de produto.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Produto;
import org.springframework.data.domain.Page;       // Representa uma página de resultados
import org.springframework.data.domain.Pageable;   // Contém as informações de paginação (página, tamanho)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Para queries JPQL customizadas
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    // Busca todos os produtos de uma categoria específica
    // → SELECT * FROM produtos WHERE categoria_id = ?
    List<Produto> findByCategoriaId(Integer categoriaId);

    // Busca produtos por status ("disponivel" ou "indisponivel")
    // → SELECT * FROM produtos WHERE status = ?
    List<Produto> findByStatus(String status);

    // Versão paginada: mesma busca por status, mas retorna Page (com controle de página)
    Page<Produto> findByStatus(String status, Pageable pageable);

    // Versão paginada da busca por categoria
    Page<Produto> findByCategoriaId(Integer categoriaId, Pageable pageable);

    // Busca produtos marcados como novidade (novidade = true)
    // → SELECT * FROM produtos WHERE novidade = ?
    List<Produto> findByNovidade(Boolean novidade);

    // Busca produtos cujo nome contém o termo digitado (busca parcial, sem distinção de maiúsculas)
    // → SELECT * FROM produtos WHERE LOWER(nome) LIKE %termo%
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // @Query: query JPQL manual para ordenar por preço crescente
    // JPQL usa nomes de classes e campos Java, não nomes de tabelas SQL
    // "p" é um alias para Produto — equivale a "SELECT * FROM produtos ORDER BY preco ASC"
    @Query("SELECT p FROM Produto p ORDER BY p.preco ASC")
    List<Produto> findAllOrderByPrecoAsc();

    // Mesma query, mas ordenando por preço decrescente (mais caro primeiro)
    @Query("SELECT p FROM Produto p ORDER BY p.preco DESC")
    List<Produto> findAllOrderByPrecoDesc();
}
