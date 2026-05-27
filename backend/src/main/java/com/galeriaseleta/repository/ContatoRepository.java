// ============================================================
// ARQUIVO: ContatoRepository.java
// FUNÇÃO: Interface que acessa a tabela "contatos" no banco.
// Não precisa de métodos extras — apenas salvar as mensagens recebidas
// usando o save() herdado do JpaRepository é suficiente.
//
// CONEXÕES: usado pelo ContatoService ao salvar mensagens do formulário.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Os métodos herdados do JpaRepository (save, findAll, findById, etc.)
// são tudo que precisamos para armazenar mensagens de contato.
public interface ContatoRepository extends JpaRepository<Contato, Integer> {
}
