// ============================================================
// ARQUIVO: AtualizarPerfilRequest.java
// FUNÇÃO: DTO de entrada para o usuário atualizar seus dados de perfil.
// Nota: email e senha têm endpoints próprios, então não estão aqui.
//
// FLUXO: Perfil → PUT /api/usuarios/me
// → UsuarioController → UsuarioService → atualiza no banco
//
// EXEMPLO DE JSON recebido:
// { "nome": "João Silva", "telefone": "(11) 99999-9999", "cpf": "123.456.789-00" }
// ============================================================

package com.galeriaseleta.dto.request;

public class AtualizarPerfilRequest {

    private String nome;      // Novo nome do usuário
    private String telefone;  // Novo telefone
    private String cpf;       // CPF (pode ser atualizado se não foi cadastrado antes)

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
