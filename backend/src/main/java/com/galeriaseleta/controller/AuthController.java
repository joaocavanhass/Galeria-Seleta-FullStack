package com.galeriaseleta.controller;

import com.galeriaseleta.dto.request.AuthLoginRequest;
import com.galeriaseleta.dto.request.AuthRegisterRequest;
import com.galeriaseleta.dto.request.EsqueceuSenhaRequest;
import com.galeriaseleta.dto.request.RedefinirSenhaRequest;
import com.galeriaseleta.dto.response.UsuarioResponse;
import com.galeriaseleta.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Autentica o usuário com e-mail e senha. */
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(UsuarioResponse.from(authService.login(request.getEmail(), request.getSenha())));
    }

    /** Registra um novo usuário. */
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody AuthRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(authService.registrar(request)));
    }

    /** Encerra a sessão do usuário. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    /** Renova o token de acesso. Body: { refreshToken }. */
    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(authService.refreshToken(body.get("refreshToken")));
    }

    /** Inicia o fluxo de recuperação de senha. */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> esqueceuSenha(@RequestBody EsqueceuSenhaRequest request) {
        authService.esqueceuSenha(request.getEmail());
        return ResponseEntity.ok().build();
    }

    /** Redefine a senha com o token de recuperação. */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> redefinirSenha(@RequestBody RedefinirSenhaRequest request) {
        authService.redefinirSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok().build();
    }
}
