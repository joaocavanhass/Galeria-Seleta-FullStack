// ============================================================
// ARQUIVO: RedefinirSenhaRequest.java
// FUNÇÃO: DTO de entrada para redefinir a senha com o token de recuperação.
// O usuário recebe o token por email e o usa junto com a nova senha.
//
// FLUXO: Link de recuperação → POST /api/auth/reset-password
// → AuthController → AuthService → valida token → salva nova senha criptografada
//
// EXEMPLO DE JSON recebido:
// { "token": "abc123xyz", "novaSenha": "minhaNovasenha456" }
// ============================================================

package com.galeriaseleta.dto.request;

public class RedefinirSenhaRequest {

    private String token;      // Token de recuperação gerado pelo forgot-password
    private String novaSenha;  // Nova senha escolhida pelo usuário

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
}
