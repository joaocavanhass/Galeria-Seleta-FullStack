// ============================================================
// ARQUIVO: AuthResponse.java
// FUNÇÃO: DTO de saída retornado após login ou cadastro bem-sucedido.
// Contém os dois tokens JWT e os dados básicos do usuário logado.
//
// O FRONTEND usa este retorno para:
// 1. Salvar accessToken no localStorage (usado em todas as próximas requisições)
// 2. Salvar refreshToken no localStorage (usado para renovar o accessToken quando expirar)
// 3. Salvar os dados do usuário para exibir no header (nome, papel, etc.)
//
// EXEMPLO DE JSON retornado:
// {
//   "accessToken": "eyJhbGci...",
//   "refreshToken": "eyJhbGci...",
//   "usuario": { "id": 1, "nome": "João", "email": "...", "papel": "cliente" }
// }
// ============================================================

package com.galeriaseleta.dto.response;

public class AuthResponse {

    private String accessToken;      // Token de acesso — válido por 24 horas
    private String refreshToken;     // Token de renovação — válido por 7 dias
    private UsuarioResponse usuario; // Dados básicos do usuário logado

    // Construtor: todos os campos são obrigatórios
    public AuthResponse(String accessToken, String refreshToken, UsuarioResponse usuario) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.usuario = usuario;
    }

    // Apenas getters — AuthResponse é imutável após ser criado
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public UsuarioResponse getUsuario() { return usuario; }
}
