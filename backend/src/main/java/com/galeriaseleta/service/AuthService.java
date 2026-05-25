package com.galeriaseleta.service;

import com.galeriaseleta.dto.request.AuthRegisterRequest;
import com.galeriaseleta.dto.response.AuthResponse;
import com.galeriaseleta.dto.response.UsuarioResponse;
import com.galeriaseleta.model.Usuario;
import com.galeriaseleta.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthService(UsuarioRepository usuarioRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return buildAuthResponse(usuario);
    }

    public AuthResponse registrar(AuthRegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(request.getCpf());

        return buildAuthResponse(usuarioRepository.save(usuario));
    }

    public void logout() {
        // JWT é stateless; o cliente descarta o token localmente
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extrairEmail(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.validarToken(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String newAccessToken = jwtService.gerarToken(usuario);
        return new AuthResponse(newAccessToken, refreshToken, UsuarioResponse.from(usuario));
    }

    public void esqueceuSenha(String email) {
        usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado"));
        // TODO: enviar e-mail com link de recuperação
    }

    public void redefinirSenha(String token, String novaSenha) {
        throw new UnsupportedOperationException("Redefinição de senha por e-mail não implementada");
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        String accessToken = jwtService.gerarToken(usuario);
        String refresh = jwtService.gerarRefreshToken(usuario);
        return new AuthResponse(accessToken, refresh, UsuarioResponse.from(usuario));
    }
}
