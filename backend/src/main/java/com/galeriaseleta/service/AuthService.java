package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.AuthRegisterRequest;
import com.galeriaseleta.model.Usuario;
import com.galeriaseleta.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return usuario;
    }

    public Usuario registrar(AuthRegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());

        return usuarioRepository.save(usuario);
    }

    public void logout() {
        // Sessão gerenciada pelo cliente; sem estado no servidor por enquanto
    }

    public Object refreshToken(String refreshToken) {
        throw new UnsupportedOperationException("JWT não implementado ainda");
    }

    public void esqueceuSenha(String email) {
        usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado"));
        // Envio de e-mail de recuperação será implementado com JWT
    }

    public void redefinirSenha(String token, String novaSenha) {
        throw new UnsupportedOperationException("Redefinição de senha requer JWT");
    }
}
