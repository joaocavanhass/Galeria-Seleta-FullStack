// ============================================================
// ARQUIVO: EnderecoRepository.java
// FUNÇÃO: Interface que acessa a tabela "enderecos" no banco.
// Permite listar todos os endereços de um usuário e encontrar
// o endereço marcado como principal (padrão).
//
// CONEXÕES: usado pelo UsuarioService para gerenciar endereços
// e pelo PedidoService para pegar o endereço padrão no checkout.
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

    // Lista todos os endereços de um usuário
    // → SELECT * FROM enderecos WHERE usuario_id = ?
    List<Endereco> findByUsuarioId(Integer usuarioId);

    // Busca o endereço marcado como principal de um usuário
    // "PrincipalTrue" é interpretado pelo Spring como "WHERE principal = true"
    // → SELECT * FROM enderecos WHERE usuario_id = ? AND principal = true
    Optional<Endereco> findByUsuarioIdAndPrincipalTrue(Integer usuarioId);
}
