// ============================================================
// ARQUIVO: AuthService.java
// FUNÇÃO: Serviço responsável por toda a lógica de autenticação.
// Gerencia login, registro, logout, refresh de token e recuperação de senha.
//
// FLUXO DE LOGIN:
// 1. Recebe email e senha do controller
// 2. Busca o usuário no banco pelo email
// 3. Compara a senha enviada com a senha criptografada no banco (BCrypt)
// 4. Se válido, gera access token e refresh token
// 5. Retorna AuthResponse com os tokens e dados do usuário
//
// TOKENS DE RECUPERAÇÃO DE SENHA:
// Armazenados em ConcurrentHashMap (em memória).
// ConcurrentHashMap é thread-safe (seguro para múltiplos acessos simultâneos).
// LIMITAÇÃO: os tokens são perdidos ao reiniciar o servidor.
//
// CONEXÕES: chamado por AuthController para todos os endpoints /api/auth/*.
// ============================================================

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

import java.util.UUID; // Para gerar tokens únicos aleatórios
import java.util.concurrent.ConcurrentHashMap; // Mapa thread-safe para tokens temporários

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Para criptografar e verificar senhas
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Armazena temporariamente os tokens de recuperação de senha em memória.
    // Mapa: token (UUID) → email do usuário
    // ConcurrentHashMap: versão thread-safe do HashMap (seguro para múltiplos usuários simultâneos)
    private final ConcurrentHashMap<String, String> tokenRecuperacao = new ConcurrentHashMap<>();

    public AuthService(UsuarioRepository usuarioRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Realiza o login do usuário
    public AuthResponse login(String email, String senha) {
        // Busca o usuário pelo email — lança exceção genérica "Credenciais inválidas"
        // (não dizemos se o email ou senha é o problema, por segurança)
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        // BCrypt.matches(): compara a senha enviada (texto puro) com o hash armazenado
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // Gera e retorna os tokens JWT
        return buildAuthResponse(usuario);
    }

    // Registra um novo usuário e retorna tokens (auto-login após cadastro)
    public AuthResponse registrar(AuthRegisterRequest request) {
        // Verifica se o email já está em uso
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado: " + request.getEmail());
        }

        // Cria o novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        // encode(): transforma a senha em hash BCrypt antes de salvar
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(request.getCpf());

        // Salva no banco e imediatamente gera os tokens (auto-login)
        return buildAuthResponse(usuarioRepository.save(usuario));
    }

    // Logout: JWT é stateless, então apenas o frontend descarta o token.
    // O servidor não precisa fazer nada — por isso o método está vazio.
    public void logout() {
        // JWT é stateless; o cliente descarta o token localmente
    }

    // Renova o access token usando o refresh token
    public AuthResponse refreshToken(String refreshToken) {
        // Extrai o email do refresh token
        String email = jwtService.extrairEmail(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Valida se o refresh token ainda é válido
        if (!jwtService.validarToken(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gera um novo access token mas mantém o mesmo refresh token
        String newAccessToken = jwtService.gerarToken(usuario);
        return new AuthResponse(newAccessToken, refreshToken, UsuarioResponse.from(usuario));
    }

    // Inicia o processo de recuperação de senha
    // Gera um token único e o associa ao email do usuário em memória
    public String esqueceuSenha(String email) {
        // Verifica se o email existe no banco
        usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado"));

        // UUID.randomUUID(): gera um identificador único aleatório (ex: "f47ac10b-58cc-...")
        String token = UUID.randomUUID().toString();

        // Armazena o token → email para validar depois na redefinição
        tokenRecuperacao.put(token, email);

        return token; // Em produção: enviaria por email. Aqui: retornado na resposta.
    }

    // Redefine a senha usando o token de recuperação
    public void redefinirSenha(String token, String novaSenha) {
        // remove(): remove E retorna o valor (email) associado ao token.
        // Se o token não existir, retorna null (token inválido ou já usado).
        String email = tokenRecuperacao.remove(token);
        if (email == null) {
            throw new RuntimeException("Token inválido ou já utilizado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Criptografa a nova senha e salva
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    // Método privado auxiliar: gera os dois tokens e monta o AuthResponse
    private AuthResponse buildAuthResponse(Usuario usuario) {
        String accessToken = jwtService.gerarToken(usuario);
        String refresh = jwtService.gerarRefreshToken(usuario);
        return new AuthResponse(accessToken, refresh, UsuarioResponse.from(usuario));
    }
}
