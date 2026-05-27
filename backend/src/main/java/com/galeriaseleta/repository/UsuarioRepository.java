// ============================================================
// ARQUIVO: UsuarioRepository.java
// FUNÇÃO: Interface que permite acessar a tabela "usuarios" no banco.
// Ao estender JpaRepository, ganhamos automaticamente dezenas de métodos
// prontos para usar: save(), findById(), findAll(), delete(), count(), etc.
// Não precisamos escrever SQL para operações básicas — o Spring Data JPA
// gera as queries automaticamente com base nos nomes dos métodos.
//
// COMO FUNCIONA O JpaRepository:
// JpaRepository<Usuario, Integer> significa:
// - Usuario: o tipo de objeto que este repositório gerencia
// - Integer: o tipo do ID (chave primária) do Usuario
//
// CONEXÕES: usado por AuthService (login, registro) e UsuarioService (perfil, admin).
// ============================================================

package com.galeriaseleta.repository;

import com.galeriaseleta.model.Usuario;
// JpaRepository: interface do Spring Data JPA com métodos CRUD prontos
import org.springframework.data.jpa.repository.JpaRepository;
// @Repository: marca esta interface como um componente de acesso a dados
import org.springframework.stereotype.Repository;

import java.util.Optional;
// Optional<T>: um container que pode ou não conter um valor.
// Usado para evitar NullPointerException — você verifica se o valor existe
// antes de usá-lo. Ex: findByEmail() pode não encontrar ninguém.

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // O Spring Data JPA lê o nome do método e gera a query SQL automaticamente:
    // "findBy" + "Email" → SELECT * FROM usuarios WHERE email = ?
    // O retorno é Optional porque pode não existir nenhum usuário com esse email.
    Optional<Usuario> findByEmail(String email);
}
