// ============================================================
// ARQUIVO: OpcaoFreteRepository.java
// FUNÇÃO: Interface que acessa a tabela "opcoes_frete" no banco.
// Não define métodos extras porque as operações básicas do JpaRepository
// (findAll, findById, save, delete, count) são suficientes para gerenciar fretes.
//
// CONEXÕES: usado pelo AdminInitializer (cria fretes iniciais)
// e pelo FreteController (lista as opções disponíveis).
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.OpcaoFrete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// JpaRepository já fornece: findAll(), findById(), save(), delete(), count(), etc.
// Não são necessários métodos adicionais para este repositório.
public interface OpcaoFreteRepository extends JpaRepository<OpcaoFrete, Integer> {
}
