// ============================================================
// ARQUIVO: UserDetailsServiceImpl.java
// FUNÇÃO: Implementa a interface UserDetailsService do Spring Security.
// É responsável por buscar o usuário no banco de dados pelo email,
// quando o Spring Security precisa verificar quem está se autenticando.
//
// FLUXO:
// JwtAuthenticationFilter extrai o email do token →
// chama userDetailsService.loadUserByUsername(email) →
// este serviço busca o usuário no banco →
// retorna UsuarioDetails (o adaptador do Spring Security)
//
// CONEXÕES: chamado pelo JwtAuthenticationFilter a cada requisição autenticada.
// ============================================================

package com.galeriaseleta.security;

import com.galeriaseleta.model.Usuario;
import com.galeriaseleta.repository.UsuarioRepository;
// UserDetails: interface que o Spring Security usa para representar o usuário
import org.springframework.security.core.userdetails.UserDetails;
// UserDetailsService: interface que define o método loadUserByUsername()
import org.springframework.security.core.userdetails.UserDetailsService;
// UsernameNotFoundException: exceção padrão do Spring quando usuário não é encontrado
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Chamado pelo Spring Security quando precisa carregar um usuário.
    // O parâmetro "username" no nosso sistema é na verdade o EMAIL do usuário.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário no banco pelo email.
        // orElseThrow: se não encontrar, lança UsernameNotFoundException
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Envolve o Usuario em um UsuarioDetails (adaptador para o Spring Security)
        return new UsuarioDetails(usuario);
    }
}
