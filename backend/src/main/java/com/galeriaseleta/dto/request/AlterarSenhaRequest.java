// ============================================================
// ARQUIVO: AlterarSenhaRequest.java
// FUNÇÃO: DTO de entrada para o usuário alterar sua própria senha.
// Exige a senha atual para confirmar a identidade antes de trocar.
//
// FLUXO: Perfil → PATCH /api/usuarios/me/senha
// → UsuarioController → UsuarioService → valida senha atual → salva nova senha criptografada
//
// EXEMPLO DE JSON recebido:
// { "senhaAtual": "senha123", "novaSenha": "novasenha456" }
// ============================================================

package com.galeriaseleta.dto.request;

public class AlterarSenhaRequest {

    private String senhaAtual; // Senha atual para confirmar identidade
    private String novaSenha;  // Nova senha escolhida (será criptografada)

    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }

    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
}
