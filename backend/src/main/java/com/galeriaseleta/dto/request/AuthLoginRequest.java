// ============================================================
// ARQUIVO: AuthLoginRequest.java
// FUNÇÃO: DTO (Data Transfer Object) de entrada para o login.
//
// O QUE É UM DTO?
// É um objeto simples que carrega dados entre camadas do sistema.
// Ao contrário dos Models (que mapeiam tabelas do banco), os DTOs
// são usados apenas para transportar informações — sem lógica de negócio.
//
// FLUXO: Frontend envia JSON → Spring converte para AuthLoginRequest
// → AuthController recebe → AuthService valida → retorna AuthResponse
//
// EXEMPLO DE JSON recebido:
// { "email": "joao@email.com", "senha": "minhasenha123" }
// ============================================================

package com.galeriaseleta.dto.request;

// Classe simples sem anotações — só transporta os dados do formulário de login
public class AuthLoginRequest {

    private String email; // Email do usuário
    private String senha; // Senha digitada (texto puro — será validada com BCrypt)

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
