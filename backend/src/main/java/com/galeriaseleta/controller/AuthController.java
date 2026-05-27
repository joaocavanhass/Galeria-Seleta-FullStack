// ============================================================
// ARQUIVO: AuthController.java
// FUNÇÃO: Controlador das rotas de autenticação (/api/auth).
//
// O QUE É UM CONTROLADOR (Controller)?
// É a "porta de entrada" da API. Quando o frontend faz uma requisição
// HTTP (ex: POST /api/auth/login), o Spring chama o método correspondente
// nesta classe. O controlador recebe os dados, delega a lógica para o
// serviço (AuthService) e devolve uma resposta HTTP ao cliente.
//
// ROTAS PÚBLICAS (não precisam de JWT):
// - POST /api/auth/login           → faz login
// - POST /api/auth/register        → cria nova conta
// - POST /api/auth/logout          → encerra sessão
// - POST /api/auth/refresh         → renova o access token
// - POST /api/auth/forgot-password → solicita recuperação de senha
// - POST /api/auth/reset-password  → redefine a senha com o token
//
// CONEXÕES: chama AuthService para toda a lógica de negócio.
// ============================================================

package com.galeriaseleta.controller;

// Importa as classes necessárias para receber dados, retornar respostas e chamar o serviço
import com.galeriaseleta.dto.request.AuthLoginRequest;
import com.galeriaseleta.dto.request.AuthRegisterRequest;
import com.galeriaseleta.dto.request.EsqueceuSenhaRequest;
import com.galeriaseleta.dto.request.RedefinirSenhaRequest;
import com.galeriaseleta.dto.response.AuthResponse;
import com.galeriaseleta.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// @RestController: diz ao Spring que esta classe é um controlador REST.
// Isso significa que os métodos retornam dados (JSON) diretamente, não páginas HTML.
// @RequestMapping("/api/auth"): define o prefixo comum de todas as rotas desta classe.
// Todos os endpoints aqui começam com /api/auth.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Injeta o serviço de autenticação — ele contém a lógica real (validar senha, gerar token, etc.)
    private final AuthService authService;

    // Construtor: o Spring injeta o AuthService automaticamente (Injeção de Dependência)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // -------------------------------------------------------
    // POST /api/auth/login
    // Recebe email e senha, valida e retorna os tokens JWT.
    //
    // @PostMapping("/login"): esta rota responde a requisições HTTP POST.
    // @RequestBody: o Spring lê o JSON enviado no corpo da requisição
    //               e converte automaticamente para o objeto AuthLoginRequest.
    // ResponseEntity<AuthResponse>: o retorno contém o corpo (AuthResponse)
    //                               e o status HTTP (200 OK por padrão com .ok()).
    // -------------------------------------------------------
    /** Autentica o usuário e retorna access + refresh token. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest request) {
        // Delega a autenticação para o serviço e retorna 200 OK com os tokens
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getSenha()));
    }

    // -------------------------------------------------------
    // POST /api/auth/register
    // Cria uma nova conta e já retorna os tokens (auto-login após registro).
    //
    // HttpStatus.CREATED (201): indica que um novo recurso foi criado.
    // É a boa prática para rotas de cadastro (POST que cria algo novo).
    // -------------------------------------------------------
    /** Registra novo usuário e já retorna os tokens (auto-login). */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@RequestBody AuthRegisterRequest request) {
        // Retorna 201 CREATED com os tokens, indicando que a conta foi criada
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    // -------------------------------------------------------
    // POST /api/auth/logout
    // Encerra a sessão — como o JWT é stateless (sem estado no servidor),
    // o "logout" real acontece no frontend descartando o token salvo.
    // O servidor apenas registra a intenção de logout.
    //
    // ResponseEntity<Void>: quando não há corpo na resposta, usamos Void.
    // .noContent().build(): retorna 204 No Content (sucesso sem corpo).
    // -------------------------------------------------------
    /** Encerra a sessão — JWT é stateless, o cliente descarta o token. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout(); // Não faz nada de relevante (JWT é stateless)
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // -------------------------------------------------------
    // POST /api/auth/refresh
    // Renova o access token usando o refresh token.
    // O corpo esperado é: { "refreshToken": "eyJ..." }
    //
    // Map<String, String>: quando o JSON não tem uma classe DTO específica,
    // podemos receber como um Map (dicionário de chave → valor).
    // body.get("refreshToken"): extrai o valor da chave "refreshToken" do JSON.
    // -------------------------------------------------------
    /** Renova o access token usando o refresh token. Body: { "refreshToken": "..." }. */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refreshToken(body.get("refreshToken")));
    }

    // -------------------------------------------------------
    // POST /api/auth/forgot-password
    // Solicita recuperação de senha. Recebe o email e gera um token
    // de recuperação (UUID único). Em produção, seria enviado por email;
    // aqui é retornado diretamente no corpo da resposta para facilitar testes.
    //
    // Map.of("token", token): cria um Map imutável com apenas uma entrada.
    // O JSON de resposta será: { "token": "abc123..." }
    // -------------------------------------------------------
    /** Gera token de recuperação de senha. Retorna o token no corpo (sem e-mail real). */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> esqueceuSenha(@RequestBody EsqueceuSenhaRequest request) {
        String token = authService.esqueceuSenha(request.getEmail()); // Gera e armazena o token
        return ResponseEntity.ok(Map.of("token", token)); // Retorna o token para o frontend
    }

    // -------------------------------------------------------
    // POST /api/auth/reset-password
    // Redefine a senha usando o token recebido em forgot-password.
    // Após redefinir, o token é invalidado (não pode ser reutilizado).
    //
    // ResponseEntity.ok().build(): retorna 200 OK sem corpo (sucesso confirmado).
    // -------------------------------------------------------
    /** Redefine a senha com o token de recuperação enviado por e-mail. */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> redefinirSenha(@RequestBody RedefinirSenhaRequest request) {
        authService.redefinirSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok().build(); // 200 OK sem corpo
    }
}
