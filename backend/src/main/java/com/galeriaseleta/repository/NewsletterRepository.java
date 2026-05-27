// ============================================================
// ARQUIVO: NewsletterRepository.java
// FUNÇÃO: Interface que acessa a tabela "newsletter" no banco.
// Permite verificar se um email já está inscrito antes de tentar
// inscrever novamente (evitando duplicatas).
//
// CONEXÕES: usado pelo ContatoService para gerenciar inscrições.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Integer> {

    // Busca uma inscrição pelo email
    // Usado para verificar se o email já existe antes de inscrever,
    // e para encontrar a inscrição ao cancelar.
    // → SELECT * FROM newsletter WHERE email = ?
    Optional<Newsletter> findByEmail(String email);
}
