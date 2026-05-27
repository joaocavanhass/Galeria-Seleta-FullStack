// ============================================================
// ARQUIVO: CupomRepository.java
// FUNÇÃO: Interface que acessa a tabela "cupons" no banco.
// O principal uso é buscar um cupom pelo código que o cliente digitou.
//
// CONEXÕES: usado pelo CupomController para validar o cupom no checkout.
// ============================================================

package com.galeriaseleta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.galeriaseleta.model.Cupom;

import java.util.Optional;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Integer> {

    // Busca um cupom pelo código digitado pelo cliente
    // Ex: findByCodigo("DESCONTO10") → cupom com codigo = "DESCONTO10"
    // → SELECT * FROM cupons WHERE codigo = ?
    // Retorna Optional porque o código pode não existir no banco
    Optional<Cupom> findByCodigo(String codigo);
}
