package com.galeriaseleta.controller;

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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Autentica o usuário e retorna access + refresh token. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getSenha()));
    }

    /** Registra novo usuário e já retorna os tokens (auto-login). */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@RequestBody AuthRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    /** Encerra a sessão — JWT é stateless, o cliente descarta o token. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    /** Renova o access token usando o refresh token. Body: { "refreshToken": "..." }. */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refreshToken(body.get("refreshToken")));
    }

    /** Inicia o fluxo de recuperação de senha por e-mail. */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> esqueceuSenha(@RequestBody EsqueceuSenhaRequest request) {
        authService.esqueceuSenha(request.getEmail());
        return ResponseEntity.ok().build();
    }

    /** Redefine a senha com o token de recuperação enviado por e-mail. */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> redefinirSenha(@RequestBody RedefinirSenhaRequest request) {
        authService.redefinirSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok().build();
    }
}
