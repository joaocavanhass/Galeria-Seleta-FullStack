package com.galeriaseleta.dto.response;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private UsuarioResponse usuario;

    public AuthResponse(String accessToken, String refreshToken, UsuarioResponse usuario) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.usuario = usuario;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public UsuarioResponse getUsuario() { return usuario; }
}
