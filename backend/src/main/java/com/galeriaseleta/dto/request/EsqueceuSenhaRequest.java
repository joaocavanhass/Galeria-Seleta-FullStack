// ============================================================
// ARQUIVO: EsqueceuSenhaRequest.java
// FUNÇÃO: DTO de entrada para iniciar o processo de recuperação de senha.
// O usuário informa o email e o sistema gera um token temporário.
//
// FLUXO: "Esqueci minha senha" → POST /api/auth/forgot-password
// → AuthController → AuthService → gera token de recuperação em memória
//
// EXEMPLO DE JSON recebido:
// { "email": "joao@email.com" }
// ============================================================

package com.galeriaseleta.dto.request;

public class EsqueceuSenhaRequest {

    private String email; // Email do usuário que esqueceu a senha

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
