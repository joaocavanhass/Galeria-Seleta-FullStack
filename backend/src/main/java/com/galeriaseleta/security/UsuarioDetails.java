// ============================================================
// ARQUIVO: UsuarioDetails.java
// FUNÇÃO: Adapta o model Usuario para a interface UserDetails do Spring Security.
//
// O PROBLEMA:
// O Spring Security não conhece a nossa classe Usuario.
// Ele trabalha com a interface UserDetails — um contrato que define
// o que um usuário autenticado precisa fornecer (username, password, roles, etc.)
//
// A SOLUÇÃO:
// UsuarioDetails é um "adaptador" (padrão Adapter) que envolve nosso Usuario
// e implementa o que o Spring Security precisa.
//
// ANALOGIA: é como um adaptador de tomada — o Spring Security tem uma
// "tomada" no formato UserDetails, e nosso Usuario é uma "tomada" diferente.
// UsuarioDetails faz a conexão entre os dois.
//
// CONEXÕES: criado por UserDetailsServiceImpl e usado por JwtAuthenticationFilter.
// ============================================================

package com.galeriaseleta.security;

import com.galeriaseleta.model.Usuario;
// GrantedAuthority: representa uma permissão/papel do usuário no Spring Security
import org.springframework.security.core.GrantedAuthority;
// UserDetails: interface que o Spring Security exige para representar um usuário
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// implements UserDetails: esta classe assina o contrato do Spring Security
public class UsuarioDetails implements UserDetails {

    // O nosso model Usuario — guardamos a referência para delegar as informações
    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    // Retorna o nosso model Usuario original (para acessar id, papel, etc.)
    public Usuario getUsuario() {
        return usuario;
    }

    // getAuthorities(): retorna as permissões/papéis do usuário.
    // Por enquanto retorna lista vazia — as verificações de papel (admin/cliente)
    // são feitas manualmente nos controllers via usuario.getPapel()
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    // getPassword(): retorna a senha criptografada (usada pelo Spring para validar login)
    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    // getUsername(): o Spring Security usa o "username" como identificador único.
    // No nosso sistema, usamos o EMAIL como identificador (não um username textual).
    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    // Métodos de status da conta — todos retornam true (conta sempre ativa)
    // Poderíamos usar esses métodos para implementar bloqueio de conta no futuro
    @Override public boolean isAccountNonExpired()    { return true; } // Conta não expirada
    @Override public boolean isAccountNonLocked()     { return true; } // Conta não bloqueada
    @Override public boolean isCredentialsNonExpired(){ return true; } // Credenciais não expiradas
    @Override public boolean isEnabled()              { return true; } // Conta habilitada
}
